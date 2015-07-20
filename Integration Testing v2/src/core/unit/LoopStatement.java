package core.unit;

import java.util.ArrayList;
import java.util.Iterator;

import core.S;
import core.models.Statement;

/**
 * Một câu lệnh lặp bao gồm 1 chuỗi câu lệnh có điểm đầu và điểm cuối đều là một. 
 * Đây là điểm điều khiển quyết định đường đi tiếp theo là thoát khỏi vỏng lặp hoặc vẫn
 * duy trì vòng lặp
 */
public class LoopStatement extends Statement {
	
	private Statement mCondition;
	private LoopablePath mBody;
	private ArrayList<Statement> mOrigin;
	
	/**
	 * Tạo một "câu lệnh" lặp từ một dãy các câu lệnh trong vòng lặp
	 * @param loop danh sách các câu lệnh trong vòng lặp, có vị trí đầu và cuối như nhau
	 * <br/>
	 * Thí dụ: i < n -> i++ -> i < n
	 */
	@SuppressWarnings("unchecked")
	public LoopStatement(ArrayList<Statement> loop) {
		//Đặt nội dung sau :)
		super(null);
		mOrigin = (ArrayList<Statement>) loop.clone();
		
		mCondition = loop.get(0);
		loop.remove(loop.size()-1);
		loop.remove(0);
		mBody = new LoopablePath(loop);
		
		setContent(String.format("%s ? [%s]...", mCondition, mBody));
	}
	
	/**
	 * Lặp phần một số lần cho trước rồi trả về danh sách gồm một đường đi lặp này.<br/>
	 * Nếu số lần lặp bằng -1, trả về danh sách các đường đi ứng với số lần lặp 
	 * bằng 0, 1, 2, ... {@link S#MAX_LOOP_TEST} lần
	 * @param iter con trỏ lưu số lần lặp của vòng lặp đang xét, 
	 * và sau đó là các số lần lặp của các vòng lặp con
	 * @return danh sách các đường đi lặp xây dựng được
	 */
	public ArrayList<ArrayList<Statement>> joinLoopStatement(Iterator<Integer> iter){
		ArrayList<ArrayList<Statement>> lists = new ArrayList<>();
		ArrayList<ArrayList<Statement>> body = new ArrayList<>();
		body.add(new ArrayList<Statement>());
		int loop = iter.next();
		
		//Ghép các câu lệnh trong phần thân lặp lại 
		mBody.joinLoopStatement(body, iter);
		
		//Duyệt qua từng danh sách phần thân (vì có thể phần thân được kiểm thử)
		for (ArrayList<Statement> inBody: body)
			
			//Chọn kiểm thử vòng lặp này
			if (loop == -1){
				for (int i = 0; i < S.MAX_LOOP_TEST; i++)
					lists.add(join(inBody, i));
			} 
			
			//Vòng lặp này chỉ được lặp một số lần chỉ định
			else {
				lists.add(join(inBody, loop));
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
	private ArrayList<Statement> join(ArrayList<Statement> body, int loop){
		ArrayList<Statement> join = new ArrayList<Statement>();
		
		//Luôn thêm câu lệnh điểu khiển trước
		join.add(mCondition);
		
		//Thêm các đoạn lặp vào đằng sau
		for (int i = 0; i < loop; i++){
			join.addAll(body);
			join.add(mCondition);
		}
		
		return join;
	}
	
	/**
	 * Trả về nút điều khiển của vòng lặp
	 */
	public Statement getCondition(){
		return mCondition;
	}
	
	/**
	 * Trả về danh sách câu lệnh trong phần thân lặp
	 */
	public LoopablePath getBody(){
		return mBody;
	}
	
	/***
	 * Trả về danh sách các câu lệnh gốc
	 */
	public ArrayList<Statement> getOriginList(){
		return mOrigin;
	}
}
