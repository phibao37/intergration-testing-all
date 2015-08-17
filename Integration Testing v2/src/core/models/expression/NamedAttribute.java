package core.models.expression;

import core.models.Type;

/**
 * Mô phỏng các biểu thức có chứa tên
 * @author ducvu
 *
 */
public interface NamedAttribute {
	
	/**
	 * Trả về thuộc tính tên của biểu thức
	 */
	public String getName();
	
	/**
	 * Gán kiểu của dữ liệu đươc liên kết với biểu thức
	 */
	public void setType(Type type);
	
	/**
	 * Trả về kiểu của dữ liệu được liên kết với biểu thức
	 */
	public Type getType();
}
