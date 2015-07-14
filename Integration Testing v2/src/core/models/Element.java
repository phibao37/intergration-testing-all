package core.models;

/**
 * Đại diện cho các phần tử có trong chương trình như biểu thức, câu lệnh, kiểu,...
 * @author ducvu
 *
 */
public abstract class Element implements Cloneable, Contentable {
	
	protected String mContent;
	
	/**
	 * Khởi tạo một phần tử rỗng.<br/>
	 * Sử dụng {@link #setContent(String)} để thiết đặt nội dung cho phần tử
	 */
	public Element() {}
	
	/**
	 * Khởi tạo một phần tử cùng với nội dung lưu trữ của nó
	 * @param content nội dung phần tử, là chuỗi hiển thị của phần tử trong mã nguồn
	 */
	public Element(String content){
		setContent(content);
	}
	
	/**
	 * Trả về nội dung hiển thị của phần tử
	 */
	@Override
	public String getContent(){
		return mContent;
	}
	
	/**
	 * Thiết đặt nội dung hiển thị của phần tử
	 * @param content nội dung phần tử, là chuỗi hiển thị của phần tử trong mã nguồn
	 */
	protected void setContent(String content){
		mContent = content;
	}
	
	/**
	 * So sánh 2 phần tử dựa trên nội dung hiển thị của nó
	 */
	public boolean equals(Object o){
		if (this == o)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		
		Element other = (Element) o;
		return other.getContent().equals(getContent());
	}
	
	/**
	 * Tạo ra một bản sao của phần tử
	 */
	public Element clone(){
		try{
			return (Element) super.clone();
		} catch (Exception e){
			return null;
		}
	}

	@Override
	public String toString() {
		return getContent();
	}

	@Override
	public int hashCode() {
		return getContent().hashCode();
	}
	
}
