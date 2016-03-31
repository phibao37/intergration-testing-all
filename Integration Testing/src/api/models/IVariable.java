package api.models;

import api.expression.IExpression;

public interface IVariable extends IElement {
	
	/**
	 * Trả về tên của biến số
	 */
	public String getName();
	
	/**
	 * Trả về kiểu của biến số
	 */
	public IType getType();
	
	/**
	 * Gán giá trị dưới dạng biểu thức cho biến số
	 * @param value biểu thức giá trị, có thể là hằng số hoặc có chứa biến số
	 */
	public void setValue(IExpression value);
	
	/**
	 * Trả về giá trị biểu thức của biến số
	 */
	public IExpression getValue();
	
	/**
	 * Kiểm tra biến đã được gán giá trị
	 */
	public default boolean isValueSet(){
		return getValue() != null;
	}
	
	/**
	 * Thiết đặt mức độ khả dụng của biến
	 */
	public void setScope(int scope);
	
	/**
	 * Trả về mức độ khả dụng của biến
	 */
	public int getScope();
	
	/**
	 * Tạo ra một biến mới có cùng giá trị (đã được sao chép) từ biến này
	 */
	@Override
	public IVariable clone();
}
