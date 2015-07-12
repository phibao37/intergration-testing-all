package core.unit;

import core.error.StatementNoRootException;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Function;
import core.models.Statement;
import core.models.Type;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.DeclareExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.models.expression.PlaceHolderExpression;
import core.models.expression.ReturnExpression;
import core.models.expression.UnaryExpression;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.models.type.ArrayType;
import core.visitor.ExpressionVisitor;

/**
 * Phân tích các câu lệnh trong một đường thi hành chỉ định, lấy ra hệ các ràng buộc
 * về testcase cần được thỏa mãn để chương trình đi qua đường thi hành này
 */
public class BasisPathParser {
	
	private ConstraintEquations mConstraints;
	private VariableTable tables;
	
	/**
	 * Thêm một biểu thức ràng buộc mới
	 */
	protected void addConstrain(Expression constraint){
		mConstraints.add(constraint);
	}
	
	/**
	 * Thêm một biểu thức truy cập mảng testcase
	 */
	protected void addArrayAccess(ArrayIndexExpression array){
		mConstraints.addArrayAccess(array);
	}
	
	/**
	 * Trả về hệ ràng buộc được tạo ra bởi bộ phân tích này
	 */
	public ConstraintEquations getConstrains(){
		return mConstraints;
	}
	
	/**
	 * Phân tích một đường thi hành để tìm các ràng buộc testcase
	 * @param path đường thi hành cần phân tích
	 * @param func hàm chứa đường thi hành, dùng để lấy các biến tham số 
	 * @return hệ phương trình ràng buộc. Khi hệ này được thỏa mãn, chương trình
	 * được thực thi sẽ đi qua đường thi hành này
	 * @throws StatementNoRootException trong đường thi hành có câu lệnh chưa được
	 * đặt biểu thức gốc
	 */
	public void parseBasisPath(BasisPath path, Function func) 
			throws StatementNoRootException{
		mConstraints = new ConstraintEquations(func.getParameters());
		tables = new VariableTable();
		
		
		//Thêm 1 scope cho các biến tham số
		//Các biến global có scope = 0
		tables.newOpenScope();
		
		//Cho bản sao của các tham số vào bảng biến
		for (Variable para: func.getParameters())
			tables.add(para.clone());
		
		//Duyệt qua các câu lệnh trong đường thi hành
		for (int i = 0; i < path.size(); i++){
			Statement stm = path.get(i);
			
			//Các câu lệnh nhãn như BEGIN, END: bỏ qua
			if (stm instanceof FlagStatement)
				continue;
			
			if (stm instanceof ScopeStatement){
				ScopeStatement stmScope = (ScopeStatement) stm;
				
				//Gặp một "câu lệnh" mở khối {, tăng scope cho bảng biến
				if (stmScope.isOpenScope())
					tables.newOpenScope();
				
				//Gặp một "câu lệnh" đóng khối }, xóa các biến trong scope hiện thời
				//và giảm scope cho bảng biến
				else
					tables.newCloseScope();
			} else {
				Expression root = stm.getRoot();
				
				new PlaceHolderExpression(root).accept(new ExpressionVisitor() {

					@Override
					public int visit(ArrayIndexExpression array) {
						if (array.isDeclare())
							return PROCESS_CONTINUE;
						
						//Thêm điều kiện các chỉ số phải không âm
						for (Expression index: array.getIndexes()){
							addConstrain(new BinaryExpression(
									tables.evalExpression(index),
									BinaryExpression.GREATER_EQUALS, 
									new IDExpression("0")));
						}
						
						//Thêm các biểu thức truy cập biến mảng testcase (scope = 1)
						if (tables.getScope(array.getName()) == 1){
							Expression[] indexes = array.getIndexes();
							Expression[] indexs = new Expression[indexes.length];
							
							for (int i = 0; i < indexs.length; i++)
								indexs[i] = tables.evalExpression(indexes[i]);
							
							addArrayAccess(new ArrayIndexExpression(
									array.getName(), 
									indexs));
						}
							
						return PROCESS_CONTINUE;
					}
					
				});
				//Xủ lý theo từng kiểu
				handleStatement(stm, root, 
						i + 1 == path.size() ? null : path.get(i+1));
			}
		}
	}
	
