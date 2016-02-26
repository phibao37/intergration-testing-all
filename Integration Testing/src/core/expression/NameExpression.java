package core.expression;

import api.models.IType;


/**
 * Mô tả một biểu thức chỉ gồm một tên tham chiếu. Tên này được dùng cho khai báo
 * và sử dụng các biến số
 * @author ducvu
 *
 */
public class NameExpression extends Expression {
	
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
	
	/**
	 * Tham gia vai trò tên đối tượng trong biểu thức truy cập thuộc tính
	 */
	public static final int ROLE_OBJECT = 3;
	
	private IType mType;
	private int mRole = ROLE_NORMAL;
	
	/**
	 * Tạo một biểu thức tên mới
	 * @param name tên tham chiếu
	 */
	public NameExpression(String name){
		super(name);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	public void setType(IType type) {
		mType = type;
		if (isCloneExpression())
			((NameExpression)getSource()).setType(type);
	}

	@Override
	public IType getType() {
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
