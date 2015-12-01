package api.models;

import api.solver.ISolveResult;

/**
 * Giao diện một đường thì hành cơ bản, là một dãy có thứ tự các câu lệnh đơn sẽ được
 * chạy bởi chương trình khi được thực thi
 */
public interface IBasisPath {
	
	/**
	 * Trả về kêt quả giải hệ ràng buộc
	 */
	public ISolveResult getResult();
}
