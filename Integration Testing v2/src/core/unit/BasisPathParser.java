package core.unit;

import java.util.ArrayList;

import core.error.CoreException;
import core.inte.StubSuite;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Function;
import core.models.Statement;
import core.models.Type;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.Conditionable;
import core.models.expression.DeclareExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.models.expression.NamedAttribute;
import core.models.expression.PlaceHolderExpression;
import core.models.expression.ReturnExpression;
import core.models.expression.UnaryExpression;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.models.type.ArrayType;
import core.models.type.BasicType;
import core.visitor.ExpressionVisitor;
import javafx.util.Pair;

/**
 * Phân tích các câu lệnh trong một đường thi hành chỉ định, lấy ra hệ các ràng buộc
 * về testcase cần được thỏa mãn để chương trình đi qua đường thi hành này
 */
public class BasisPathParser {
	
	private ConstraintEquations mConstraints;
	protected VariableTable tables;
	//private BasisPath mPath;
	
	/**
	 * Cho phép ngắt quãng việc phân tích giữa chừng
	 */
	protected boolean shouldContinue;
	
	private ArrayList<UnaryExpression> postFix = new ArrayList<>();
	
	private ArrayList<Pair<Statement, ArrayList<Expression>>> mAnalyzic;
	private Statement mStatement;
	private ArrayList<Expression> mStmConstraint;
	protected StubSuite mStubSuite;
	protected CoreException mError;
	
	/**
	 * Thêm một điều kiện ràng buộc chính (điều kiện quyết định nhánh)
	 * @param condition biểu thức điều kiện ràng buộc
	 * @param not dạng phủ định (nhánh false)
	 */
	protected void addCondition(Expression condition, boolean not){
		boolean notCondition = true;
		
		if (condition instanceof Conditionable)
			notCondition = !((Conditionable) condition).isConditionExpression();
		else if (condition instanceof NamedAttribute){
			String name = ((NamedAttribute) condition).getName();
			notCondition = tables.find(name).getDataType() != BasicType.BOOL;
		} else {
			System.out.println("Không rõ biểu thức điều kiện: " + condition);
		}
		
		//Nếu chưa là biểu thức điều kiện (2), chuyển thành dạng điều kiện (2 != 0)
		if (notCondition)
			condition = new BinaryExpression(
					condition, 
					BinaryExpression.NOT_EQUALS, 
					IDExpression.ZERO);
		
		//Chuyển về phủ định 
		if (not)
			condition = new UnaryExpression(UnaryExpression.LOGIC_NOT, condition);
		
		putConstraint(condition, true);
	}
	
	/**
	 * Thêm một ràng buộc nâng cao mới
	 */
	protected void addConstraint(Expression constraint){
		putConstraint(constraint, false);
	}
	
	private void putConstraint(Expression constraint, boolean isMain){
		mConstraints.add(constraint);
		
		if (mStmConstraint == null){
			mStmConstraint = new ArrayList<>();
			mAnalyzic.add(new Pair<>(mStatement, mStmConstraint));
		}
		if (isMain)
			mStmConstraint.add(0, constraint);
		else
			mStmConstraint.add(constraint);
	}
	
	/**
	 * Thêm một biểu thức truy cập mảng testcase
	 */
	protected void addArrayAccess(ArrayIndexExpression array){
		mConstraints.addArrayAccess(array);
	}
	
	/**
	 * Đặt biểu thức bên trong câu lệnh return ứng với đường đi này
	 */
	protected void setReturnExpression(Expression returned){
		mConstraints.setReturnExpression(returned);
	}
	
	/**
	 * Trả về hệ ràng buộc được tạo ra bởi bộ phân tích này
	 */
	public ConstraintEquations getConstrains(){
		return mConstraints;
	}
	
	/**
	 * Trả về đường thi hành đang được phân tích
	 */
//	public BasisPath getPath(){
//		return mPath;
//	}
	
