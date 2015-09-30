package core.eval;

import core.models.Expression;

/**
 * Giao diện chung cho các đối tượng có thể tính toán (hoặc chỉ rút gọn)
 * giá trị của một biểu thức
 * @author ducvu
 *
 */
public interface Evaluateable {
	
	/**
	 * Tính toán biểu thức và trả về giá trị kết quả
	 * @param expression biểu thức cần tính toán
	 * @return giá trị đã được tính
	 */
	Expression evalExpression(Expression expression);
}
