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
		return new IDExpression(mDefault);
	}
	
	
	/**
	 * Kiểu dữ liệu số nguyên
	 */
	public static final BasicType INT = new BasicType("int", 0);
	
	/**
	 * Kiểu dữ liệu số nguyên lớn
	 */
	public static final BasicType LONG = new BasicType("long", 0L);
	
	/**
	 * Kiểu dữ liệu số thực động
	 */
	public static final BasicType FLOAT = new BasicType("float", 0F);
	
	/**
	 * Kiểu dữ liệu số thực
	 */
	public static final BasicType DOUBLE = new BasicType("double", 0D);
	
	/**
	 * Kiểu dữ liệu kí tự
	 */
	public static final BasicType CHAR = new BasicType("char", '0');
	
	
	/**
	 * Kiểu dữ liệu nhị phân
	 */
	public static final BasicType BOOL = new BasicType("bool", false);

	
	/**
	 * Kiểu dữ liệu void (dùng cho trả về của hàm)
	 */
	public static final BasicType VOID = new BasicType("void", null);

}