	/**
	 * Phân tích một đường thi hành để tìm các ràng buộc testcase
	 * @param path đường thi hành cần phân tích
	 * @param func hàm chứa đường thi hành, dùng để lấy các biến tham số
	 * @param stub bộ stub dùng để thay thế các hàm bằng các giá trị cứng
	 * @throws CoreException các lỗi làm cho việc phân tích không thực hiện được
	 */
	public void parseBasisPath(BasisPath path, Function func, StubSuite stub) 
			throws CoreException{
		mError = null;
		mStubSuite = stub;
		shouldContinue = true;
		//mPath = path;
		mConstraints = new ConstraintEquations(func.getParameters());
		tables = new VariableTable();
		mAnalyzic = new ArrayList<>();
		path.setAnalyzic(mAnalyzic);
		
		//Thêm 1 scope cho các biến tham số
		//Các biến global có scope = 0
		tables.newOpenScope();
		
		//Cho bản sao của các tham số vào bảng biến
		for (Variable para: func.getParameters())
			tables.add(para.clone());
		
		//Duyệt qua các câu lệnh trong đường thi hành
		for (int i = 0; shouldContinue && i < path.size(); i++){
			mStatement = path.get(i);
			
			//Các câu lệnh nhãn như BEGIN, END: bỏ qua
			if (mStatement instanceof FlagStatement)
				continue;
			
			if (mStatement instanceof ScopeStatement){
				ScopeStatement stmScope = (ScopeStatement) mStatement;
				
				//Gặp một "câu lệnh" mở khối {, tăng scope cho bảng biến
				if (stmScope.isOpenScope())
					tables.newOpenScope();
				
				//Gặp một "câu lệnh" đóng khối }, xóa các biến trong scope hiện thời
				//và giảm scope cho bảng biến
				else
					tables.newCloseScope();
			} else {
				Expression root = mStatement.getRoot().clone();
				mStmConstraint = null;
				postFix.clear();
				
				preVisitRoot(root);

				//Xủ lý theo từng kiểu
				handleStatement(mStatement, root, 
						i + 1 == path.size() ? null : path.get(i+1));
				
				//Thực hiện tăng các biểu thức x++, y-- sau khi đã thực hiện xong
				//TODO chưa thực sự đúng vì có thể có nhiều biểu thức trong 1 lệnh

				postFix.forEach(this::handleAssignOne);
			}
		}
	}
	
