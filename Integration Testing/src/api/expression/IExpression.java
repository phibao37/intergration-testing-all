package api.expression;

import api.models.IElement;

/**
 * Các giao diện của một biểu thức bên trong một câu lệnh đơn
 */
public interface IExpression extends IElement {
	
	/**
	 * @note các bản sao cần giữ một liên kết tới nguồn của nó, sẽ được sử dụng bởi
	 * {@link #equalsSource(IExpression)} và {@link #getSource()}
	 */
	public IExpression cloneElement();
	
	/**
	 * So sánh 2 biểu thức có cùng chung một nguồn
	 */
	public boolean equalsSource(IExpression e);
	
	/**
	 * Trả về nguồn của biểu thức, được xác định như sau:
	 * <ul>
	 * 	<li>Đối tượng được sinh ra bởi constructor: trả về chính đối tượng đó</li>
	 * 	<li>Đối tượng được sinh bởi {@link #cloneElement()}: trả về đối tượng nguồn
	 *  mà nó được sao chép</li>
	 * </ul>
	 */
	public IExpression getSource();
	
	/**
	 * Kiểm tra đây là biểu thức được sao chép từ một đối tượng khác
	 */
	public default boolean isCloneExpression(){
		return getSource() != this;
	}
	
	/**
	 * Đánh dấu biểu thức bị khóa, không cho phép thay thế
	 */
	public IExpression blockReplace(boolean blocked);
	
	/**
	 * Kiểm tra biểu thức bị khóa, không cho phép bị thay thế
	 */
	public boolean isReplaceBlocked();
	
	/**
	 * Trả về <i>true</i> nếu giá trị của biểu thức luôn là một hằng số<br/>
	 * Trả về <i>false</i> nếu trong biểu thức tồn tại các tham số, biến,...
	 */
	public boolean isConstant();
}
