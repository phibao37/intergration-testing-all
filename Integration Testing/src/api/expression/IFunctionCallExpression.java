package api.expression;

import api.models.IFunction;

public interface IFunctionCallExpression extends IExpressionGroup, 
		IContainNameExpression {

	/**
	 * Trả về danh sách các tham số gọi hàm<br/>
	 * Mảng được truy cập trực tiếp, do vậy, không nên thay đổi các phần tử trong mảng này
	 */
	IExpression[] getArguments();

	/**
	 * Thiết đặt hàm số được cho là đúng với lời gọi hàm này
	 */
	void setFunction(IFunction func);

	/**
	 * Trả về hàm số tương ứng với lời gọi hàm này, hoặc null nếu không có
	 */
	IFunction getFunction();
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}

}