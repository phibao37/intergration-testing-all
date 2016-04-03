package core.models;

import api.models.IElement;

/**
 * Đại diện cho các phần tử có trong chương trình như biểu thức, câu lệnh, kiểu,...
 * @author ducvu
 *
 */
public abstract class Element implements IElement{
	
	protected String mContent;
	
	/**
	 * Khởi tạo một phần tử rỗng.<br/>
	 * Sử dụng {@link #setContent(String)} để thiết đặt nội dung cho phần tử
	 */
	protected Element() {}
	
	/**
	 * Khởi tạo một phần tử cùng với nội dung lưu trữ của nó
	 * @param content nội dung phần tử, là chuỗi hiển thị của phần tử trong mã nguồn
	 */
	protected Element(String content){
		setContent(content);
	}
	
	/**
	 * Thiết đặt nội dung hiển thị của phần tử
	 * @param content nội dung phần tử, là chuỗi hiển thị của phần tử trong mã nguồn
	 */
	public void setContent(String content){
		mContent = content;
	}
	
	@Override
	public String getContent(){
		return mContent;
	}
	
	/* SELF IMPLEMENT */
	
	@Override
	public Element clone(){
		try{
			return (Element) super.clone();
		} catch (Exception e){
			return null;
		}
	}
	
	@Override
	public boolean equals(Object o){
		return equalsContent(o);
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
