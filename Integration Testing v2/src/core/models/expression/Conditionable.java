package core.models.expression;

/**
 * Mô tả các biểu thức có thể mang điều kiện, tức giá trị của nó là true/false.<br/>
 * Bao gồm các trường hợp sau:
 * <ul>
 * 	<li>Các phép toán logic: <code>true && false, true || false, !false</code></li>
 * 	<li>Các phép toán so sánh: <code>x == y, z < 5</code>, ...</li>
 * 	<li>Biểu thức hằng nhị phân: <code>true, false</code></li>
 * </ul>
 * <b>Chú ý:</b> biểu thức chứa tên ({@link NamedAttribute}) không được xét là mang 
 * điều kiện, do tùy thuộc vào ngữ cảnh biến số đang tham chiếu có mang giá trị điều
 * kiện hay không
 */
public interface Conditionable {
	
	/**
	 * Trả về <b>true</b> nếu biểu thức này là biểu thức điều kiện,
	 * <b>false</b> nếu ngược lại
	 */
	public boolean isConditionExpression();
	
}
