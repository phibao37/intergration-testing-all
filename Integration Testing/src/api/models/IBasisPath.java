package api.models;

import java.util.List;

import api.solver.ISolveResult;

/**
 * Giao diện một đường thì hành cơ bản, là một dãy có thứ tự các câu lệnh đơn sẽ được
 * chạy bởi chương trình khi được thực thi
 */
public interface IBasisPath extends List<IStatement> {
	
	IBasisPath clone();
	
	/**
	 * Sao chép đường thi hành từ câu lệnh đầu đến vị trí chỉ định
	 */
	IBasisPath cloneAt(int index);
	
	void setSolveResult(ISolveResult result);
	
	ISolveResult getSolveResult();
}
