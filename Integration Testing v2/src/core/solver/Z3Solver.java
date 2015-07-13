package core.solver;

import java.util.ArrayList;
import java.util.HashMap;
import core.models.Expression;
import core.models.Type;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.IDExpression;
import core.models.expression.UnaryExpression;
import core.models.type.ArrayType;
import core.models.type.BasicType;
import core.solver.Z3.Func;
import core.unit.VariableTable;

/**
 * Giải hệ theo chương trình Z3
 */
public class Z3Solver implements Solver {
	
	private Variable[] mTestcase;
	private String mSolutionStr;
	private int mSolutionCode;
	
	private VariableTable mTable;
	private Z3 z3 = new Z3();
	
	/**
	 * Bộ giải hệ theo Z3 mặc định
	 */
	public static final Z3Solver DEFAULT = new Z3Solver();
	
	public static final String RESULT_SAT = "sat";
	public static final String RESULT_UNSAT = "unsat";
	public static final String RESULT_UNKNOWN = "unknown";
	
	@Override
	public void beginSolve(Variable[] testcases,
			ArrayList<Expression> constraints,
			ArrayList<ArrayIndexExpression> array) {
		
		//Khởi tạo bảng biến, reset lại nghiệm
		mTable = new VariableTable();
		mTestcase = null;
		mSolutionCode = UNKNOWN;
		mSolutionStr = RESULT_UNKNOWN;
		
		//Thêm các khai báo biến vào bảng biến
		for (Variable testcase: testcases)
			addVariable(testcase);
		
		//Thêm các ràng buộc của hệ
		for (Expression constraint: constraints)
			addCondition(constraint);
		
		//Thực thi chương trình Z3
		z3.addLine("check-sat")
		.addLine("get-model")
		.execute();
		
		String result = z3.getLine();
		if (RESULT_SAT.equals(result)) {
			
			//Tạo bản sao danh sách các biến để xem những biến nào đã được z3 giải
			ArrayList<Variable> nonResult = new ArrayList<Variable>(mTable);
			
			//Bỏ qua dòng đầu: (model
			z3.getLine(); 
			
			while (z3.hasFunction()){
				Func func = z3.getFunction();
				Variable var = mTable.find(func.getName());
				
				//Xóa biến đã được z3 giải
				nonResult.remove(var);

				//Thêm các khai báo hàm bên trong model
				z3.addFunction(func);
			}
			
			//Bỏ qua dòng cuối: đóng model)
			z3.getLine();
			
			for (Variable var: nonResult){
				Func func = toZ3Func(var, false);
				
				//Với các biến không được z3 giải, tạo khai báo hàm mặc định theo kiểu
				func.setValueFromType();
				z3.addFunction(func);
			}

			for (Variable v: testcases)
				//Rút gọn các biểu thức hàm trong z3 nếu không là biến mảng
				if (!v.getType().isArrayType())
					z3.addLine("simplify %s", v.getName());  			// (I)
			
			for (ArrayIndexExpression arr: array){
				String params = "";
				
				//Với các biểu thức truy cập mảng, đầu tiên cần rút gọn các chỉ số
				//dẫn đến phần tử mảng
				for (Expression index: arr.getIndexes()){
					String s_index = parseCondition(index);
					z3.addLine("simplify %s", s_index); 				// (II)
					params += " " + s_index;
				}
				
				//Sau đó, rút gọn giá trị phần tử mảng tại vị trí tương ứng
				z3.addLine("simplify (%s%s)", arr.getName(), params); 	// (III)
			}
			z3.execute();
			
			for (Variable v: testcases)
				if (!v.getType().isArrayType()){
					String value = z3.getLine();						// (I)
					String name = v.getName();
					
					//Gán giá trị cho biến thường
					mTable.updateVariableValue(name, str2Expression(value));
				}
			
			for (ArrayIndexExpression arr: array){
				Expression[] indexes = arr.getIndexes();
				Expression[] indexs = new Expression[indexes.length];
				
				//Lấy biểu thức các chỉ số mảng sau khi đã được z3 rút gọn
				for (int i = 0; i < indexs.length; i++){
					indexs[i] = str2Expression(z3.getLine());			// (II)
				}
				
				//Cập nhật giá trị phần tử của biến mảng tại vị trí tương ứng
				mTable.updateArrayValue(arr.getName(), 
						indexs, str2Expression(z3.getLine()));			// (III)
			}
			
			//Đặt kết quả
			mTestcase = new Variable[testcases.length];
			mTable.toArray(mTestcase);
			mSolutionCode = SUCCESS;
			mSolutionStr = mTable.toString();
			
		} else if (RESULT_UNSAT.equals(result)){
			mSolutionCode = ERROR;
			mSolutionStr = RESULT_UNSAT; //+ Why?
		} 
		
	}
	
	/**
	 * Tạo biểu thức ứng với string kết quả của z3 
	 * @param str 4: không cần xử lý<br/>
	 * (- 4): bỏ dấu ngoặc và khoảng trắng ở giữa dấu âm
	 * @return biểu thức kết quả, dạng biểu thức hằng
	 */
	private static IDExpression str2Expression(String str){
		boolean neg = false;
		
		//Loại bỏ dấu ngoặc ( ... )
		while (str.matches("\\(.*\\)"))
			str = str.substring(1, str.length() - 1);
		
		//Có dấu âm
		if (str.charAt(0) == '-'){
			neg = true;
			str = str.substring(1);
		}
		
		if (neg)
			str = "-" + str.trim(); //thường là có khoảng trắng ở phia sau dấu âm
		return new IDExpression(str);
	}
	
