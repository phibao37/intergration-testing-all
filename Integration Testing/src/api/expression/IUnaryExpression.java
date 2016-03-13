package api.expression;

public interface IUnaryExpression extends IExpressionGroup{

	/**
	 * Kiểm tra phép toán nằm ở bên trái của biểu thức con
	 */
	boolean isLeftOperator();

	/**
	 * Kiểm tra đây là một biểu thức gán
	 */
	boolean isAssignOperator();

	/**
	 * Trả về phép toán của biểu thức
	 */
	String getOperator();

	/**
	 * Trả về biểu thức con
	 */
	IExpression getSubElement();

	boolean isConditionExpression();

	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}
}