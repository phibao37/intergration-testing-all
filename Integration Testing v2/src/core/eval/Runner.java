package core.eval;

import core.models.Function;
import core.models.Variable;

/**
 * Đối tượng cho phép thực thi một hàm số chỉ định, tính toán và trả về kết quả tương ứng
 *<br/>
 *Việc chạy hàm bao gồm truyền một danh sách các biến số tương ứng với tham số hàm và
 * thực thi các lệnh trong hàm số. Việc chạy hàm có thể làm thay đối giá trị biến số 
 * đầu vào
 */
public interface Runner {
	
	/**
	 * Thực thi một hàm số với một bộ đầu vào chỉ định
	 * @param function tham chiếu tới hàm số cần thực thi
	 * @param args danh sách các biến số dầu vào, cần đúng tên và thứ tự như danh sách
	 * các tham số của hàm ({@link Function#getParameters()}), hơn nữa giá trị của biến
	 * phải được xác định
	 * @return giá trị hàm trả về, hoặc null nếu không có.<br/>
	 * Một số hàm còn làm thay đổi các giá trị đấu vào (thí dụ: thay đổi giá trị phần tử
	 * mảng), các thay đổi này phải được cập nhật vào danh sách biến số đầu vào
	 */
	public Object run(Function function, Variable... args);
	
}
