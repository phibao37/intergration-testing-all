package core.solver;

import java.util.ArrayList;
import core.S;
import core.error.CoreException;
import core.eval.SimpleEval;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.models.expression.NamedAttribute;
import core.models.expression.PlaceHolderExpression;
import core.models.type.BasicType;
import core.unit.ConstraintEquations;
import core.unit.VariableTable;
import core.visitor.ExpressionVisitor;
import javafx.util.Pair;

/**
 * Giải hệ ràng buộc bằng phương pháp sinh giá trị ngẫu nhiên
 */
public class RandomSolver extends Solver {

	public static final String RESULT_UNSAT = "unsat";
	public static final String RESULT_UNKNOWN = "unknown";
	
	/**
	 * Bộ giải hệ theo random mặc định
	 */
	static final RandomSolver DEFAULT = new RandomSolver();
	
	private Variable[] mTestcase;
	private String mSolutionStr;
	private int mSolutionCode;
	private Expression mReturnValue;
	
	private VariableTable mTable;
	
	@Override
	public Result solve(ConstraintEquations constraints) throws CoreException {
		Variable[] testcases = constraints.getTestcases();
		ArrayList<ArrayIndexExpression> arrays = constraints.getArrayAccesses();
		constraints = (ConstraintEquations) constraints.clone();
		
		//Khởi tạo bảng biến, reset lại nghiệm
		mTable = new VariableTable();
		mTestcase = null;
		mSolutionCode = Result.UNKNOWN;
		mSolutionStr = RESULT_UNKNOWN;
		mReturnValue = null;
		
		//Tính toán các biểu thức hằng số trước, nếu biểu thức sai thì hệ vô nghiệm
		if (hasNoSolution(constraints)){
			mSolutionCode = Result.ERROR;
			mSolutionStr = RESULT_UNSAT;
		}
		else{
			ArrayList<Pair<Expression, ArrayList<NamedAttribute>>> refMap = 
					new ArrayList<>();
			
			//Duyệt qua các điều kiện để tìm các biến cần tạo giá trị
			for (Expression ep: constraints){
				//System.out.println(" ? "  + ep);
				ArrayList<NamedAttribute> ref = new ArrayList<>();
				
				ep.accept(new ExpressionVisitor() {
					@Override
					public void leave(NameExpression name) {
						ref.add(name);
					}

					@Override
					public void leave(ArrayIndexExpression array) {
						ref.add(array);
					}
				});
				
				refMap.add(new Pair<Expression, ArrayList<NamedAttribute>>(ep, ref));
			}
			
			int loop = S.RAND_LOOP;
			
			//Bắt đầu quá trình tạo giá trị ngẫu nhiên và giải
			while (--loop >= 0){
				for (Variable var: testcases)
					mTable.add(var.clone());
				boolean feasible = true;

				//Thay thế và tính toán từng ràng buộc
				for (Pair<Expression, ArrayList<NamedAttribute>> entry: refMap){
					Expression constraint = entry.getKey().clone();
					PlaceHolderExpression h = new PlaceHolderExpression(constraint);
					
					for (NamedAttribute name: entry.getValue()){
						if (name instanceof NameExpression)
							h.replace((Expression) name, 
									getValue((NameExpression) name));
						else {
							h.replace((Expression) name, 
									getValue((ArrayIndexExpression) name));
						}
					}
					
					try{
						IDExpression result = SimpleEval.calculate(h.getElement());
						if (!result.boolValue()){
							//System.out.println("False\n");
							feasible = false;
							break;
						}
					} catch (ArithmeticException e){
						feasible = false;
						break;
					}
				}
				
				if (feasible)
					break;
				mTable.clear();
			}
			
			//Chưa đi hết vòng lặp, có nghiệm
			if (loop >= 0){
				//Đặt kết quả
				mTestcase = new Variable[testcases.length];
				mTable.toArray(mTestcase);
				for (Variable var: mTestcase)
					if (!var.isValueSet()){
						if (var instanceof ArrayVariable)
							var.initValueIfNotSet();
						else
							var.setValue(createRandomValue(var));
					}
				
				for (ArrayIndexExpression array: arrays)
					calculate(array);
				
				mReturnValue = constraints.getReturnExpression();
				if (mReturnValue != null)
					try{
						mReturnValue = calculate(mReturnValue);
					} catch (ArithmeticException e){
						mReturnValue = new IDExpression("Infinity");
					}

				mSolutionStr = summarySolution(mTable);
				mSolutionCode = Result.SUCCESS;
			}
			
		}
		
		return new Result(mSolutionCode, mSolutionStr, mTestcase, mReturnValue, this);
	}
	
