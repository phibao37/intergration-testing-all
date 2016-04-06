package core.solver;

import java.util.ArrayList;
import java.util.List;

import api.expression.IArrayIndexExpression;
import api.expression.IBinaryExpression;
import api.expression.IDeclareExpression;
import api.expression.IExpression;
import api.expression.IExpressionGroup;
import api.expression.IMemberAccessExpression;
import api.expression.INameExpression;
import api.expression.INumberExpression;
import api.expression.IReturnExpression;
import api.expression.IUnaryExpression;
import api.models.ITestpath;
import api.models.IStatement;
import api.models.IType;
import api.models.IVariable;
import api.parser.ISymbolicExecutor;
import api.solver.IPathConstraints;
import api.solver.IVariableTable;
import core.Utils;
import core.expression.BinaryExpression;
import core.expression.ExpressionVisitor;
import core.expression.NameExpression;
import core.expression.NumberExpression;
import core.expression.UnaryExpression;
import core.models.ArrayVariable;
import core.models.Variable;
import core.models.statement.ScopeStatement;
import core.models.type.ArrayType;
import core.models.type.BasicType;

public class SymbolicExecutor extends ExpressionVisitor
		implements ISymbolicExecutor {

	private boolean parseError;
	private IVariableTable varTable;
	private IPathConstraints constraint;
	private List<IPathConstraints> listConstraint;
	
	private int iter;
	private ITestpath path;
	private IExpressionGroup rootgroup;
	
	@Override
	public List<IPathConstraints> execPath(ITestpath path, IVariable[] params,
			int options) {
		
		boolean parseError = Utils.hasFlag(options, PARSE_ERROR_PATH);
		varTable = createVariableTable();
		constraint = new PathConstraints(params, path);
		listConstraint = new ArrayList<>();
		this.path = path;
		listConstraint.add(constraint);
		
		//Thêm các biến toàn cục sau !!!
		varTable.increaseScope();
		
		//Cho bản sao của các tham số vào bảng biến
		for (IVariable param: params)
			varTable.addVariable(param.clone());
		
		//Duyệt qua các câu lệnh trong đường thi hành
		for (iter = 0; iter < path.size(); iter++){
			IStatement stm = path.get(iter);
			
			if (stm.isNormal()){
				IExpression root = stm.getRoot();

				if (!stm.isVisited()){
					this.parseError = parseError;
					varTable.injectTypeExpression(root);
				}
				else
					this.parseError = false;
				
				root = root.clone();
				rootgroup = root.group();
				rootgroup.accept(this);
				root = rootgroup.ungroup();
				
				//Xử lý nhánh quyết định
				if (stm.isCondition()){
					boolean falseBranch = stm.getFalse() == path.get(iter + 1);
					root = varTable.fill(root);
					if (falseBranch)
						root = new UnaryExpression(
								UnaryExpression.LOGIC_NOT, root);
					constraint.addLogicConstraint(root);
				}
				
				stm.setVisit(true);
			}
			
			//Thay đổi scope khi gặp các dấu ngoặc {}
			else if (stm instanceof ScopeStatement){
				ScopeStatement scope = (ScopeStatement) stm;
				if (scope.isOpenScope())
					varTable.increaseScope();
				else
					varTable.decreaseScope();
			}
		}
		
		return listConstraint;
	}
	
	@Override
	public void leave(IDeclareExpression declare) {
		IType type = declare.getType();
		
		for (IExpression dc: declare.getDeclares()){
			IExpression left = dc, right = null;
			IVariable var = null;
			INameExpression ref = null;
			
			//Khai báo được gán giá trị
			if (left instanceof IBinaryExpression){
				IBinaryExpression bin = (IBinaryExpression) left;
				left = bin.getLeft();
				right = bin.getRight();
			}
			
			//Đây là một biến thường
			if (left instanceof INameExpression){
				ref = (INameExpression) left;
				var = new Variable(ref.getName(), type);
			}
			
			//Đây là một biến mảng
			else if (left instanceof IArrayIndexExpression) {
				IType arrayType = type;
				IArrayIndexExpression array = (IArrayIndexExpression) left;
				IExpression[] indexes = array.getIndexes();
				
				for (int i = indexes.length - 1; i >= 0; i--){
					int size = 0;
					
					if (indexes[i] instanceof INumberExpression)
						size = ((INumberExpression) indexes[i]).intValue();
					arrayType = new ArrayType(arrayType, size);
				}
				
				ref = new NameExpression(array.getName());
				var = new ArrayVariable(ref.getName(), (ArrayType) arrayType);
			}
			
			//Thêm biến vào bảng biến
			varTable.addVariable(var);
			
			//Khai báo và khởi tạo giá trị, thực hiện như gán
			if (right != null)
				handleAssignment(new BinaryExpression(
						ref, BinaryExpression.ASSIGN, right));
		}
	}

	@Override
	public void leave(IBinaryExpression bin) {
		if (bin.isAssignOperator()){
			handleAssignment(bin);
		}
		
		else 
			checkDivideByZero(bin);
	}
	
	@Override
	public void leave(IUnaryExpression unary) {
		String op = unary.getOperator();
		IExpression sub = unary.getSubElement();
		
		switch (op){
		case UnaryExpression.DECREASE:
		case UnaryExpression.INCREASE:
			String newOp = op.substring(1) + "=";
			//Phép tính ++i, --j: thực hiện phép tính rồi trả về giá trị sau
			if (unary.isLeftOperator()){
				
				//Chuyển qua biểu thức gán để xử lý: i = i+1
				handleAssignment(new BinaryExpression(
						sub, newOp, NumberExpression.ONE));
			}
			
			//Phép tính: i++, j--: trả về rồi thực hiện phép tính sau
			else{
				
//				((IRegistterValueUsed)sub).setOnValueUsedOne(ex -> 
//					handleAssignment(new BinaryExpression(
//						ex, newOp, NumberExpression.ONE)));
				handleAssignment(new BinaryExpression(
						sub, newOp, NumberExpression.ONE));
			}
			
			//Thay thế bằng tên biến: call(++i) => call(i)
			rootgroup.replaceChild(unary, sub);
			break;
		}
	}

	@Override
	public void leave(IReturnExpression rt) {
		IExpression value = rt.getReturnExpression();
		
		if (value != null){
			value = varTable.fill(value);
			constraint.setReturnExpression(value);
			path.setReturnExpression(value);
		}
	}

	protected void handleAssignment(IBinaryExpression assign){
		String op = assign.getOperator();
		IExpression left = assign.getLeft(), right = assign.getRight();
		
		//Các phép toán tình gộp, tách ra: a += b => a = (a+b)
		if (!op.equals(BinaryExpression.ASSIGN)){
			op = op.substring(0, 1);
			right = new BinaryExpression(left, op, right);
			checkDivideByZero((IBinaryExpression) right);
		}
		
		//Biến thường
		if (left instanceof INameExpression){
			String name = ((INameExpression) left).getName();
			
			//Xử lý gán mảng: int b[] = a;...
			
			varTable.updateVariable(name, right);
		}
		
		//Biển phần tử mảng
		else if (left instanceof IArrayIndexExpression){
			IArrayIndexExpression array = (IArrayIndexExpression) left;
			varTable.updateArrayElement(array.getName(), array.getIndexes(), right);
		}
		
		//Truy cập thuộc tính đối tượng
		else if (left instanceof IMemberAccessExpression){
			
		}
		
		//Thay thế biểu thức gốc bằng vế trái:
		//call(a = b[i]);  =>  call(a);
		rootgroup.replaceChild(assign, left);
	}

	/**
	 * Thêm ràng buộc biểu thức chia cho 0
	 */
	protected void checkDivideByZero(IBinaryExpression bin){
		if (!bin.getOperator().equals(BinaryExpression.DIV))
			return;
		IExpression up = bin.getLeft(), down = bin.getRight();
		IType tup = up.getType(), tdown = down.getType(), INT = BasicType.INT,
				LONG = BasicType.LONG;
		
		//Chỉ số nguyên/số nguyên mới gây lỗi chia cho 0
		if ((tup != INT && tup != LONG) || (tdown != INT && tdown != LONG))
			return;
		
		IExpression fdown = varTable.fill(down);
		rootgroup.replaceChild(down, fdown);
		
		//Tạo hệ ràng buộc bắt lỗi thương = 0
		cloneConstraint(new BinaryExpression(
					fdown, BinaryExpression.EQUALS, NumberExpression.ZERO),
				IPathConstraints.TYPE_DIVIDE_ZERO);
	}
	
	/**
	 * Tạo hệ ràng buộc mới ứng với trường hợp lỗi
	 * @param extraConstraint ràng buộc tạo ra lỗi
	 */
	protected void cloneConstraint(IExpression extraConstraint, int errorType){
		if (parseError){
			IPathConstraints clone = constraint.clone();
			clone.setConstraintType(errorType);
			clone.setPath(path.cloneAt(iter));
			listConstraint.add(clone);
			
			clone.addLogicConstraint(extraConstraint);
		}
		constraint.addLogicConstraint(new UnaryExpression(
				UnaryExpression.LOGIC_NOT, extraConstraint));
	}
	
	@Override
	public void leave(IArrayIndexExpression array) {
		if (array.isDeclare()) return;
		
		for (IExpression index: array.getIndexes()){
			IExpression findex = varTable.fill(index);
			rootgroup.replaceChild(index, findex);
			
			//Kiểm tra index < 0
			cloneConstraint(new BinaryExpression(
					findex, BinaryExpression.LESS, NumberExpression.ZERO), 
				IPathConstraints.TYPE_OUT_OF_BOUND);
		}
		
		//Scope = 1, tương ứng với các biến tham số
		if (varTable.getScope(array.getName()) == 1){
			constraint.addArrayAccess((IArrayIndexExpression) 
					varTable.fill(array.clone()));
		}
	}

	@Override
	public void leave(IMemberAccessExpression member) {
		//Kiểm tra con trỏ NULL
		if (!member.isDotAccess()){ //a -> b
			IExpression pointer = member.getNameExpression();
			pointer = varTable.fill(pointer);
			cloneConstraint(new BinaryExpression(
					pointer, BinaryExpression.EQUALS, NumberExpression.ZERO), 
				IPathConstraints.TYPE_NULL_POINTER);
		}
	}

	protected IVariableTable createVariableTable(){
		return new VariableTable();
	}

}
