package core.unit;

import java.util.ArrayList;

import core.models.Expression;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.solver.Solver;

/**
 * Một bộ lưu trữ các ràng buộc dùng để giải ra testcase cho một đường thi hành.<br/>
 * Bộ này bao gồm: 
 * <ul>
 * 	<li>{@link #getTestcases()}: danh sách các biến testcase cần giải</li>
 * 	<li>{@link #get(int)}: bản thân bộ lưu trữ là môt danh sách các ràng buộc logic</li>
 * 	<li>{@link #getArrayAccesses()}: các biểu thức truy cập vào các 
 *  biển mảng testcase</li>
 * 	<li>{@link #getReturnExpression()}: biểu thức trả về ứng với đường thi hành</li>
 * </ul>
 * @see Solver#solve(ConstraintEquations)
 */
public class ConstraintEquations extends ArrayList<Expression> {
	private static final long serialVersionUID = 2991049972861726397L;
	
	private Variable[] mTestcases;
	private ArrayList<ArrayIndexExpression> mArray;
	private Expression mReturn;
	private Variable[] mAfterVariables;
	
	/**
	 * Tạo một hệ ràng buộc mới
	 * @param testcases danh sách các biến số cần được giải trong ràng buộc
	 * @param afterVariables danh sách các biến số đầu vào mang giá trị sau khi
	 * hàm kiểm thử đã thực hiện xong
	 */
	public ConstraintEquations(Variable[] testcases, Variable[] afterVariables){
		mTestcases = testcases;
		mAfterVariables = afterVariables;
		mArray = new ArrayList<>();
	}
	
	/**
	 * Thêm một biểu thức truy cập vào các biến mảng. Chỉ các biến testcase sẽ được lưu
	 * @param array biểu thức truy cập mảng
	 */
	public void addArrayAccess(ArrayIndexExpression array){
		for (Variable testcase: mTestcases)
			if (testcase.getName().equals(array.getName())){
				mArray.add(array);
				break;
			}
	}
	
	/**
	 * Lấy danh sách các biến số cần giải
	 */
	public Variable[] getTestcases(){
		return mTestcases;
	}
	
	/**
	 * Trả về danh sách các biểu thức truy cập biến mảng testcase
	 */
	public ArrayList<ArrayIndexExpression> getArrayAccesses(){
		return mArray;
	}
	
	/**
	 * Đặt biểu thức cho câu lệnh return hàm (nếu có)
	 */
	public void setReturnExpression(Expression value){
		mReturn = value;
	}
	
	/**
	 * Trả về các biến số đầu vào mang giá trị sau khi hàm số được thực hiện 
	 */
	public Variable[] getAfterVariables(){
		return mAfterVariables;
	}
	
	/**
	 * Trả về biểu thức bên trong câu lệnh return (nếu có)
	 */
	public Expression getReturnExpression(){
		return mReturn;
	}
}
