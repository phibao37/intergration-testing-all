package core.models;

import java.util.ArrayList;

import api.models.IBasisPath;
import api.models.IStatement;
import api.solver.ISolveResult;

/**
 * Mô tả một đường thi hành cơ bản, đó là một dãy có thứ tự duy nhất các câu lệnh
 * sẽ được chạy bởi chương trình
 * @author ducvu
 *
 */
public class BasisPath extends ArrayList<IStatement> implements IBasisPath {
	private static final long serialVersionUID = 8275921168369912688L;
	
	private ISolveResult result;
	
	public String toString(){
		String content = get(0).toString();
		
		for (int i = 1; i < size(); i++){
			IStatement stm = get(i);
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i+1));
		}
		
		return content;
	}
	
	public BasisPath clone(){
		return (BasisPath) super.clone();
	}
	
	/**
	 * Trả về chuỗi mô tả đường thi hành, bỏ qua các câu lệnh BEGIN, END, {, }, ...
	 */
	public String toStringSkipMarkdown(){
		String content = "";
		int i = 0;
		
		for (IStatement stm: this){
			i++;
			if (!stm.isNormal())
				continue;
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i));
		}
		
		return content.isEmpty() ? content : content.substring(4);
	}

	@Override
	public IBasisPath cloneAt(int index) {
		IBasisPath clone = clone();
		
		for (int i = size() - 1; i > index; i--)
			clone.remove(i);
		return clone;
	}

	@Override
	public void setSolveResult(ISolveResult result) {
		this.result = result;
	}

	@Override
	public ISolveResult getSolveResult() {
		return result;
	}
	
}
