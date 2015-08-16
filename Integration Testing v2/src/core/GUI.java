package core;

import java.io.File;

import core.graph.CFGView;
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
	 * Xem chi tiết một hàm số: số lượng testcase, số hàm con, ...
	 */
	public void openFunctionDetails(Function fn){}
	
	/**
	 * Thông báo cho GUI biết số lượng testcase đã bị thay đổi
	 */
	public void notifyFunctionTestcaseChanged(Function fn, int count){}
	
	/**
	 * Mở đồ thị CFG của một hàm trong chương trình để xem
	 * @param fn hàm trong chương trình
	 * @param subCondition đồ thị phủ các điều kiện con ?
	 * @return 
	 */
	public CFGView openFuntionView(Function fn, boolean subCondition){
		return null;
	}
	
	public void openFunctionTestcaseManager(Function fn) {}
	
	/**
	 * Bắt đầu quá trình kiểm thử một đơn vị
	 * @param func hàm cần kiểm thử
	 */
	public void beginTestFunction(Function func){}
	
	public void functionPairClicked(Function source, Function target){}
	
	/**
	 * Trả về độ rộng của một canvas hiển thị chính trong ứng dụng.<br/>
	 * Một số canvas khi khởi tạo cần sủ dụng thông tin này
	 */
	public int getDefaultCanvasWidth(){
		return 0;
	}
	
	/**
	 * Đặt thông báo trạng thái cho ứng dụng
	 */
	public void setStatus(String status, Object... args) {}
}
