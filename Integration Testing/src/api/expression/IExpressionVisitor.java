package api.expression;

import api.models.IStatement;

public interface IExpressionVisitor {

	/**
	 * Bỏ qua việc duyệt các biểu thức khác, thoát hoàn toàn
	 */
	int PROCESS_ABORT = -1;
	/**
	 * Bỏ qua duyệt các biểu thức con, tiếp tục duyệt các biểu thức sau
	 */
	int PROCESS_SKIP = 0;
	/**
	 * Tiếp tục duyệt các biểu thức con, sau đó là các biểu thức đằng sau
	 */
	int PROCESS_CONTINUE = 1;

	/**
	 * Trước khi duyệt qua một biểu thức nào đó
	 * @param expression biểu thức sẽ được duyệt
	 * @return 
	 * <b>true</b>: duyệt qua biểu thức
	 * <b>false</b>: bỏ qua biểu thức
	 */
	boolean preVisit(IExpression expression);

	/**
	 * Sau khi duyệt qua một biểu thức nào đó
	 * @param expression biểu thức đã được duyệt
	 */
	void postVisit(IExpression expression);

	int visit(INameExpression name);
	int visit(IFunctionCallExpression call);
	int visit(IArrayExpression array);
	int visit(IArrayIndexExpression array);
	int visit(IBinaryExpression bin);
	int visit(IUnaryExpression unary);
	int visit(IDeclareExpression declare);
	int visit(INumberExpression number);
	int visit(IMemberAccessExpression member);
	int visit(IStringExpression string);
	int visit(IReturnExpression rt);
	int visit(IObjectExpression obj);
	
	void leave(INameExpression name);
	void leave(IFunctionCallExpression call);
	void leave(IArrayExpression array);
	void leave(IArrayIndexExpression array);
	void leave(IBinaryExpression bin);
	void leave(IUnaryExpression unary);
	void leave(IDeclareExpression declare);
	void leave(INumberExpression number);
	void leave(IMemberAccessExpression member);
	void leave(IStringExpression string);
	void leave(IReturnExpression rt);
	void leave(IObjectExpression obj);

	/**
	 * Được gọi khi đang duyệt qua các câu lệnh trong hàm số
	 * @param statement câu lệnh đang được duyệt qua
	 * @return {@link #PROCESS_CONTINUE} biểu thức gốc sẽ được thăm tiếp theo<br/>
	 * {@link #PROCESS_SKIP} bỏ qua biểu thức gốc, thăm câu lệnh kế tiếp<br/>
	 * {@link #PROCESS_CONTINUE} hủy bỏ quả trình duyệt
	 */
	int visit(IStatement statement);

}