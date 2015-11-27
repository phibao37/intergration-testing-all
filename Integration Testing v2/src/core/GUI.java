package core;

import graph.CFGView;

import java.io.File;
import java.util.ArrayList;

import core.models.Function;
import javafx.util.Pair;

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
	
	public void functionPairClicked(Function source, Function target, boolean dbClick){}
	
	/**
	 * Yêu cầu tạo một bộ stub mới
	 * @param strMap danh sách các cặp hàm số-chuỗi biểu thức stub
	 */
	public void requestNewStubSuite(ArrayList<Pair<Function, String>> strMap)
			throws Exception {}
	
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
	
	/**
	 * Đặt thông báo trạng thái cho ứng dụng, hiển thị trong một khoảng thời gian
	 */
	public void setStatus(int second, String status, Object... args) {}
}
