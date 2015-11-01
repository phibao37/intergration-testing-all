package core.models.expression;

import core.models.Expression;
import core.models.Type;

/**
 * Mô tả một biểu thức chỉ gồm một tên tham chiếu. Tên này được dùng cho khai báo
 * và sử dụng các biến số
 * @author ducvu
 *
 */
public class NameExpression extends Expression implements NamedAttribute {
	
	/**
	 * Vai trò bình thường, tham chiếu đến các biến
	 */
	public static final int ROLE_NORMAL = 0;
	
	/**
	 * Tham gia vai trò tên biến mảng trong một biểu thức truy cập phần tử mảng
	 */
	public static final int ROLE_ARRAY = 1;
	
	/**
	 * Tham gia vai trò tên hàm trong biểu thức gọi hàm
	 */
	public static final int ROLE_FUNCTION = 2;
	
	private Type mType;
	private int mRole = ROLE_NORMAL;
	
	/**
	 * Tạo một biểu thức tên mới
	 * @param name tên tham chiếu
	 */
	public NameExpression(String name){
		super(name);
	}

	/**
	 * Trả về tên của biến tham chiếu
	 */
	@Override
	public String getName() {
		return getContent();
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public void setType(Type type) {
		mType = type;
		if (isCloneExpression())
			((NameExpression)getSource()).setType(type);
	}

	@Override
	public Type getType() {
		return mType;
	}

	/**
	 * Trả về vai trò của biểu thức tên đối với biểu thức cha của nó
	 */
	public int getRole() {
		return mRole;
	}
	
	/**
	 * Gán vai trò của biểu thức tên trong biểu thức cha của nó
	 */
	public void setRole(int role) {
		mRole = role;
	}

}
