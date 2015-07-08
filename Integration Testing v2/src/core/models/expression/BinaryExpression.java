package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;

/**
 * Mô tả một biểu thức nhị phân, bao gồm một dấu phép toán và hai
 * biểu thức con ở hai bên dấu phép toán
 * @example
 * <ul>
 * 	<li>x == y: phép toán so sánh bằng</li>
 * 	<li>x = y: phép toán gán</li>
 * 	<li>(x < 1) && isValid(): phép toán logic AND</li>
 * </ul>
 * @author ducvu
 *
 */
public class BinaryExpression extends ExpressionGroup {
	
	private String mOperator;
	
	/**
	 * Tạo một biểu thức nhị phân cùng với 2 biểu thức con và phép toán
	 * @param leftElement biểu thức biểu thức ở bên trái phép toán
	 * @param operator chuỗi hiển thị của phép toán
	 * @param rightElement biểu thức biểu thức ở bên phải phép toán
	 */
	public BinaryExpression(Expression leftElement, String operator, Expression rightElement){
		super(leftElement, rightElement);
		mOperator = operator;
	}

	@Override
	protected String generateContent() {
		return String.format("(%s%s%s)", getLeft(), getOperator(), getRight());
	}
	
	/**
	 * Trả về biểu thức nằm ở bên trái
	 */
	public Expression getLeft(){
		return g[0];
	}
	
	/**
	 * Trả về phép toán của biểu thức
	 */
	public String getOperator(){
		return mOperator;
	}
	
	/**
	 * Trả về biểu thức nằm ở bên phải
	 */
	public Expression getRight(){
		return g[1];
	}
}
