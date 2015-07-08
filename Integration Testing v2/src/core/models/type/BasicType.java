package core.models.type;

import core.models.Expression;
import core.models.Type;
import core.models.expression.IDExpression;

/**
 * Các kiểu cơ bản dùng trong ngôn ngữ lập trình
 * @author ducvu
 *
 */
public class BasicType extends Type {
	
	private Object mDefault;
	
	/**
	 * Tạo kiểu cơ bản cùng với dạng hiển thị của nó
	 * @param content chuỗi hiển thị của kiểu
	 * @param defaultValue giá trị mặc định
	 */
	protected BasicType(String content, Object defaultValue){
		super(content);
		mDefault = defaultValue;
	}
	
	@Override
	public Expression getDefaultValue() {
		return new IDExpression(String.valueOf(mDefault));
	}
	
	/**
	 * Kiểu dữ liệu số nguyên
	 */
	public static final BasicType INT = new BasicType("int", 0);
	
	/**
	 * Kiểu dữ liệu số thực động
	 */
	public static final BasicType FLOAT = new BasicType("float", 0.0f);
	
	/**
	 * Kiểu dữ liệu số thực
	 */
	public static final BasicType DOUBLE = new BasicType("double", 0.0);
	
	/**
	 * Kiểu dữ liệu kí tự
	 */
	public static final BasicType CHAR = new BasicType("char", '0');
	
	/**
	 * Kiểu dữ liệu chuỗi
	 */
	public static final BasicType STRING = new BasicType("string", "");
	
	/**
	 * Kiểu dữ liệu chuỗi Java
	 */
	public static final BasicType STRING_JAVA = new BasicType("String", "");
	
	/**
	 * Kiểu dữ liệu nhị phân
	 */
	public static final BasicType BOOL = new BasicType("bool", false);
	
	/**
	 * Kiểu dữ liệu nhị phân Java
	 */
	public static final BasicType BOOLEAN = new BasicType("boolean", false);
	
	/**
	 * Kiểu dữ liệu void (dùng cho trả về của hàm)
	 */
	public static final BasicType VOID = new BasicType("void", null);

}




