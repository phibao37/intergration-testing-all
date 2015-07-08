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
	 * Thay thế một biểu thức con bằng một biểu thức khác tại cùng vị trí
	 * @param find biểu thức cần thay thế, dạng {@link String} hoặc {@link Expression}
	 * @param replace biểu thức sẽ được thay thế tại vị trí tương ứng
	 * @return 
	 * <i>true</i>: thay thế thành công<br/>
	 * <i>false</i>: biểu thức cần thay thế không được tìm thấy
	 */
	public boolean replace(Object find, Expression replace) {
		boolean replaced = false;
		
		for (int i = 0; i < g.length; i++){
			Expression item = g[i];
			
			if (item instanceof ExpressionGroup){
				replaced = ((ExpressionGroup) item).replace(find, replace);
			}
			else if (item.equals(find)){
				g[i] = replace;
				replaced = true;
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
		
		for (int i = 0; i < g.length; i++)
			clone.g[i] = g[i].clone();
		return clone;
	}
}







