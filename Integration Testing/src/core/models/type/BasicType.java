package core.models.type;

import core.expression.NumberExpression;
import api.expression.IExpression;

/**
 * Các kiểu cơ bản dùng trong ngôn ngữ lập trình
 *
 */
public class BasicType extends Type {
	private Object mDefault;
	
	/**
	 * Tạo kiểu cơ bản cùng với dạng hiển thị của nó
	 * @param content chuỗi hiển thị của kiểu
	 * @param defaultValue giá trị mặc định
	 * @param size cỡ của kiểu
	 */
	protected BasicType(String content, Object defaultValue, int size){
		super(content, size);
		mDefault = defaultValue;
	}
	
	@Override
	public IExpression getDefaultValue() {
		return new NumberExpression(mDefault);
	}
	
	/**
	 * Kích thước cho kiểu nhị phân
	 */
	public static final int BOOL_SIZE = 1;
	
	/**
	 * Kích thước cho kiểu kí tự
	 */
	public static final int CHAR_SIZE = 2;
	
	/**
	 * Kích thước cho kiểu số nguyên
	 */
	public static final int INT_SIZE = 3;
	
	/**
	 * Kích thước cho kiểu số nguyên lớn
	 */
	public static final int LONG_SIZE = 4;
	
	/**
	 * Kích thước cho kiểu dấu phẩy động
	 */
	public static final int FLOAT_SIZE = 5;
	
	/**
	 * Kích thước cho kiểu số thực
	 */
	public static final int DOUBLE_SIZE = 6;
	
	/**
	 * Kiểu dữ liệu nhị phân
	 */
	public static final BasicType BOOL = new BasicType("bool", false, BOOL_SIZE);
	
	/**
	 * Kiểu dữ liệu kí tự
	 */
	public static final BasicType CHAR = new BasicType("char", '0', CHAR_SIZE);
	
	/**
	 * Kiểu dữ liệu số nguyên
	 */
	public static final BasicType INT = new BasicType("int", 0, INT_SIZE);
	
	/**
	 * Kiểu dữ liệu số nguyên lớn
	 */
	public static final BasicType LONG = new BasicType("long", 0L, LONG_SIZE);
	
	/**
	 * Kiểu dữ liệu số thực động
	 */
	public static final BasicType FLOAT = new BasicType("float", 0F, FLOAT_SIZE);
	
	/**
	 * Kiểu dữ liệu số thực
	 */
	public static final BasicType DOUBLE = new BasicType("double", 0D, DOUBLE_SIZE);

	/**
	 * Kiểu dữ liệu void (dùng cho trả về của hàm)
	 */
	public static final BasicType VOID = new BasicType("void", null, 0);
	

	public static final BasicType[] LIST_BASIC_TYPE = {BOOL, CHAR, INT, LONG, FLOAT, 
		DOUBLE, VOID};
	
}




