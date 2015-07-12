package core.eval;

import core.models.Expression;
import core.models.Type;
import core.models.expression.IDExpression;
import core.models.expression.PlaceHolderExpression;
import core.models.type.BasicType;
import core.visitor.ExpressionVisitor;

/**
 * Chuyển đổi các số thực rút gọn được sang số nguyên. Thí dụ: 1.00 => 1
 *
 */
public class Float2IntEval implements Evaluateable {
	
	/**
	 * Bộ thực thi chuyển số thực sang số nguyên mặc định
	 */
	public static final Float2IntEval DEFAULT = new Float2IntEval();
	
	@Override
	public Expression evalExpression(Expression expression) {
		PlaceHolderExpression group = new PlaceHolderExpression(expression);
		
		expression.accept(new ExpressionVisitor() {

			@Override
			public int visit(IDExpression id) {
				Type type = id.getType();
				String content = id.getContent();
				
				if (type == BasicType.FLOAT || type == BasicType.DOUBLE){
					//Các trường hợp khác ?
					if (content.matches("\\d+\\.0+[fd]?"))
						group.replace(id, new IDExpression(
								content.substring(0, content.indexOf("."))));
				}
				return PROCESS_CONTINUE;
			}
			
		});
		
		return group.getElement();
	}
	
}
