package api.expression;

import api.models.IType;

public interface IDeclareExpression extends IExpressionGroup {

	/**
	 * Trả về kiểu của khai báo
	 */
	IType getType();

	/**
	 * Trả về danh sách các biểu thức biến được khai báo
	 */
	IExpression[] getDeclares();

	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}
}