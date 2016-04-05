package api.models;

import java.util.List;

import api.parser.ISymbolicExecutor;
import api.solver.ISolution;

/**
 * Giao diện một đường thì hành cơ bản, là một dãy có thứ tự các câu lệnh đơn sẽ được
 * chạy bởi chương trình khi được thực thi
 */
public interface ITestpath extends List<IStatement> {
	
	ITestpath clone();
	
	/**
	 * Sao chép đường thi hành từ câu lệnh đầu đến vị trí chỉ định
	 */
	ITestpath cloneAt(int index);
	
	void setSolution(ISolution result);
	
	ISolution getSolution();
	
	/**
	 * Tạo đối tượng phân tích hệ ràng buộc từ đường thi hành
	 */
	public ISymbolicExecutor getConstraintParser();
}
