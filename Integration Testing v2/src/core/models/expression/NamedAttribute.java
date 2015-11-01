package core.models.expression;

import core.models.Expression;
import core.models.Type;

/**
 * Mô phỏng các biểu thức có chứa phần tên
 * @author ducvu
 *
 */
public interface NamedAttribute {
	
	/**
	 * Trả về chuỗi hiển thị tên của biểu thức
	 */
	public default String getName(){
		return getNameExpression().getContent();
	}
	
	/**
	 * Trả về biểu thức tương ứng tên của biểu thức
	 */
	public Expression getNameExpression();
	
	/**
	 * Gán kiểu của dữ liệu đươc liên kết với biểu thức
	 */
	public void setType(Type type);
	
	/**
	 * Trả về kiểu của dữ liệu được liên kết với biểu thức
	 */
	public Type getType();
}
