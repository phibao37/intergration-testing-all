package core.models;

/**
 * Kiểu biểu thức nhóm trong ngôn ngữ mà nó chứa các biểu thức con ở bên trong.<br/>
 * Thí dụ: (x > 0) && (x < 10) là biểu thức nhóm bao gồm 2 biểu thức con là
 * (x > 0), (x < 10)
 * @author ducvu
 *
 */
public abstract class ExpressionGroup extends Expression {
	
	/**
	 * Mảng các biểu thức con
	 */
	protected Expression[] g;
	
	/**
	 * Khởi tạo biểu thức nhóm cùng với danh sách các biểu thức con của nó
	 * @param elements danh sách biểu thức con
	 */
	public ExpressionGroup(Expression... elements) {
		g = elements;
	}
	
	/**
	 * Đặt nội dung hiển thị của biểu thức nhóm.<br/>
	 * Để truy cập mảng các biểu thức con, sử dụng đối tượng {@link #g}<br/>
	 */
	protected abstract String generateContent();
	
	/**
	 * Thông báo rằng nội dung hiển thị đã bị thay đổi do có sự chỉnh sửa các phần tử
	 */
	protected final void notifyContentChanged(){
		mContent = null;
	}
	
	/**
	 * Thay thế một biểu thức con bằng một biểu thức khác tại cùng vị trí
	 * @param find biểu thức cần thay thế. Một biểu thức được gọi là khớp để thay thế
	 * nếu 2 biểu thức này đều là một (phép so sánh ==) hoặc có cùng chung một nguồn 
	 * (xem {@link #equalsSource(Expression)})
	 * @param replace biểu thức sẽ được thay thế tại vị trí tương ứng
	 * @return 
	 * <i>true</i>: thay thế thành công<br/>
	 * <i>false</i>: biểu thức cần thay thế không được tìm thấy
	 * @throws NullPointerException biểu thức tìm kiểm bằng null
	 */
	public boolean replace(Expression find, Expression replace) {
		boolean replaced = false;
		
		for (int i = 0; i < g.length; i++){
			Expression item = g[i];
			
			if (find.equalsSource(item)){
				g[i] = replace;
				replaced = true;
			}
			else if (item instanceof ExpressionGroup){
				replaced = ((ExpressionGroup) item).replace(find, replace);
			}
			
			if (replaced)
				break;
		}
		
		if (replaced)
			setContent(generateContent());
		
		return replaced;
	}

	/**
	 * Tạo ra một bản sao của biểu thức. Các thành phần biểu thức con cũng tự nó được
	 * sao chép (deep-clone)
	 */
	public ExpressionGroup clone(){
		ExpressionGroup clone = (ExpressionGroup) super.clone();
		
		clone.g = new Expression[g.length];
		for (int i = 0; i < g.length; i++){
			if (g[i] != null)
			clone.g[i] = g[i].clone();
		}
		return clone;
	}
}







