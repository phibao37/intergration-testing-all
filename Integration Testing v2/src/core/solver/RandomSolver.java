package core.solver;

import java.util.ArrayList;
import java.util.Map;

import core.S;
import core.error.CoreException;
import core.eval.SimpleEval;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Type;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.IDExpression;
import core.models.expression.MemberAccessExpression;
import core.models.expression.NameExpression;
import core.models.expression.NamedAttribute;
import core.models.expression.ObjectExpression;
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
	private Variable[] mAfterTestcase;
	
	private VariableTable mTable;
	
	@Override
	public Result solve(ConstraintEquations constraints) throws CoreException {
		Variable[] testcases = constraints.getTestcases();
		ArrayList<ArrayIndexExpression> arrays = constraints.getArrayAccesses();
		constraints = (ConstraintEquations) constraints.clone();
		
		//Khởi tạo bảng biến, reset lại nghiệm
		mTable = new VariableTable();
		mTestcase = null;
		mAfterTestcase = null;
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
						if (name.getRole() == NameExpression.ROLE_NORMAL)
							ref.add(name);
					}

					@Override
					public void leave(ArrayIndexExpression array) {
						ref.add(array);
					}

					@Override
					public void leave(MemberAccessExpression member) {
						ref.add(member);
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
					

					//System.out.println("Before calculate: " + h);
					for (NamedAttribute name: entry.getValue()){
						if (name instanceof NameExpression)
							h.replace((Expression) name, 
									getValue((NameExpression) name));
						else if (name instanceof ArrayIndexExpression) {
							h.replace((Expression) name, 
									getValue((ArrayIndexExpression) name));
						}
						else if (name instanceof MemberAccessExpression){
							h.replace((Expression) name, 
									getValue((MemberAccessExpression)name));
						}
					}

					//System.out.println("calculate: " + h.getElement());
					
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
						if (var.getType().isArrayType() || var.getType().isObjectType())
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
				
				//Tình toán các giá trị sau khi hàm chạy
				mAfterTestcase = constraints.getAfterVariables();
				int i = -1;
				for (Variable var: mAfterTestcase){
					i++;
					
					//Các giá trị không thay đổi, lấy từ nguồn của nó
					if (!var.getType().isValueChangeable() || !var.isValueSet()){
						var.setValue(mTestcase[i].getValue());
						continue;
					}
					
					//Là biến mảng, gán giá trị từng phần tử
					if (var instanceof ArrayVariable){
						ArrayVariable arr = (ArrayVariable) var;
						Map<int[], Expression> elms = arr.getAllValue();
						for (Map.Entry<int[], Expression> entry: elms.entrySet()){
							arr.setValueAt(calculate(entry.getValue()), entry.getKey());
						}
						
						//Các chỉ số chứa ẩn số sẽ được tính toán và ghi đè sau, 
						//không thể hỗ trợ theo đúng thứ tự !!!!
						Map<ArrayList<Expression>, Expression> map =
								arr.getAbstractDatas();
						for (Map.Entry<ArrayList<Expression>, Expression> entry: 
								map.entrySet()){
							ArrayList<Expression> indexes = entry.getKey();
							Expression[] indexs = new Expression[indexes.size()];
							
							for (int j = 0; j < indexs.length; j++)
								indexs[j] = calculate(indexes.get(j));
							
							arr.setValueAt(calculate(entry.getValue()), indexs);
						}
					} else {
						var.setValue(calculate(var.getValue()));
					}
				}
				

				mSolutionStr = summarySolution(mTable);
				mSolutionCode = Result.SUCCESS;
			}
			
		}
		
		return new Result(mSolutionCode, mSolutionStr, mTestcase, mReturnValue, 
				mAfterTestcase, this);
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
				if (name.getRole() == NameExpression.ROLE_NORMAL)
					h.replace(name, getValue(name));
			}

			@Override
			public void leave(ArrayIndexExpression array) {
				h.replace(array, getValue(array));
			}

			@Override
			public void leave(MemberAccessExpression member) {
				h.replace(member, getValue(member));
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
	 * Lấy giá trị một truy cập thuộc tính đối tượng, hoặc tạo ngẫu nhiên nếu chưa có
	 */
	private Expression getValue(MemberAccessExpression member){
		Variable find = mTable.find(member.getName()).initValueIfNotSet();
		String name = member.getMemberName();
		ObjectExpression object = find.object();
		
		if (object.isMemberSet(name))
			return object.getMember(name);
		
		Expression ep = createRandomValue(object.getMemberType(name));
		try {
			object.setMember(name, ep);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ep;
	}
	
	/**
	 * Tạo giá trị ngẫu nhiên từ một biến số có kiểu nhất định
	 */
	private static IDExpression createRandomValue(Variable var){
		return createRandomValue(var.getDataType());
	}
	
	/**
	 * Tạo giá trị ngẫu nhiên từ một kiểu nhất định
	 * @param type kiểu giá trị, đang hỗ trợ BasicType
	 */
	private static IDExpression createRandomValue(Type type){
		return new IDExpression(RandomGenarator.forType((BasicType) type));
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

	@Override
	public String getContent() {
		return "Tạo giá trị ngẫu nhiên trong khoảng nhất định cho tất cả các biến số"
				+ "<br/> đến khi tất cả các ràng buộc đều thỏa mãn";
	}
	
}
