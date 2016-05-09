package core.solver.random;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import api.expression.IArrayIndexExpression;
import api.expression.IExpression;
import api.expression.IExpressionEval;
import api.expression.IExpressionGroup;
import api.expression.IMemberAccessExpression;
import api.expression.INameExpression;
import api.expression.INumberExpression;
import api.expression.IObjectExpression;
import api.models.IType;
import api.models.IVariable;
import api.solver.IPathConstraints;
import api.solver.ISolution;
import api.solver.ISolver;
import api.solver.IVariableTable;
import core.Config;
import core.expression.ExpressionEval;
import core.expression.ExpressionVisitor;
import core.expression.NumberExpression;
import core.expression.StringExpression;
import core.models.ArrayVariable;
import core.models.type.ArrayType;
import core.models.type.BasicType;
import core.solver.Solution;
import core.solver.VariableTable;

public class RandomSolver implements ISolver {

	public static final String 
			RESULT_UNSAT = "unsat",
			RESULT_UNKNOWN = "unknown";

	private IVariableTable varTable;
	private IExpressionEval epEval = new ExpressionEval();
	
	@Override
	public ISolution solveConstraint(IPathConstraints constraint) {
		IVariable[] solution = null;
		int code = ISolution.UNKNOWN;
		String message = RESULT_UNKNOWN;
		List<IExpression> logicCnt = constraint.getLogicConstraints();
		List<IArrayIndexExpression> array = constraint.getArrayAccess();
		IExpression returnValue = null;
		boolean isNormalConstraint = 
				constraint.getConstraintType() == IPathConstraints.TYPE_NORMAL;
		IVariableTable untouchTable = createVariableTable();
		
		//System.out.println("\nHPT: " + logicCnt);
		
		//Tính toán các ràng buộc không chứa biến,
		//nếu kết quả luôn sai thì cả hệ vô nghiệm (thí dụ chứa 1 < 0)
		if (hasNoSolution(logicCnt)){
			code = ISolution.UNSATISFIED;
			message = RESULT_UNSAT;
		}
		
		else {
			//Nhóm các ràng buộc liên quan lại với nhau
			List<List<IExpression>> group = groupConstraint(logicCnt);
			int loop = Config.RAND_LOOP, currentGroup = 0;
			
			//Thêm các khai báo biến vào bảng biến
			for (IVariable input: constraint.getParameters())
				untouchTable.add(input.clone());
			varTable = untouchTable;
			
//			for (int i = 0; i < group.size(); i++)
//				System.out.println("Group " + i + ": " + group.get(i));
			
			//Bắt đầu quá trình tạo giá trị ngẫu nhiên và giải
			while (--loop >= 0 && currentGroup < group.size()){
				boolean feasible = true;
				List<IExpression> listConstraint = group.get(currentGroup);
				varTable = untouchTable.clone();
				
				//Xét từng ràng buộc, tạo giá trị ngẫu nhiên và tính giá trị
				for (int i = 0; i < listConstraint.size(); i++){
					IExpression cnt = listConstraint.get(i);
					
					try {
						//Tính toán biểu thức xem trả về True hay False
						INumberExpression r = calculate(cnt);
						
						if (!r.boolValue()){
							feasible = false;
							break;
						}
					}
					
					catch (ArithmeticException e){
						feasible = false;
						break;
					}
				}
				
				if (feasible){
					untouchTable = varTable;
					currentGroup++;
				}
			}
			
			//Chưa đi hết vòng lặp, có nghiệm
			if (loop >= 0){
				//Đặt kết quả
				solution = new IVariable[varTable.size()];
				varTable.toArray(solution);
				for (IVariable input: solution)
					if (!input.isValueSet()){
						if (input.getType() instanceof BasicType)
							input.setValue(randomForVariableData(input));
						else
							input.initValueIfNotSet();
					}
				
				for (IArrayIndexExpression arrayAccess: array)
					try{
						calculate(arrayAccess);
					} catch (ArithmeticException e) {}
				
				if (isNormalConstraint){
					if (constraint.getReturnExpression() != null)
						returnValue = calculate(constraint.getReturnExpression());
					code = ISolution.SATISFY;
				} 
				
				else {
					code = ISolution.ERROR;
					String error = null;
					
					switch (constraint.getConstraintType()){
					case IPathConstraints.TYPE_DIVIDE_ZERO:
						error = "Division by 0"; break;
					}
					returnValue = new StringExpression(error);
				}
				
				message = summarySolution(varTable);
			}
		}
		
		return new Solution(solution, code, message, returnValue, this);
	}
	
	/**
	 * Fill giá trị, tính toán và trả về giá trị kết quả
	 */
	protected INumberExpression calculate(IExpression ex){
		IExpressionGroup group = ex.clone().group();
		
		group.accept(new ExpressionVisitor() {

			@Override
			public void leave(INameExpression name) {
				if (name.getRole() == INameExpression.ROLE_NORMAL)
					group.replaceChild(name, getValue(name));
			}

			@Override
			public void leave(IArrayIndexExpression array) {
				group.replaceChild(array, getValue(array));
			}

			@Override
			public void leave(IMemberAccessExpression member) {
				group.replaceChild(member, getValue(member));
			}
			
		});
		
		return (INumberExpression) epEval.eval(group.ungroup());
	}
	
	/**
	 * Trả về giá trị hiện thời hoặc random giá trị mới cho biến
	 */
	protected IExpression getValue(INameExpression name){
		IVariable find = varTable.find(name.getName());
		if (find.isValueSet())
			return find.getValue();
		
		IExpression value = randomForVariableData(find);
		find.setValue(value);
		return value;
	}
	
