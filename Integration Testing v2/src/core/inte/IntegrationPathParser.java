package core.inte;

import core.error.StatementNoRootException;
import core.models.Function;
import core.unit.BasisPath;
import core.unit.BasisPathParser;

/**
 * Xử lý các câu lệnh gọi hàm trong các đường đi để phục vụ cho việc tích hợp
 */
public class IntegrationPathParser extends BasisPathParser {
	
	private Function mCalling;
	
	public void parseBasisPath(BasisPath path, Function func, Function calling)
			throws StatementNoRootException {
		mCalling = calling;
		super.parseBasisPath(path, func);
	}

	/**
	 * Bộ phân tích mặc định
	 */
	public static final IntegrationPathParser DEFAULT = new IntegrationPathParser();
}
