package api.models;

import java.util.List;

public interface IFunction {
	
	/**
	 * Trả về tên của hàm số
	 */
	public String getName();
	
	/**
	 * Thêm một hàm vào danh sách hàm được tham chiếu
	 * @param refer hàm được tham chiếu (được gọi) trong phần thân hàm
	 */
	public void addRefer(IFunction refer);
	
	/**
	 * Trả về danh sách các hàm được tham chiếu
	 */
	public List<IFunction> getRefers();
}
