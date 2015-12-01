package api.models;

/**
 * Đại diện cho các phần tử có trong chương trình như biểu thức, câu lệnh, kiểu,...
 */
public interface IElement extends HasContent, Cloneable {
	
	/**
	 * Tạo ra bản sao mới của một phần tử
	 */
	public IElement cloneElement();
}