	/**
	 * Trả về giá trị hiện thời hoặc random giá trị mới cho phần tử mảng
	 */
	protected IExpression getValue(IArrayIndexExpression arrayAccess){
		IExpression[] indexes = arrayAccess.getIndexes().clone();
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = calculate(indexes[i]);
		
		ArrayVariable array = (ArrayVariable) 
				varTable.find(arrayAccess.getName());
		if (array.isValueSet(indexes))
			return array.getValueAt(indexes);
		
		IExpression value = randomForVariableData(array);
		array.setValueAt(value, indexes);
		return value;
	}
	
	/**
	 * Trả về giá trị hiện thời hoặc random giá trị mới cho thuộc tính đối tượng
	 */
	protected IExpression getValue(IMemberAccessExpression member){
		IVariable find = varTable.find(member.getName()).initValueIfNotSet();
		String name = member.getMemberName();
		IObjectExpression object = find.object();
		
		if (object.isMemberSet(name))
			return object.getMember(name);
		
		IExpression ep = randomForType(object.getMemberType(name));
		try {
			object.setMember(name, ep);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ep;
	}
	
	protected IExpression randomForVariableData(IVariable var){
		IType t = var.getType();
		
		if (t instanceof ArrayType) {
			while (t instanceof ArrayType)
				t = ((ArrayType)t).getSubType();
		}
		
		return randomForType(t);
	}
	
	protected IExpression randomForType(IType type){
		return new NumberExpression(RandomGenarator.forType((BasicType) type));
	}
	
	/**
	 * Nhóm các ràng buộc có liên quan lại với nhau
	 */
	protected List<List<IExpression>> groupConstraint(
			List<IExpression> listConstraint){
		List<List<IExpression>> group = new ArrayList<>(),
				groupRefAll = new ArrayList<>();
		
		for (IExpression constraint: listConstraint){
			List<IExpression> refList = new ArrayList<>();
			CheckRefVisitor visitor = new CheckRefVisitor(){

				@Override
				public void leave(INameExpression name) {
					if (name.getRole() == INameExpression.ROLE_NORMAL){
						refList.add(name);
						addIndex(checkRefInList(groupRefAll, name));
					}
				}

				@Override
				public void leave(IArrayIndexExpression array) {
					refList.add(array);
					addIndex(checkRefInList(groupRefAll, array));
				}

				@Override
				public void leave(IMemberAccessExpression member) {
					refList.add(member);
					addIndex(checkRefInList(groupRefAll, member));
				}
				
			};
			constraint.accept(visitor);
			
			TreeSet<Integer> indexList = visitor.getIndexList();
			
			//Không có nhóm nào chứa các biểu thức tham chiếu,
			//cho ràng buộc vào nhóm mới
			if (indexList.isEmpty()){
				List<IExpression> newGroup = new ArrayList<>();
				newGroup.add(constraint);
				
				group.add(newGroup);
				groupRefAll.add(refList);
			}
			
			//Có 1 hoặc nhiều nhóm chứa chung tham chiếu, 
			//gộp các nhóm và cho ràng buộc vào nhóm này
			else {
				Integer[] list = new Integer[indexList.size()];
				indexList.toArray(list);
				int index = list[0];
				
				group.get(index).add(constraint);
				groupRefAll.get(index).addAll(refList);
				
				//Gộp các nhóm phía sau làm một
				for (int i = list.length - 1; i > 0; i--){
					int removeIndex = list[i];
					
					group.get(index).addAll(group.get(removeIndex));
					group.remove(removeIndex);
					
					groupRefAll.get(index).addAll(groupRefAll.get(removeIndex));
					groupRefAll.remove(removeIndex);
				}
			}
		}
		
		return group;
	}
	
	/**
	 * Kiểm tra một tham chiếu có trong hệ tham chiếu không, hoặc trả về -1
	 */
	protected int checkRefInList(List<List<IExpression>> groupRef, 
			IExpression ref){
		int index = 0;
		for (List<IExpression> group: groupRef){
			int i = group.indexOf(ref);
			
			if (i >= 0){
				return index;
				
			}
			index++;
		}
		return -1;
	}
	
	/**
	 * Kiểm tra hệ ràng buộc vô nghiệm ngay từ đầu (thí dụ có chứa: 1 < 0),
	 * đồng thời loại bỏ các ràng buộc luôn luôn đúng ra khỏi hệ (0 < 1)
	 */
	protected boolean hasNoSolution(List<IExpression> listConstraint){
		
		for (int i = listConstraint.size() - 1; i >= 0; i--){
			IExpression e = listConstraint.get(i);
			
			//Chỉ xét các ràng buộc không chứa biến
			if (!e.isConstant()) continue;
			
			INumberExpression result = (INumberExpression) 
					getExpressionEval().eval(e);
			
			//Ràng buộc này luôn đúng, loại bỏ khỏi danh sách
			if (result.boolValue())
				listConstraint.remove(i);
			
			//Ràng buộc này sai, thông báo hệ vô nghiệm và thoát
			else {
				return true;
			}
		}
		
		return false; 
	}

	protected IVariableTable createVariableTable(){
		return new VariableTable();
	}
	
	protected IExpressionEval getExpressionEval(){
		return epEval;
	}
	
	@Override
	public String getName() {
		return "Random";
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
	
	class CheckRefVisitor extends ExpressionVisitor{
		
		private TreeSet<Integer> indexList = new TreeSet<>();

		public void addIndex(int index){
			if (index != -1)
				indexList.add(index);
		}
		
		public TreeSet<Integer> getIndexList(){
			return indexList;
		}
		
	}

}
