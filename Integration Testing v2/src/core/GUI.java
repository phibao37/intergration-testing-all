package core;

import java.io.File;
import core.models.Function;

/**
 * Lớp hiển thị giao diện chính của ứng dụng
 *
 */
public class GUI {

	/**
	 * Đối tượng GUI đang mở, dùng để tương tác với các tác vụ đồ họa 
	 */
	public static GUI instance = new GUI();
	
	/**
	 * Tạo một đối tượng GUI mới
	 */
	protected GUI(){
		GUI.instance = this;
	}
	
	/**
	 * Mở nội dung một tập tin để xem. Lớp con cần overide để sử dụng
	 * @param file tập tin muốn mở
	 */
	public void openFileView(File file){}
	
	/**
	 * Bắt đầu quá trình kiểm thử một đơn vị
	 * @param func hàm cần kiểm thử
	 */
	public void beginTestFunction(Function func){}
	
	
	/**
	 * Đặt thông báo trạng thái cho ứng dụng
	 */
	public void setStatus(String status, Object... args) {}
	
	/**
	 * Đặt thông báo trạng thái cho ứng dụng, hiển thị trong một khoảng thời gian
	 */
	public void setStatus(int second, String status, Object... args) {}
}
