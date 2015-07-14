package core.unit;

import java.util.ArrayList;

import core.models.Expression;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;

/**
 * Một tập các biểu thức ràng buộc 
 *
 */
public class ConstraintEquations extends ArrayList<Expression> {
	private static final long serialVersionUID = 2991049972861726397L;
	
	private Variable[] mTestcases;
	private ArrayList<ArrayIndexExpression> mArray;
	private Expression mReturn;
	
	/**
	 * Tạo một hệ ràng buộc mới
	 * @param testcases danh sách các biến số cần được giải trong ràng buộc
	 */
	public ConstraintEquations(Variable[] testcases){
		mTestcases = testcases;
		mArray = new ArrayList<ArrayIndexExpression>();
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
	public void setReturnValue(Expression value){
		mReturn = value;
	}
	
	/**
	 * Trả về biểu thức bên trong câu lệnh return (nếu có)
	 */
	public Expression getReturnValue(){
		return mReturn;
	}
}