	/**
	 * Thêm một biến testcase vào để giải
	 */
	public Z3Solver addVariable(Variable var){
		//Thêm bản sao vào bảng biến, vì sẽ được gán giá trị sau đó
		mTable.add(var.clone());
		
		//Thêm khai báo hàm vào z3 từ biến testcase
		z3.addFunction(toZ3Func(var, true));
		return this;
	}
	
	/**
	 * Chuyển từ biến testcase sang dạng khai báo hàm trong z3
	 * @param var biến testcase cần chuyển
	 * @param declare dự định để khai báo (declare) hoặc tạo (define)
	 * @return hàm ứng với biến testcase
	 */
	private static Z3.Func toZ3Func(Variable var, boolean declare){
		Type type = var.getType();
		Z3.Func func = null;
		
		if (type instanceof ArrayType){
			int count = 0;
			
			while (type instanceof ArrayType){
				count++;
				type = ((ArrayType) type).getSubType();
			}
			
			func = new Func(var.getName(), toSmt2(type));
			for (int i = 1; i <= count; i++)
				func.addParameter(declare ? "Int" : String.format("(x!%d Int)", i));
		} 
		
		else {
			func = new Func(var.getName(), toSmt2(type));
		}
		return func;
	}
	
	/**
	 * Thêm một điều kiện ràng buộc vào để giải
	 */
	public Z3Solver addCondition(Expression condition){
		z3.addLine("assert %s", parseCondition(condition));
		return this;
	}
	
	/**
	 * Chuyển từ biểu thức sang string của ngôn ngữ smt2
	 * @param ex biểu thức cần chuyển
	 * @return string trong ngôn ngữ smt2 tương ứng
	 */
	private String parseCondition(Expression ex){
		
		//Kiểu biểu thức 2 bên
		if (ex instanceof BinaryExpression){
			BinaryExpression bin = (BinaryExpression) ex;
			String op1 = parseCondition(bin.getLeft());
			String op2 = parseCondition(bin.getRight());
			String op = bin.getOperator();
			
			//Chuyển == sang =
			if (op.equals(BinaryExpression.EQUALS))
				return String.format("(= %s %s)", op1, op2);
			
			//Chuyển != sang not (= )
			else if (op.equals(BinaryExpression.NOT_EQUALS))
				return String.format("(not (= %s %s))", op1, op2);
			
			//Chuyển && sang and
			else if (op.equals(BinaryExpression.LOGIC_AND))
				return String.format("(and %s %s)", op1, op2);
			
			//Chuyển || sang or
			else if (op.equals(BinaryExpression.LOGIC_OR))
				return String.format("(or %s %s)", op1, op2);
			
			//Chuyển % sang mod
			else if (op.equals(BinaryExpression.MOD))
				return String.format("(rem %s %s)", op1, op2);
			
			//TODO chuyển / sang div nếu kiểu của 2 vế đều là int!!!!!!!!!!!! 
			
			//Các dấu bình thường như +, -, >, <, ... để nguyên
			else 
				return String.format("(%s %s %s)",op, op1, op2);
		} 
		
		//Kiểu biểu thức một bên
		else if (ex instanceof UnaryExpression){
			UnaryExpression u = (UnaryExpression) ex;
			String op = u.getOperator();
			String sub = parseCondition(u.getSubElement());
			
			//Chuyển ! sang not
			if (op.equals(UnaryExpression.LOGIC_NOT))
				return String.format("(not %s)", sub);
			
			//Phép toán lấy dấu âm, thêm khoảng trắng vào giữa
			else if (op.equals(UnaryExpression.MINUS))
				return String.format("(- %s)", sub);
			
			//Phép toán lấy dấu dương, thêm khoảng trắng vào giữa
			else if (op.equals(UnaryExpression.PLUS))
				return String.format("(+ %s)", sub);
		}
		
		//Biểu thức truy cập mảng
		else if (ex instanceof ArrayIndexExpression){
			ArrayIndexExpression array = (ArrayIndexExpression) ex;
			String params = "";
			
			for (Expression index: array.getIndexes())
				params += " " + parseCondition(index);
			return String.format("(%s %s)", array.getName(), params);
		}
		
		//Kiểu bình thường (tên biến, giá trị hằng), trả về nội dung của biểu thức
		return ex.getContent();
	}
	
	private static HashMap<Type, String> smtMap = new HashMap<>();
	
	static{
		smtMap.put(BasicType.INT, "Int");
		smtMap.put(BasicType.FLOAT, "Real");
		smtMap.put(BasicType.DOUBLE, "Real");
		smtMap.put(BasicType.BOOL, "Bool");
		smtMap.put(BasicType.BOOLEAN, "Bool");
	}
	
	/**
	 * Chuyển từ kiểu trong ngôn ngữ sang kiểu của Z3
	 */
	public static String toSmt2(Type t){
		
		if (smtMap.containsKey(t))
			return smtMap.get(t);
		
		return t.getContent();
	}
	
	@Override
	public Variable[] getSolution() {
		return mTestcase;
	}
	
	@Override
	public int getSolutionCode() {
		return mSolutionCode;
	}

	public String getSolutionMessage(){
		return mSolutionStr;
	}

}
