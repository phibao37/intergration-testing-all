package core.expression;

import api.expression.IExpression;

/**
 * Mô tả biểu thức return
 * @author ducvu
 *
 */
public class ReturnExpression extends UnaryExpression {
	
	public static final String RETURN = "return";
	
	/**
	 * Tạo một biểu thức RETURN với biểu thức con sẽ được trả về
	 * @param subElement biểu thức cần return hoặc null
	 */
	public ReturnExpression(IExpression subElement) {
		super(RETURN, subElement);
	}

	@Override
	protected String generateContent() {
		return String.format("%s %s", RETURN, getReturnExpression());
	}

	/**
	 * Trả về biểu thức sau return
	 */
	public IExpression getReturnExpression(){
		return getSubElement();
	}
}