	/**
	 * Tính toán, tạo giá trị và trả về kết quả biểu thức
	 * @param e biểu thức cần tính toán
	 * @param positive yêu cầu giá trị biểu thức phải không âm
	 * @return giá trị sau khi tính toán
	 * @throws CoreException có yêu cầu tính toán biểu thúc không âm nhưng không
	 * thể sinh ngẫu nhiên ra được
	 */
	private IDExpression calculate(Expression e){
		PlaceHolderExpression h = new PlaceHolderExpression(e.clone());
		e.accept(new ExpressionVisitor() {
			
			@Override
			public void leave(NameExpression name) {
				h.replace(name, getValue(name));
			}

			@Override
			public void leave(ArrayIndexExpression array) {
				h.replace(array, getValue(array));
			}
		});
		
		return SimpleEval.calculate(h.getElement());
	}
	
	/**
	 * Lấy giá trị một biến theo tên, hoặc tạo ngẫu nhiên nếu chưa có
	 */
	private Expression getValue(NameExpression name){
		Variable find = mTable.find(name.getName());
		if (find.isValueSet())
			return find.getValue();
		
		Expression ep = createRandomValue(find);
		//System.out.printf("%s = %s\n", find.getName(), ep);
		find.setValue(ep);
		return ep;
	}
	
	/**
	 * Lấy giá trị một truy cập mảng, hoặc tạo ngẫu nhiên nếu chưa có
	 */
	private Expression getValue(ArrayIndexExpression array){
		Expression[] indexes = array.getIndexes().clone();
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = calculate(indexes[i]);
		
		ArrayVariable find = (ArrayVariable) mTable.find(array.getName());
		if (find.isValueSet(indexes))
			return find.getValueAt(indexes);
		
		Expression ep = createRandomValue(find);
		//System.out.printf("%s[%s] = %s\n", find.getName(), 
		//Utils.merge("][", indexes), ep);
		find.setValueAt(ep, indexes);
		return ep;
	}
	
	/**
	 * Tạo giá trị ngẫu nhiên từ một biến số có kiểu nhất định
	 */
	private static IDExpression createRandomValue(Variable var){
		Object value = RandomGenarator.forType((BasicType) var.getDataType());
		return new IDExpression(value);
	}
	
	/**
	 * Kiểm tra hệ ràng buộc vô nghiệm ngay từ đầu (thí dụ có chứa: 1 < 0),
	 * đồng thời loại bỏ các ràng buộc luôn luôn đúng ra khỏi hệ (0 < 1)
	 */
	private static boolean hasNoSolution(ArrayList<Expression> list){
		boolean noSolution = false;
		
		for (int i = list.size() - 1; i >= 0; i--){
			Expression e = list.get(i);
			if (!e.isConstant()) continue;
			//Chỉ xét các ràng buộc không chứa biến
			
			IDExpression result = SimpleEval.calculate(e);
			
			//Ràng buộc này luôn đúng, loại bỏ khỏi danh sách
			if (result.boolValue())
				list.remove(i);
			
			//Ràng buộc này sai, thông báo vô nghiệm và thoát
			else {
				noSolution = true;
				break;
			}
		}
		
		return noSolution; 
	}
	
}
