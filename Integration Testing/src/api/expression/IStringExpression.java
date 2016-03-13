package api.expression;

public interface IStringExpression extends IConstantExpression {
	
	public String stringValue();
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}
}
