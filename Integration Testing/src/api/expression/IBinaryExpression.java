package api.expression;

public interface IBinaryExpression extends IExpressionGroup {

	/**
	 * Trả về biểu thức nằm ở bên trái
	 */
	IExpression getLeft();

	/**
	 * Trả về phép toán của biểu thức
	 */
	String getOperator();

	/**
	 * Trả về biểu thức nằm ở bên phải
	 */
	IExpression getRight();

	/**
	 * Kiểm tra biểu thức gán
	 */
	boolean isAssignOperator();

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