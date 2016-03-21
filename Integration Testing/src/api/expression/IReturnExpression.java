package api.expression;

public interface IReturnExpression extends IUnaryExpression {

	/**
	 * Trả về biểu thức sau return
	 */
	IExpression getReturnExpression();
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}

}