package api.loop;

import java.util.ArrayList;
import java.util.List;

import api.models.IStatement;
import api.models.ITestpath;
import core.Utils;

/**
 * Danh sách có thứ tự các câu lệnh (không nhất thiết là phải đi cả chương trình), trong
 * đó có thể chứa các "câu lệnh" lặp {@link ILoopStatement}
 */
public interface ILoopPath extends ITestpath {

	/**
	 * Trả về danh sách các câu lệnh lặp ở trong đường đi này
	 */
	List<ILoopStatement> getLoops();
	
	/**
	 * Ghép các câu lệnh trong đường đi này vào mỗi đường đi trong danh sách cung cấp.
	 * Nếu gặp phải câu lệnh lặp, câu lệnh lặp này sẽ được lặp một số lần nhất định
	 * dựa theo chỉ số truyền vào. <br/>
	 * Nếu chỉ số là -1, câu lệnh lặp này sẽ được chọn làm
	 * kiểm thử, một bộ các danh sách câu lệnh sẽ được tạo ra ứng với câu lệnh lặp được
	 * lặp 0, 1, 2, ... looper.getMaxLoop()} lần, sau đó các đường đi trong
	 * danh sách truyền vào sẽ được nhân tích Đề-các với mỗi danh sách các câu lệnh vừa
	 * được tạo
	 * @param list danh sách lưu các đường đi cần được xây dựng. Cần có ít nhất 1 đường
	 * thi hành rỗng trong đó
	 * @param looper con trỏ chỉ đến chỉ số số lần lặp của các câu lệnh lặp
	 */
	default void joinLoopStatement(List<List<IStatement>> list, ILooper looper){
		//Tạo 1 danh sách chứa 1 đường đi rỗng
		List<List<IStatement>> build = new ArrayList<>();
		build.add(new ArrayList<>());
		
		//Duyệt lần lượt các câu lệnh trong danh sách
		for (IStatement stm: this){
			
			//Với câu lệnh lặp, nhân các đường đi trong danh sách với mỗi danh sách
			//mà câu lệnh lặp xây dựng được
			if (stm instanceof ILoopStatement){
				Utils.addMultiply(
						build, 
						((ILoopStatement) stm).joinLoopStatement(looper));
			}
			
			//Với câu lệnh bình thường, thêm nó vào mỗi đường đi trong danh sách
			else {
				for (List<IStatement> b: build)
					b.add(stm);
			}
		}
		
		Utils.addMultiply(list, build);
	}
	
	/**
	 * Kiểm tra đường đi này đã chứa một đường đi khác hay chưa
	 */
	default boolean isCover(ILoopPath path){
		if (this.size() != path.size()) return false;
		
		for (int i = 0; i < size(); i++){
			IStatement a = get(i), b = path.get(i);
			
			if (a instanceof ILoopStatement){
				if (!((ILoopStatement) a).isCover(b))
					return false;
			} else {
				if (a != b)
					return false;
			}
		}
		
		return true;
	}
}
