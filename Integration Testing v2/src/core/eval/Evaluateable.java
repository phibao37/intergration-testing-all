package core.eval;

import core.models.Expression;

/**
 * Giao diện chung cho các đối tượng có thể tính toán kết quả của một biểu thức
 * @author ducvu
 *
 */
public interface Evaluateable {
	
	/**
	 * Tính toán biểu thức và trả về kết quả
	 * @param expression biểu thức cần tính toán
	 * @return giá trị đã được tính
	 */
	public Expression evalExpression(Expression expression);
}
