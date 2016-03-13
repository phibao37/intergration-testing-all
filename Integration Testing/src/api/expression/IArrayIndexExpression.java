package api.expression;


public interface IArrayIndexExpression extends IExpressionGroup, 
		IContainNameExpression {

	
	/**
	 * Trả về danh sách các chỉ số truy cập mảng
	 */
	IExpression[] getIndexes();

	/**
	 * Thiết đặt đây là một biểu thức khai báo mảng: int a[1][] = ...
	 */
	IArrayIndexExpression setDeclare();

	/**
	 * Kiểm tra đây là biểu thức trong khai báo
	 */
	boolean isDeclare();
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}

}