package api.expression;

/**
 * Giao diện của một biểu thức nhóm, nó có chứa các biểu thức con ở bên trong
 */
public interface IExpressionGroup extends IExpression {
	
	/**
	 * Thay thế một biểu thức con bằng một biểu thức khác tại cùng vị trí
	 * @param find biểu thức cần thay thế. Một biểu thức được gọi là khớp để thay thế
	 * nếu 2 biểu thức này đều là một (phép so sánh ==) hoặc có cùng chung một nguồn 
	 * (xem {@link #equalsSource(IExpression)})
	 * @param replace biểu thức sẽ được thay thế tại vị trí tương ứng
	 * @return 
	 * <i>true</i>: thay thế thành công<br/>
	 * <i>false</i>: biểu thức cần thay thế không được tìm thấy
	 * @throws NullPointerException biểu thức tìm kiểm bằng null
	 */
	public boolean replaceChild(IExpression find, IExpression replace);
}
