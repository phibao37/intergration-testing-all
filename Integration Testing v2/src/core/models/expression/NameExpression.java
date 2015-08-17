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
	
	private Type mType;
	
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

}
