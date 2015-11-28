package api;

import java.util.List;

import api.models.IFunction;

/**
 * Giao diện tiến trình chính của ứng dụng, quản lý danh sách các đối tượng đầu vào trong
 * một phiên làm việc cũng như các công việc xử lý chúng
 */
public interface IMain {

	/**
	 * Thêm một hàm số vào danh sách các hàm trong phiên làm việc
	 * @param function hàm số, thường vừa được phân tích từ mã nguồn
	 */
	public void addFunction(IFunction function);
	
	/**
	 * Trả về danh sách các hàm trong phiên làm việc
	 */
	public List<IFunction> getFunctions();
}
