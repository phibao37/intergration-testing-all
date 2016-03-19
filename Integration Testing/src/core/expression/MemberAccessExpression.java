package core.expression;

import api.expression.IExpression;
import api.expression.IMemberAccessExpression;
import api.models.IType;


/**
 * Biểu thức truy cập thuộc tính của một đối tượng, thí dụ:
 * <pre>
 * a.x
 * b.y()
 * </pre>
 *
 */
public class MemberAccessExpression extends ExpressionGroup implements IMemberAccessExpression {

	private String mMember;
	private boolean isDot;
	private IType mType;
	
	/**
	 * Tạo một biểu thức truy cập thuộc tính từ biểu thức đối tượng và tên thuộc tính
	 * @param parent đối tượng
	 * @param member tên của thuộc tính
	 * @param dot biểu thức truy cập theo dấu "." thay vì "->"
	 */
	public MemberAccessExpression(Expression parent, String member, boolean dot) {
		super(parent);
		mMember = member;
		isDot = dot;
		
		//if (parent instanceof NameExpression)
			//((NameExpression) parent).setRole(NameExpression.ROLE_OBJECT);
	}
	
	@Override
	protected String generateContent() {
		return g[0] + (isDot ? "." : "->") + mMember;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IMemberAccessExpression#isDotAccess()
	 */
	@Override
	public boolean isDotAccess(){
		return isDot;
	}
	

	/* (non-Javadoc)
	 * @see core.expression.IMemberAccessExpression#getMemberName()
	 */
	@Override
	public String getMemberName(){
		return mMember;
	}
	
	@Override
	public IType getType() {
		return mType;
	}

	public void setType(IType type) {
		mType = type;
		if (isCloneExpression())
			((MemberAccessExpression) getSource()).setType(type);
	}


	@Override
	public IExpression getName() {
		return g[0];
	}


}