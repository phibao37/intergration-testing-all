package core.solver.z3;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import api.expression.IArrayIndexExpression;
import api.expression.IBinaryExpression;
import api.expression.IExpression;
import api.expression.IMemberAccessExpression;
import api.expression.IUnaryExpression;
import api.models.IType;
import api.models.IVariable;
import api.solver.IPathConstraints;
import api.solver.ISolution;
import api.solver.ISolver;
import api.solver.IVariableTable;
import core.expression.BinaryExpression;
import core.expression.NumberExpression;
import core.expression.StringExpression;
import core.expression.UnaryExpression;
import core.models.Variable;
import core.models.type.ArrayType;
import core.models.type.BasicType;
import core.models.type.ObjectType;
import core.solver.Solution;
import core.solver.VariableTable;
import core.solver.z3.Z3.Func;

public class Z3Solver implements ISolver {
	
	public static final String RESULT_SAT = "sat",
			RESULT_UNSAT = "unsat",
			RESULT_UNKNOWN = "unknown";

	private Z3 z3;
	private IVariableTable varTable;
	private ArrayList<LinkMemberVariable> linkMembers;
	private boolean mSolveOk;
	
	@Override
	public ISolution solveConstraint(IPathConstraints constraint) {
		IVariable[] solution = null;
		int code = ISolution.UNKNOWN;
		String message = RESULT_UNKNOWN;
		List<IArrayIndexExpression> array = constraint.getArrayAccess();
		IExpression returnValue = null;
		boolean isNormalConstraint = 
				constraint.getConstraintType() == IPathConstraints.TYPE_NORMAL;
		
		z3 = new Z3();
		varTable = createVariableTable();
		linkMembers = new ArrayList<>();
		mSolveOk = false;
		
		//Thêm các khai báo biến vào bảng biến
		for (IVariable input: constraint.getParameters())
			addVariable(input, true);
		
		//Thêm các ràng buộc của hệ
		for (IExpression cnt: constraint.getLogicConstraints())
			addCondition(cnt);
		
		//Thực thi chương trình Z3
try{
		z3.addLine("check-sat")
		.addLine("get-model")
		.execute();
		
		String result = z3.getLine();
		if (RESULT_SAT.equals(result)) {
			
			//Tạo bản sao danh sách các biến để xem những biến nào đã được z3 giải
			ArrayList<IVariable> nonResult = new ArrayList<>(varTable);
			
			//Bỏ qua dòng đầu: (model
			z3.getLine(); 
			mSolveOk = true;
			
			while (z3.hasFunction()){
				Func func = z3.getFunction();
				IVariable var = varTable.find(func.getName());
				
				//Xóa biến đã được z3 giải
				nonResult.remove(var);

				//Thêm các khai báo hàm bên trong model
				z3.addFunction(func);
			}
			
			//Bỏ qua dòng cuối: đóng model)
			z3.getLine();
			
			for (IVariable var: nonResult){
				if (var.getType() instanceof ObjectType){
					var.initValueIfNotSet();
				}
				else{
				Func func = toZ3Func(var, false);
				
				//Với các biến không được z3 giải, tạo khai báo mặc định theo kiểu
				func.setValueFromType();
				z3.addFunction(func);
				}
			}
			
			for (IVariable v: varTable)
				//Rút gọn các biểu thức hàm trong z3 nếu không là biến mảng
				if (v.getType() instanceof BasicType)
					z3.addLine("simplify %s", v.getName());  			// (I)
			
			for (IArrayIndexExpression arr: array){
				String params = "";
				
				//Với các biểu thức truy cập mảng, đầu tiên cần rút gọn các chỉ số
				//dẫn đến phần tử mảng
				for (IExpression index: arr.getIndexes()){
					String s_index = parseCondition(index);
					z3.addLine("simplify %s", s_index); 				// (II)
					params += " " + s_index;
				}
				
				//Sau đó, rút gọn giá trị phần tử mảng tại vị trí tương ứng
				z3.addLine("simplify (%s%s)", arr.getName(), params); 	// (III)
			}
			
			if (isNormalConstraint && constraint.getReturnExpression() != null)
				z3.addLine("simplify %s", 
						parseCondition(constraint.getReturnExpression()));  //(IV)
			
			z3.execute();
			
			for (IVariable v: varTable)
				if (v.getType() instanceof BasicType){
					String value = z3.getLine();						// (I)
					String name = v.getName();
					
					//Gán giá trị cho biến thường
					varTable.updateVariable(name, str2Expression(value));
				}
			
			for (IArrayIndexExpression arr: array){
				IExpression[] indexes = arr.getIndexes();
				IExpression[] indexs = new IExpression[indexes.length];
				
				//Lấy biểu thức các chỉ số mảng sau khi đã được z3 rút gọn
				for (int i = 0; i < indexs.length; i++){
					indexs[i] = str2Expression(z3.getLine());			// (II)
				}
				
				try{
				//Cập nhật giá trị phần tử của biến mảng tại vị trí tương ứng
				varTable.updateArrayElement(arr.getName(), 
						indexs, str2Expression(z3.getLine()));			// (III)
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
			
			if (isNormalConstraint){
				if (constraint.getReturnExpression() != null)
					returnValue = str2Expression(z3.getLine());   		//(IV)
				code = ISolution.SATISFY;
			} 
			
			//Với các hệ ràng buộc gây ra lỗi, sẽ không có biểu thức return
			else {
				code = ISolution.ERROR;
				String error = null;
				
				switch (constraint.getConstraintType()){
				case IPathConstraints.TYPE_DIVIDE_ZERO:
					error = "Division by 0"; break;
				}
				returnValue = new StringExpression(error);
			}
			
			//Gán các giá trị dạng đối tượng
			for (LinkMemberVariable var: linkMembers){
				IMemberAccessExpression member = var.getLinkedExpression();
				varTable.updateMemberValue(member, var.getValue());
			}
			varTable.removeIf(t -> t instanceof LinkMemberVariable);
			
			solution = new IVariable[varTable.size()];
			varTable.toArray(solution);
			message = summarySolution(varTable);
			
		} else if (RESULT_UNSAT.equals(result)){
			code = ISolution.UNSATISFIED;
			message = RESULT_UNSAT;
		} 
		
} catch (IOException e){
	e.printStackTrace();
	//Co van de xay ra, return UNKNOWN
}
		
		return new Solution(solution, code, message, returnValue, this);
	}
	
	/**
	 * Tạo biểu thức ứng với string kết quả của z3 
	 * @param str 4: không cần xử lý<br/>
	 * (- 4): bỏ dấu ngoặc và khoảng trắng ở giữa dấu âm
	 * @return biểu thức kết quả, dạng biểu thức hằng
	 */
	private static NumberExpression str2Expression(String str){
		boolean neg = false;
		String extra = null;
		
		//Loại bỏ dấu ngoặc ( ... )
		while (str.matches("\\(.*\\)"))
			str = str.substring(1, str.length() - 1);
		
		//Có dấu âm
		if (str.charAt(0) == '-'){
			neg = true;
			str = str.substring(2);
		}
		
		//Loại bỏ dấu ngoặc ( ... )
		while (str.matches("\\(.*\\)"))
			str = str.substring(1, str.length() - 1);
		
		//Là một phép chia /
		if (str.matches("/ [\\d\\.]+ [\\d\\.]+")){
			String[] part = str.split(" ");
			BigDecimal d1 = new BigDecimal(part[1]);
			BigDecimal d2 = new BigDecimal(part[2]);
			
			if (d2.compareTo(BigDecimal.ZERO) == 0)
				str = "Infinity";
			else{
				try{
					str = d1.divide(d2).toString();
				} catch (Exception e){
					str = d1.divide(d2, 10, BigDecimal.ROUND_HALF_DOWN).toString();
					extra = d1.longValue() + "/" + d2.longValue();
				}
			}
		} 
		
		//Phép chia nguyên div
		else if (str.matches("div \\d+ \\d+")){
			String[] part = str.split(" ");
			BigInteger d1 = new BigInteger(part[1]);
			BigInteger d2 = new BigInteger(part[2]);
			
			try{
				str = d1.divide(d2).toString();
			} catch (Exception e){
				str = "Infinity";
			}
		}
		
		if (neg)
			str = "-" + str;
		return new NumberExpression(str).setExtraDisplay(extra);
	}

	@Override
	public String getName() {
		return "Z3";
	}
	
	/**
	 * Thêm một biến testcase vào để giải
	 */
	public Z3Solver addVariable(IVariable var, boolean clone){
		//Thêm bản sao vào bảng biến, vì sẽ được gán giá trị sau đó
		varTable.addVariable(clone ? var.clone() : var);
		
		//Thêm khai báo hàm vào z3 từ biến testcase
		if (!(var.getType() instanceof ObjectType))
			z3.addFunction(toZ3Func(var, true));
		return this;
	}
	
	/**
	 * Chuyển từ biến testcase sang dạng khai báo hàm trong z3
	 * @param var biến testcase cần chuyển
	 * @param declare dự định để khai báo (declare) hoặc tạo (define)
	 * @return hàm ứng với biến testcase
	 */
	private static Z3.Func toZ3Func(IVariable var, boolean declare){
		IType type = var.getType();
		Z3.Func func = null;
		
		if (type instanceof ArrayType){
			int count = 0;
			
			while (type instanceof ArrayType){
				count++;
				type = ((ArrayType) type).getSubType();
			}
			
			func = new Z3.Func(var.getName(), toSmt2(type));
			for (int i = 1; i <= count; i++)
				func.addParameter(declare ? "Int" : String.format("(x!%d Int)", i));
		} 
		
		else {
			func = new Z3.Func(var.getName(), toSmt2(type));
		}
		return func;
	}
	
	/**
	 * Thêm một điều kiện ràng buộc vào để giải
	 */
	public Z3Solver addCondition(IExpression condition){
		z3.addLine("assert %s", parseCondition(condition));
		return this;
	}
	
	/**
	 * Chuyển từ biểu thức sang string của ngôn ngữ smt2
	 * @param ex biểu thức cần chuyển
	 * @return string trong ngôn ngữ smt2 tương ứng
	 */
	private String parseCondition(IExpression ex){
		
		//Kiểu biểu thức 2 bên
		if (ex instanceof IBinaryExpression){
			IBinaryExpression bin = (IBinaryExpression) ex;
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
			
			//Chuyển / sang div nếu phép chia là phép chia nguyên (3/2 = 1)
			else if (op.equals(BinaryExpression.DIV) 
					&& bin.getType().getSize() <= BasicType.INT_SIZE)
				return String.format("(div %s %s)", op1, op2);
			
			//Các dấu bình thường như +, -, >, <, ... để nguyên
			else 
				return String.format("(%s %s %s)",op, op1, op2);
		} 
		
		//Kiểu biểu thức một bên
		else if (ex instanceof IUnaryExpression){
			IUnaryExpression u = (IUnaryExpression) ex;
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
		else if (ex instanceof IArrayIndexExpression){
			IArrayIndexExpression array = (IArrayIndexExpression) ex;
			String params = "";
			
			for (IExpression index: array.getIndexes())
				params += " " + parseCondition(index);
			return String.format("(%s %s)", array.getName(), params);
		}
		
		//Biểu thức truy cập thuộc tính đối tượng, chuyển đổi sang biến trung gian
		else if (ex instanceof IMemberAccessExpression){
			IMemberAccessExpression member = (IMemberAccessExpression) ex;
			
			//Tìm biểu thức trong danh sách hiện thời
			for (LinkMemberVariable var: linkMembers)
				if (var.getLinkedExpression().equals(member))
					return var.getName();
			
			//Không tìm thấy, tạo biến trung gian mới
			String name = "___" + linkMembers.size() + "___";
			IVariable find = varTable.find(member.getName());
			find.initValueIfNotSet();
			LinkMemberVariable var = new LinkMemberVariable(name, 
					find.object().getMemberType(member.getMemberName()), member);
			
			linkMembers.add(var);
			if (mSolveOk){
				Func func = toZ3Func(var, false);
				func.setValueFromType();
				z3.addFunction(func);
				var.setValue(str2Expression(func.getValue()));
			}
			else
				addVariable(var, false);
			return name;
			
		}
		
		//Kiểu bình thường (tên biến, giá trị hằng), trả về nội dung của biểu thức
		return ex.getContent();
	}
	
	/**
	 * Biến trung gian, được liên kết đến một biểu thức truy cập thuộc tính
	 */
	private static class LinkMemberVariable extends Variable{

		private IMemberAccessExpression mLink;
		
		public LinkMemberVariable(String name, IType type, IMemberAccessExpression link) {
			super(name, type);
			mLink = link;
		}
		
		/**
		 * Trả về biểu thức truy cập thuộc tính được liên kết đến
		 */
		public IMemberAccessExpression getLinkedExpression(){
			return mLink;
		}
	}
	
	protected IVariableTable createVariableTable(){
		return new VariableTable();
	}

	
	private static HashMap<IType, String> smtMap = new HashMap<>();
	
	static{
		smtMap.put(BasicType.INT, "Int");
		smtMap.put(BasicType.LONG, "Int");
		smtMap.put(BasicType.FLOAT, "Real");
		smtMap.put(BasicType.DOUBLE, "Real");
		smtMap.put(BasicType.BOOL, "Bool");
	}
	
	/**
	 * Chuyển từ kiểu trong ngôn ngữ sang kiểu của Z3
	 */
	public static String toSmt2(IType t){
		
		if (smtMap.containsKey(t))
			return smtMap.get(t);
		
		return t.getContent();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Trả về chuỗi tóm tắt các nghiệm sau khi đã giải, dạng
	 * (name1, name2, ...) = (value1, value2, ...)
	 */
	public static String summarySolution(List<IVariable> solution){
		if (solution.isEmpty())
			return "";
		
		String left ="";
		String right = "";
		
		for (IVariable var: solution){
			left += ", " + var.getName();
			right += ", " + var.getValue();
		}
		
		return String.format("(%s) = (%s)",
				left.substring(2), right.substring(2));
	}
}
