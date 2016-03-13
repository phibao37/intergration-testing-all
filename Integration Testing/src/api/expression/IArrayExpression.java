package api.expression;

public interface IArrayExpression extends IExpressionGroup {

	public int length();
	
	public void setElement(int index, IExpression value);
	
	public IExpression getElement(int index);
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}
	
}
