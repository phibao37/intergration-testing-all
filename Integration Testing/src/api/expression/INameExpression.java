package api.expression;

import api.models.IType;

public interface INameExpression extends IExpression, IContainNameExpression, 
		IRegistterValueUsed {

	/**
	 * Vai trò bình thường, tham chiếu đến các biến
	 */
	final int ROLE_NORMAL = 0;
	/**
	 * Tham gia vai trò tên biến mảng trong một biểu thức truy cập phần tử mảng
	 */
	final int ROLE_ARRAY = 1;
	/**
	 * Tham gia vai trò tên hàm trong biểu thức gọi hàm
	 */
	final int ROLE_FUNCTION = 2;
	/**
	 * Tham gia vai trò tên đối tượng trong biểu thức truy cập thuộc tính
	 */
	final int ROLE_OBJECT = 3;

	/**
	 * Trả về vai trò của biểu thức tên đối với biểu thức cha của nó
	 */
	int getRole();

	/**
	 * Gán vai trò của biểu thức tên trong biểu thức cha của nó
	 */
	void setRole(int role);
	
	void setType(IType type);
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}

}