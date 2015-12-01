package api.models;

/**
 * Mô tả các đối tượng có nội dung dạng chuỗi chứa trong nó
 */
public interface HasContent {
	
	/***
	 * Trả về nội dung ứng với phần tử
	 */
	public String getContent();
	
	/**
	 * So sánh 2 phần tử dựa trên nội dung hiển thị của nó
	 */
	public default boolean equalsContent(Object o){
		if (this == o)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		return getContent().equals(((HasContent)o).getContent());
	}

}
