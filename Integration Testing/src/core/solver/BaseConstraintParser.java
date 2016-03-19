package core.solver;

import java.util.ArrayList;
import java.util.List;

import api.expression.IBinaryExpression;
import api.expression.IDeclareExpression;
import api.expression.IExpression;
import api.expression.IExpressionGroup;
import api.expression.IMemberAccessExpression;
import api.models.IBasisPath;
import api.models.IStatement;
import api.models.IType;
import api.models.IVariable;
import api.parser.ConstraintParser;
import api.parser.IVariableTable;
import api.solver.IConstraint;
import core.Utils;
import core.expression.BinaryExpression;
import core.expression.ExpressionVisitor;
import core.expression.NumberExpression;
import core.expression.UnaryExpression;
import core.models.statement.ScopeStatement;
import core.models.type.BasicType;

public class BaseConstraintParser extends ExpressionVisitor
		implements ConstraintParser {

	private boolean parseError;
	private IVariableTable varTable;
	private IConstraint constraint;
	private List<IConstraint> listConstraint;
	
	private int iter;
	private IBasisPath path;
	private IExpressionGroup rootgroup;
	
	@Override
	public List<IConstraint> parseBasisPath(IBasisPath path, IVariable[] params,
			int options) {
		
		parseError = Utils.hasFlag(options, PARSE_ERROR_PATH);
		varTable = createVariableTable();
		constraint = new Constraint(params, path);
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
				IExpression root = stm.getRoot().clone();
				
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
		
	}

	@Override
	public void leave(IBinaryExpression bin) {
		if (bin.isAssignOperator()){
			
		}
		
		else if (bin.getOperator().equals(BinaryExpression.DIV))
			checkDivideByZero(bin.getLeft(), bin.getRight());
	}


	/**
	 * Thêm ràng buộc biểu thức chia cho 0
	 */
	private void checkDivideByZero(IExpression up, IExpression down){
		IType tup = up.getType(), tdown = down.getType(), INT = BasicType.INT,
				LONG = BasicType.LONG;
		
		//Chỉ số nguyên/số nguyên mới gây lỗi chia cho 0
		if ((tup != INT && tup != LONG) || (tdown != INT && tdown != LONG))
			return;
		
		IExpression fdown = varTable.fill(down);
		rootgroup.replaceChild(down, fdown);
		
		//Tạo hệ ràng buộc bắt lỗi thương = 0
		if (parseError){
			IConstraint clone = constraint.clone();
			clone.setConstraintType(IConstraint.TYPE_DIVIDE_ZERO);
			clone.setPath(path.cloneAt(iter));
			listConstraint.add(clone);
			
			clone.addLogicConstraint(new BinaryExpression(
					fdown, BinaryExpression.EQUALS, NumberExpression.ZERO));
		}
		constraint.addLogicConstraint(new BinaryExpression(
				fdown, BinaryExpression.NOT_EQUALS, NumberExpression.ZERO));
	}

	@Override
	public void leave(IMemberAccessExpression member) {
		
	}



	protected IVariableTable createVariableTable(){
		return new VariableTable();
	}

}