	/**
	 * Xử lý câu lệnh theo kiểu và biểu thức gốc của nó
	 */
	protected void handleStatement(Statement stm, Expression expression, 
			Statement next){
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
			handleCondition(expression, stm.getFalse() == next);
			break;
		case Statement.FUNCTION_CALL:
			break;
		case Statement.RETURN:
			handleReturn((ReturnExpression) expression);
			break;
		default:
			System.out.println("Unhandle statement: " + stm);
		}
	}
	
	/**
	 * Các công việc cần được xử lý trước khi chính thức xủ lý biểu thức gốc.<br/>
	 * Thí dụ: biểu thức (++i == 3) cần được xủ lý i = i + 1 trước, sau đó thay thế
	 * biểu thức thành (i == 3)
	 */
	protected void preVisitRoot(Expression root) throws CoreException{
		PlaceHolderExpression holder = new PlaceHolderExpression(root);
		holder.accept(new ExpressionVisitor() {
			
			@Override
			public int visit(UnaryExpression unary) {
				if (unary == root //chỉ xét bên trong gốc
						|| !unary.isAssignOperator()) //chỉ xét gán
					return PROCESS_CONTINUE;
				
				//Thí dụ: (x < ++i)
				//Thực hiện tăng/giảm cho biến prefix trước: i = i + 1
				if (unary.isLeftOperator())
					handleAssignOne(unary);
				
				//Với các biến postfix, thêm vào danh sách để xử lý sau
				else
					postFix.add(unary);
				
				//Thay thế bằng biến: (x < i)
				holder.replace(unary, unary.getSubElement());
				
				return PROCESS_SKIP;
			}
			
		});
		
		holder.accept(new ExpressionVisitor() {

			@Override
			public int visit(ArrayIndexExpression array) {
				if (array.isDeclare())
					return PROCESS_CONTINUE;
				
				//Thêm điều kiện các chỉ số phải không âm
				for (Expression index: array.getIndexes()){
					addConstraint(new BinaryExpression(
							tables.fillExpression(index),
							BinaryExpression.GREATER_EQUALS,
							IDExpression.ZERO
					));
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
		
		//Thay thế các lời gọi hàm bằng giá trị stub
		holder.accept(new ExpressionVisitor() {

			@Override
			public boolean preVisit(Expression expression) {
				return mError == null;
			}

			@Override
			public void leave(FunctionCallExpression call) {
				try {
					holder.replace(call, handleFunctionCall(call));
				} catch (CoreException e) {
					mError = e;
				}
			}
			
		});
		
		if (mError != null)
			throw mError;
	}
	
	/**
	 * Xử lý một biểu thức khai báo
	 */
	protected void handleDeclare(DeclareExpression declare){
		Type type = declare.getType();
		
		for (Expression dc: declare.getDeclares()){
			Expression left = dc, value = null;
			Variable var;
			NameExpression name;
			
			//Có khởi tạo giá trị khi khai báo: int a = 2, a[] = {1, 2};
			if (dc instanceof BinaryExpression){
				BinaryExpression bin = (BinaryExpression) dc;
				left = bin.getLeft();
				value = bin.getRight();
			}
			
			//Đây là một khai báo biến bình thường
			if (left instanceof NameExpression){
				name = (NameExpression) left;
				var = new Variable(name.getName(), type);
			}
			
			//Đây là một khai báo mảng
			else {
				Type arrayType = type;
				ArrayIndexExpression array = (ArrayIndexExpression) left;
				Expression[] indexes = array.getIndexes();
				name = new NameExpression(array.getName());
				
				for (int i = indexes.length - 1; i >= 0; i--){
					Expression index = indexes[i];
					int size;
					
					if (index == null)
						size = 0;
					else if (index instanceof IDExpression){
						size = ((IDExpression) index).intValue();
					}
					else {
						throw new RuntimeException("Named length array not support: "
								+ array );
					}
					arrayType = new ArrayType(arrayType, size);
				}
				var = new ArrayVariable(array.getName(), (ArrayType) arrayType);
			}
			
			//Thêm biến vào bảng biến
			tables.add(var);
			
			if (value != null)
				handleAssign(new BinaryExpression(
						name, 
						BinaryExpression.ASSIGN, 
						value
				));
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
		
		//Không có truy cập phần tử mảng
		if (name instanceof NameExpression){
			boolean shouldReplace = true;
			String s_name = ((NameExpression) name).getName();
			
			if (value instanceof NameExpression){
				Variable find = tables.find(value.getContent());
				
				//Cả vế phải cũng là tham chiếu đến mảng, 2 biến này sẽ có cùng chung 
				//tham chiếu tới giá trị của biển vế phải: int a[] = {1, 2}, b = a;
				if (find instanceof ArrayVariable){
					tables.find(s_name).setValue(find.getValue());
					shouldReplace = false;
				}
				
				//TODO chưa hỗ trợ: int test(int a[][][]){int b[][] = a[0];}
			}
			
			if (shouldReplace)
				tables.updateVariableValue(s_name, value);
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
		Expression name = assign.getSubElement(), right;
		String op = assign.getOperator();
		String newOp;
		
		if (op.equals(UnaryExpression.INCREASE))
			newOp = BinaryExpression.ADD;
		else
			newOp = BinaryExpression.MINUS;

		//Chuyển qua dạng biểu thức 2 bên để xử lý tiếp
		right = new BinaryExpression(name, newOp, IDExpression.ONE);
		handleAssign(new BinaryExpression(name, BinaryExpression.ASSIGN, right));
	}
	
	/**
	 * Xử lý một biểu thức điều kiện
	 */
	protected void handleCondition(Expression condition, boolean not){
		//System.out.printf("\n***table = %s\n%s", tables, condition);
		
		//Fill biểu thức bởi các biến testcase, sau đó thêm vào hệ ràng buộc
		condition = tables.fillExpression(condition);
		//System.out.printf(" --> %s\n\n", condition);
		addCondition(condition, not);
	}
	
	/**
	 * Xử lý một biểu thức gọi hàm
	 * @return nếu hàm được gọi để lấy giá trị, thì giá trị kết quả sẽ được trả về
	 * <ul>
	 * <li>Hàm sẽ được chuyển qua bộ biên dịch để tạo file thực thi, sau đó truyền
	 * các tham số vào và nhận lấy kết quả, được chuyển sang biểu thức</li>
	 * <li>
	 * Các hàm mà đã biết được cả nội dung thân hàm có thể được chạy theo chế độ static
	 * nếu hỗ trợ
	 * </li>
	 * </ul>
	 * @throws CoreException chưa có bộ stub kiểm thử
	 */
	protected Expression handleFunctionCall(FunctionCallExpression call) 
			throws CoreException{
		try{
			//assert mStubSuite.containsKey(call.getFunction());
			return mStubSuite.get(call.getFunction());
		} catch (Exception e){
			throw new CoreException("Cần có bộ stub và hàm tương ứng để kiểm thử");
		}
	}
	
	/**
	 * Xử lý biểu thức RETURN
	 */
	protected void handleReturn(ReturnExpression rt){
		Expression value = rt.getReturnExpression();
		
		if (value != null){
			setReturnExpression(tables.fillExpression(value));
		}
	}
	
	/**
	 * Bộ duyệt mặc định
	 */
	public static final BasisPathParser DEFAULT = new BasisPathParser();
	
}









