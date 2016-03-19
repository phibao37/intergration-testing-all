package api.parser;

import java.util.List;

import api.models.IBasisPath;
import api.models.IVariable;
import api.solver.IConstraint;

public interface ConstraintParser {
	
	/**
	 * Từ một đường thi hành, trích xuất ra tập các hệ ràng buộc:
	 * <ul>
	 * 	<li>Hệ đầu tiên ứng với trường hợp đường thi hành được thực thi từ
	 * 	câu lệnh đầu tời cuối (không xảy ra lỗi)</li>
	 * <li>Các hệ tiếp theo ứng với các điểm dừng trung gian mà có lỗi xảy ra
	 * 	(chia cho 0, con trỏ NULL, truy cập phẩn tử mảng âm, ...)</li>
	 * </ul>
	 */
	List<IConstraint> parseBasisPath(IBasisPath path, IVariable[] params, 
			int options);
	
	final int DEFAULT = 0,
			PARSE_ERROR_PATH = 1;
}
