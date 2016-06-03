package api.loop;

import java.util.ArrayList;
import java.util.List;

import api.models.IStatement;

/**
 * Một câu lệnh lặp bao gồm 1 chuỗi câu lệnh có điểm đầu và điểm cuối đều là một. 
 * Đây là điểm điều khiển quyết định đường đi tiếp theo là thoát khỏi vỏng lặp hoặc vẫn
 * duy trì vòng lặp
 */
public interface ILoopStatement extends IStatement {

	/**
	 * Trả về nút điều khiển của vòng lặp
	 */
	IStatement getCondition();
	
	/**
	 * Trả về danh sách câu lệnh trong phần thân lặp
	 */
	ILoopPath getBody();
	
	/**
	 * Lặp phần một số lần cho trước rồi trả về danh sách gồm một đường đi lặp này.<br/>
	 * Nếu số lần lặp bằng -1, trả về danh sách các đường đi ứng với số lần lặp 
	 * bằng 0, 1, 2, ... looper.getMaxLoop() lần
	 * @param looper con trỏ lưu số lần lặp của vòng lặp đang xét, 
	 * và sau đó là các số lần lặp của các vòng lặp con
	 * @return danh sách các đường đi lặp xây dựng được
	 */
	default List<List<IStatement>> joinLoopStatement(ILooper looper){
		List<List<IStatement>> lists = new ArrayList<>(), body = new ArrayList<>();
		body.add(new ArrayList<>());
		int loop = looper.iter().next();
		
		//Ghép các câu lệnh trong phần thân lặp lại 
		getBody().joinLoopStatement(body, looper);
		
		//Duyệt qua từng danh sách phần thân (vì có thể phần thân được kiểm thử)
		for (List<IStatement> inBody: body)
			
			//Chọn kiểm thử vòng lặp này
			if (loop == ILooper.TEST_THIS){
				for (int i = 0; i <= looper.getMaxLoop(); i++)
					lists.add(_join(inBody, i));
			} 
			
			//Vòng lặp này chỉ được lặp một số lần chỉ định
			else {
				lists.add(_join(inBody, loop));
			}
		
		return lists;
	}
	
	/**
	 * Lặp phần thân 1 số lần nhất định
	 * @param body danh sách các câu lệnh trong phần thân
	 * @param loop số lượng lần lặp
	 * @return danh sách các câu lệnh trong phần thân và câu lệnh điều kiện xen kẽ nhau,
	 * được lặp một số chỉ định lần
	 */
	default List<IStatement> _join(List<IStatement> body, int loop){
		List<IStatement> join = new ArrayList<>();
		
		//Luôn thêm câu lệnh điểu khiển trước
		join.add(getCondition());
		
		//Thêm các đoạn lặp vào đằng sau
		for (int i = 0; i < loop; i++){
			join.addAll(body);
			join.add(getCondition());
		}
		
		return join;
	}
	
	/**
	 * Kiểm tra câu lệnh lặp này có chứa câu lệnh khác (có thể là câu lệnh lặp hoặc
	 * câu lệnh thường)
	 */
	default boolean isCover(IStatement stm){
		if (stm instanceof ILoopStatement){
			ILoopStatement loop = (ILoopStatement) stm;
			return getCondition() == loop.getCondition()
					&& getBody().isCover(loop.getBody());
		}
		else
			return getCondition() == stm;
	}
}
