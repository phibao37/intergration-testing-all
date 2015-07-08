package core.unit;

import java.util.ArrayList;

import core.eval.Evaluateable;
import core.models.Expression;
import core.models.Variable;

/**
 * Bảng lưu trữ danh sách các biến số và thực thi các tác vụ như:
 * <ul>
 * 	<li>cập nhật giá trị cho các biến số</li>
 * 	<li>thay thế các biểu thức cho trước bởi các biến số mà nó đang lưu giữ</li>
 * </ul>
 * @author ducvu
 *
 */
public class VariableTable extends ArrayList<Variable> {
	private static final long serialVersionUID = -1048482067823015287L;
	
	//TODO xay dung bang bien
	
	/**
	 * Thay thế các biến trong bảng có mặt trong biểu thức bằng giá trị của các biến đó 
	 * @param expression biểu thức cần thay thế
	 * @return biểu thức đã được thay thế
	 */
	public Expression fillExpression(Expression expression){
		return null;
	}
	
	/**
	 * Thực hiện {@link #fillExpression(Expression)}, sau đó tính toán để rút gọn biểu thức
	 * @param expression biểu thức cần thay thế và tính toán
	 * @return biểu thức đã được tính toán
	 */
	public Expression evalExpression(Expression expression){
		return DEFAULT_EVAL.evalExpression(fillExpression(expression));
	}
	
	private static Evaluateable DEFAULT_EVAL;
	
	/**
	 * Thiết đặt bộ tính toán giá trị mặc định
	 * @param eval bộ tính toán giá trị biểu thức
	 */
	public static void setDefaultEval(Evaluateable eval){
		DEFAULT_EVAL = eval;
	}
}
