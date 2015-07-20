package core.unit;

import java.util.ArrayList;
import java.util.Iterator;

import core.S;
import core.Utils;
import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;

/**
 * Danh sách có thứ tự các câu lệnh (không nhất thiết là phải đi cả chương trình), trong
 * đó có thể chứa các "câu lệnh" lặp {@link LoopStatement}
 */
public class LoopablePath extends ArrayList<Statement> {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<LoopStatement> mLoopList;
	
	/**
	 * Tạo danh sách từ một danh sách các câu lệnh thông thường. Nếu phát hiện ra 
	 * vòng lặp, nhóm các câu lệnh trong vòng lặp sẽ được gộp lại thành 1 câu lệnh lặp 
	 * @param stmList danh sách theo thứ tự thực thi các câu lệnh đơn
	 */
	public LoopablePath(ArrayList<Statement> stmList){
		int i = 0, j = 0;
		Statement stm;
		mLoopList = new ArrayList<LoopStatement>();
		
		while (i < stmList.size()){
			stm = stmList.get(i);
			boolean found = false;
			
			//Duyệt lần lượt các câu lệnh đến khi gặp 1 câu lệnh điều kiện, ta
			//kiểm tra xem đây có là nút lặp hay không
			if (stm.isCondition()){
				for (j = i + 1; j < stmList.size(); j++)
					if (stmList.get(j) == stm){
						found = true;
						break;
					}
			}
			
			//Đây là một nút lặp, nhóm phần thân lặp vào "câu lệnh" lặp
			if (found){
				ArrayList<Statement> loop = new ArrayList<Statement>();
				
				for (; i <= j; i++)
					loop.add(stmList.get(i));
				
				LoopStatement l = new LoopStatement(loop);
				this.add(l);
				mLoopList.add(l);
			} else {
				this.add(stm);
				i++;
			}
		}
	}
	
	/**
	 * Trả về danh sách các câu lệnh lặp ở trong đường đi này
	 */
	public ArrayList<LoopStatement> getLoops(){
		return mLoopList;
	}
	
	/**
	 * Ghép các câu lệnh trong đường đi này vào mỗi đường đi trong danh sách cung cấp.
	 * Nếu gặp phải câu lệnh lặp, câu lệnh lặp này sẽ được lặp một số lần nhất định
	 * dựa theo chỉ số truyền vào. <br/>
	 * Nếu chỉ số là -1, câu lệnh lặp này sẽ được chọn làm
	 * kiểm thử, một bộ các danh sách câu lệnh sẽ được tạo ra ứng với câu lệnh lặp được
	 * lặp 0, 1, 2, ... {@link S#MAX_LOOP_TEST} lần, sau đó các đường đi trong
	 * danh sách truyền vào sẽ được nhân tích Đề-các với mỗi danh sách các câu lệnh vừa
	 * được tạo
	 * @param list danh sách lưu các đường đi cần được xây dựng. Cần có ít nhất 1 đường
	 * thi hành rỗng trong đó
	 * @param iter con trỏ chỉ đến chỉ số số lần lặp của các câu lệnh lặp
	 */
	public void joinLoopStatement(ArrayList<ArrayList<Statement>> list, 
			Iterator<Integer> iter){
		
		//Tạo 1 danh sách chứa 1 đường đi rỗng
		ArrayList<ArrayList<Statement>> build = new ArrayList<>();
		build.add(new ArrayList<Statement>());
		
		//Duyệt lần lượt các câu lệnh trong danh sách
		for (Statement stm: this){
			
			//Với câu lệnh lặp, nhân các đường đi trong danh sách với mỗi danh sách
			//mà câu lệnh lặp xây dựng được
			if (stm instanceof LoopStatement){
				Utils.addMultiply(
						build, 
						((LoopStatement) stm).joinLoopStatement(iter));
			}
			
			//Với câu lệnh bình thường, thêm nó vào mỗi đường đi trong danh sách
			else {
				for (ArrayList<Statement> b: build)
					b.add(stm);
			}
		}
		
		Utils.addMultiply(list, build);
	}

	@Override
	public String toString() {
		String content = "";
		int i = 0;
		
		for (Statement stm: this){
			i++;
			if (stm instanceof FlagStatement || stm instanceof ScopeStatement)
				continue;
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition()){
				boolean isTrue = true;
				
				if (i == size()){
					content += " (?)";
				} else {
					Statement next = get(i);
					
					if (next instanceof LoopStatement)
						next = ((LoopStatement) next).getCondition();
					isTrue = stm.getTrue() == next;
					content += String.format(" (%s)", isTrue);
				}
				
			}
		}
		
		return content.isEmpty() ? content : content.substring(4);
	}
	
	
}
