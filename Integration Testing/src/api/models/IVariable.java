package api.models;

import api.expression.IExpression;
import api.expression.IObjectExpression;

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
	 * Trả về biểu thức giá trị dạng đối tượng khi biến này là biến cấu trúc
	 * hoặc trả về <i>null</i> nếu không đúng
	 */
	public default IObjectExpression object(){
		IExpression value = getValue();
		if (value instanceof IObjectExpression)
			return (IObjectExpression) value;
		else
			return null;
	}
	
	/**
	 * Kiểm tra biến có chứa giá trị dạng đối tượng
	 */
	public default boolean hasObject(){
		return object() != null;
	}
	
	/**
	 * Thiết đặt giá trị cho biến nếu như nó chưa có giá trị
	 */
	public default IVariable initValueIfNotSet(){
		if (!isValueSet())
			setValue(getType().getDefaultValue());
		return this;
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
