package core.models;

/**
 * Mô tả một biểu thức có trong mã nguồn, có thẻ là:
 * <ul>
 * 	<li>12: biểu thức hằng số</li>
 * 	<li>x, y[0]: biểu thức biến số</li>
 * 	<li>x+y, x < y: biểu thức nhị phân</li>
 * 	<li>x++: biểu thức một bên</li>
 * 	<li>f(x, y): biểu thức gọi hàm</li>
 * </ul>
 * @author ducvu
 *
 */
public abstract class Expression extends Element {
	
	/**
	 * Khởi tạo một biểu thức rỗng<br/>
	 * Sử dụng {@link #setContent(String)} để thiết đặt nội dung cho biểu thức
	 */
	public Expression() {
		this(null);
	}
	
	/**
	 * Khởi tạo một biểu thức cùng với nội dung lưu trữ của nó
	 * @param content nội dung biểu thức, là chuỗi hiển thị của biểu thức trong mã nguồn
	 */
	public Expression(String content){
		super(content);
	}
	
	/**
	 * Trả về nội dung hiển thị của biểu thức
	 */
	public String getContent(){
		if (mContent == null)
			mContent = generateContent();
		return mContent;
	}
	
	/**
	 * Tạo ra nội dung hiển thị của biểu thức khi nội dung này chưa được thiết đặt
	 * @return nội dung hiển thị mới
	 */
	protected String generateContent(){
		return mContent;
	}
	
	/**
	 * Tạo ra một bản sao của biểu thức
	 */
	public Expression clone(){
		return (Expression) super.clone();
	}
	
	/**
	 * In cây quan hệ
	 * @param margin khoảng cách đầu dòng
	 */
	public void printTree(String margin){
		System.out.println(margin + this);
		if (this instanceof ExpressionGroup){
			Expression[] g = ((ExpressionGroup)this).g;
			for (Expression ep: g)
				ep.printTree(margin + "   ");
		}
	}
	
}
