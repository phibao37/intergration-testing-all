package core.expression;

import api.expression.IExpression;
import api.expression.INameExpression;
import api.models.IType;


/**
 * Mô tả một biểu thức chỉ gồm một tên tham chiếu. Tên này được dùng cho khai báo
 * và sử dụng các biến số
 * @author ducvu
 *
 */
public class NameExpression extends Expression implements INameExpression {
	
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

	@Override
	public int getRole() {
		return mRole;
	}
	
	@Override
	public void setRole(int role) {
		mRole = role;
	}

	@Override
	public IExpression getNameExpression() {
		return this;
	}


	private OnValueUsed valueUsed;
	
	@Override
	public void setOnValueUsedOne(OnValueUsed listener) {
		valueUsed = listener;
	}

	@Override
	public void notifyValueUsed() {
		if (valueUsed != null){
			valueUsed.valueUsed(this);
		}
		valueUsed = null;
	}

}
