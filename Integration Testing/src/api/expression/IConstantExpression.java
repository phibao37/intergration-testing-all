package api.expression;

/**
 * Các biểu thức hằng
 */
public interface IConstantExpression extends IExpression {
	
	@Override
	public default boolean isConstant(){
		return true;
	}
}