	/**
	 * Xử lý câu lệnh theo kiểu và biểu thức gốc của nó
	 */
	protected void handleStatement(Statement stm, Expression expression, Statement next){
		switch (stm.getType()){
		case Statement.DECLARATION:
			handleDeclare((DeclareExpression) expression);
			break;
		case Statement.ASSIGNMENT:
			handleAssign((BinaryExpression) expression);
			break;
		case Statement.ASSIGNMENT_ONE:
			handleAssignOne((UnaryExpression) expression);
			break;
		case Statement.CONDITION:
			if (stm.getFalse() == next)
				expression = new UnaryExpression(UnaryExpression.LOGIC_NOT, expression);
			handleCondition(expression);
			break;
		case Statement.FUNCTION_CALL:
			handleFunctionCall((FunctionCallExpression) expression);
			break;
		case Statement.RETURN:
			handleReturn((ReturnExpression) expression);
			break;
		default:
			System.out.println("Unhandle statement: " + stm);
		}
	}
	
	/**
	 * Xử lý một biểu thức khai báo
	 */
	protected void handleDeclare(DeclareExpression declare){
		Type type = declare.getDeclareType();
		
		for (Expression dc: declare.getDeclares()){
			Expression left = dc, value = null;
			Variable var = null;
			
			//Có khởi tạo giá trị khi khai báo: int a = 2, a[] = {1, 2};
			if (dc instanceof BinaryExpression){
				BinaryExpression bin = (BinaryExpression) dc;
				left = bin.getLeft();
				value = bin.getRight();
			}
			
			//Đây là một khai báo biến bình thường
			if (left instanceof NameExpression){
				var = new Variable(((NameExpression) left).getName(), type);
			}
			
			//Đây là một khai báo mảng
			else {
				Type arrayType = type;
				ArrayIndexExpression array = (ArrayIndexExpression) left;
				Expression[] indexes = array.getIndexes();
				for (int i = indexes.length - 1; i >= 0; i--){
					Expression index = indexes[i];
					int size;
					
					if (index == null)
						size = 0;
					else if (index instanceof IDExpression){
						size = (int) ((IDExpression) index).getJavaValue();
					}
					else {
						throw new RuntimeException("Named length array not support: "
								+ array );
					}
					arrayType = new ArrayType(arrayType, size);
				}
				var = new ArrayVariable(array.getName(), (ArrayType) arrayType);
			}
			
			if (value != null)
				var.setValue(tables.fillExpression(value));
			
			//Thêm biến vào bảng biến
			tables.add(var);
			
		}
	}
	
	/**
	 * Xử lý một biểu thức gán 2 bên
	 */
	protected void handleAssign(BinaryExpression assign){
		String op = assign.getOperator();
		Expression name = assign.getLeft();
		Expression value = assign.getRight();
		
		//Các phép toán tính và gán: +=, *=, ...chuyển về dạng =
		if (!op.equals(BinaryExpression.ASSIGN)){
			value = new BinaryExpression(name, op.substring(0, 1), value);
		}
		
		//Biến thông thường
		if (name instanceof NameExpression){
			tables.updateVariableValue(((NameExpression) name).getName(), value);
		}
		
		//Có truy cập vào phần tử mảng
		else {
			ArrayIndexExpression array = (ArrayIndexExpression) name;
			tables.updateArrayValue(array.getName(), array.getIndexes(), value);
		}
	}
	
	/**
	 * Xử lý một biểu thức gán 1 bên (x++, --y)
	 */
	protected void handleAssignOne(UnaryExpression assign){
		IDExpression one = new IDExpression("1");
		Expression name = assign.getSubElement(), right;
		String op = assign.getOperator();
		String newOp;
		
		if (op.equals(UnaryExpression.INCREASE))
			newOp = BinaryExpression.ADD;
		else
			newOp = BinaryExpression.MINUS;

		//Chuyển qua dạng biểu thức 2 bên để xử lý tiếp
		right = new BinaryExpression(name, newOp, one);
		handleAssign(new BinaryExpression(name, BinaryExpression.ASSIGN, right));
	}
	
	/**
	 * Xử lý một biểu thức điều kiện
	 */
	protected void handleCondition(Expression condition){
		//System.out.printf("\n***table = %s\n%s", tables, condition);
		
		//Fill biểu thức bởi các biến testcase, sau đó thêm vào hệ ràng buộc
		condition = tables.evalExpression(condition);
		//System.out.printf(" --> %s\n\n", condition);
		addConstrain(condition);
	}
	
	/**
	 * Xử lý một biểu thức gọi hàm
	 */
	protected void handleFunctionCall(FunctionCallExpression call){
		
	}
	
	/**
	 * Xử lý biểu thức RETURN
	 */
	protected void handleReturn(ReturnExpression rt){
		
	}
	
	/**
	 * Bộ duyệt mặc định
	 */
	public static final BasisPathParser DEFAULT = new BasisPathParser();
	
}









