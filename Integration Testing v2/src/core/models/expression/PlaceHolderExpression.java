package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;

/**
 * Kiểu biểu thức bao một biểu thức con duy nhất.<br/>
 * Thường dùng để thay thế biểu thức này bằng một biểu thức khác trong giá trị của biến số
 * @author ducvu
 * @see PlaceHolderExpression#replace(Object, Expression)
 */
public class PlaceHolderExpression extends ExpressionGroup {
	
	/**
	 * Tạo biểu thức bao với biểu thức con của nó
	 * @param subElement biểu thức con cần bao lại
	 */
	public PlaceHolderExpression(Expression subElement){
		super(subElement);
	}
	
	@Override
	protected String generateContent() {
		return String.format("(%s)", getElement());
	}
	
	/**
	 * Trả về biểu thức con được bao bởi biểu thức nhóm này
	 */
	public Expression getElement(){
		return g[0];
	}
}
