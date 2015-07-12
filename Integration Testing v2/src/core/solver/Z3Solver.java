package core.solver;

import java.util.ArrayList;
import java.util.List;

import core.models.Expression;
import core.models.Type;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.IDExpression;
import core.models.expression.UnaryExpression;
import core.models.type.ArrayType;
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

			//Lấy kết quả của z3 (kết quả bên trong khối model)
			List<String> lines = z3.getLines();
			String input = lines.get(1); // Bỏ qua dòng đầu (model
			for (int i = 2;
					i < lines.size() - 1;// Bỏ qua dòng cuối (đóng model)
					i++)
				input += "\n" + lines.get(i);
			
			//Đưa kết quả của z3 vào lại input của z3 để rút gọn các biểu thức hàm
			z3.setRaw(input);

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
					
					//Biến testcase này không có ràng buộc nào nên z3 không tạo ra biến,
					//tự tạo giá trị mặc định cho biến này
					if (value.endsWith("unknown constant " + name + "\")"))
						mTable.find(name).initValueIfNotSet();
					
					//Gán giá trị cho biến thường
					else
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
		
		Type type = var.getType();
		
		//Với biến kiểu mảng, cần dùng 
		//declare-fun <tên biến> (<kiểu chỉ số>...) <kiểu phần tử>
		if (type instanceof ArrayType){
			String param = "";
			
			//Kiểu của chỉ số mảng luôn là Int
			while (type instanceof ArrayType){
				param += " Int";
				type = ((ArrayType) type).getSubType();
			}
			
			z3.addLine("declare-fun %s (%s) %s", var.getName(),
					param.substring(1), toSmt2(type));
			
		} 
		
		//Biến thường, sử dụng declare-const <tên biến> <kiểu biến>
		else {
			String name = var.getName();
			z3.addLine("declare-const %s %s", name, toSmt2(type));
		}
		return this;
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
	
	/**
	 * Chuyển từ kiểu trong ngôn ngữ sang kiểu của Z3
	 */
	public static String toSmt2(Type t){
		String content = t.getContent();
		
		//Đổi chữ cái đàu tiên sang chữ hoa, TODO xem danh sách kiểu của z3
		return Character.toUpperCase(content.charAt(0)) + content.substring(1);
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
