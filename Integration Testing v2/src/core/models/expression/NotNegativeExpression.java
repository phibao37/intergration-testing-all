package core.models.expression;

import core.models.Expression;

/**
 * Mô tả một biểu thức điều kiện không âm, thí dụ: a >= 0, (x+y) >= 0
 */
public class NotNegativeExpression extends BinaryExpression {

	/**
	 * Tạo một biểu thức điều kiện không âm
	 * @param leftExpression điều kiện bên vế trái
	 */
	public NotNegativeExpression(Expression leftExpression) {
		super(leftExpression, GREATER_EQUALS, new IDExpression("0"));
	}

}
