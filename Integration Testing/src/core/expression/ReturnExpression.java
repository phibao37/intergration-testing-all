package core.expression;

import api.expression.IExpression;
import api.expression.IReturnExpression;

/**
 * Mô tả biểu thức return
 * @author ducvu
 *
 */
public class ReturnExpression extends UnaryExpression implements IReturnExpression {
	
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

	/* (non-Javadoc)
	 * @see core.expression.IReturnExpression#getReturnExpression()
	 */
	@Override
	public IExpression getReturnExpression(){
		return getSubElement();
	}
}
