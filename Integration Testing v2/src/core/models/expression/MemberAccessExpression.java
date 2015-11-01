package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
import core.models.Type;

/**
 * Biểu thức truy cập thuộc tính của một đối tượng, thí dụ:
 * <pre>
 * a.x
 * b.y()
 * </pre>
 *
 */
public class MemberAccessExpression extends ExpressionGroup implements NamedAttribute {

	private String mMember;
	private boolean isDot;
	private Type mType;
	
	/**
	 * Tạo một biểu thức truy cập thuộc tính từ biểu thức đối tượng và tên thuộc tính
	 * @param parent đối tượng
	 * @param member tên của thuộc tính
	 * @param dot biểu thức truy cập theo dấu "." thay vì "->"
	 */
	public MemberAccessExpression(Expression parent, String member, boolean dot) {
		super(parent);
		mMember = member;
	}
	
	@Override
	protected String generateContent() {
		return g[0] + (isDot ? "." : "->") + mMember;
	}
	
	/**
	 * Kiểm tra thuộc tính được truy cập bằng "." thay vì "->" 
	 */
	public boolean isDotAccess(){
		return isDot;
	}
	
	/**
	 * Trả về tên của thuộc tính
	 */
	public String getMemberName(){
		return mMember;
	}
	
	@Override
	/**
	 * Trả về chuối hiển thị của đối tượng trong biểu thức
	 */
	public String getName() {
		return g[0].getContent();
	}
	
	@Override
	public Type getType() {
		return mType;
	}

	@Override
	public void setType(Type type) {
		mType = type;
		if (isCloneExpression())
			((NameExpression)getSource()).setType(type);
	}

}
