package core.models.expression;

import core.models.Expression;
import core.visitor.ExpressionVisitor;

/**
 * Mô tả một biểu thức chỉ gồm một tên tham chiếu. Tên này được dùng cho khai báo
 * và sử dụng các biến số
 * @author ducvu
 *
 */
public class NameExpression extends Expression implements NamedAttribute {
	
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
	protected int handle(ExpressionVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean isConstant() {
		return false;
	}
	
	
}
