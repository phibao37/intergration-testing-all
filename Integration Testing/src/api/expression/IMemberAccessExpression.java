package api.expression;

public interface IMemberAccessExpression extends IExpressionGroup, 
		IContainNameExpression, IRegistterValueUsed {

	/**
	 * Kiểm tra thuộc tính được truy cập bằng "." thay vì "->" 
	 */
	boolean isDotAccess();

	/**
	 * Trả về tên của thuộc tính
	 */
	String getMemberName();
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}

}