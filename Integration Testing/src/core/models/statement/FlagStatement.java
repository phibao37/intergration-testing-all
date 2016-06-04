package core.models.statement;

import core.models.Statement;

/**
 * Dạng câu lệnh đặc biệt dùng để đánh dấu các vị trí trên đường đi.<br/>
 * Thường dùng để đánh dấu vị trí đầu và cuối của một đường thi hành
 * @author ducvu
 *
 */
public class FlagStatement extends Statement {

	/**
	 * Vị trí mở đầu
	 */
	public static final String BEGIN = "Begin";
	
	/**
	 * Vị trí kết thúc
	 */
	public static final String END = "End";
	
	/**
	 * Tạo một câu lệnh đánh dấu mới
	 * @param content nội dung đánh dấu
	 */
	public FlagStatement(String content) {
		super(content);
	}
	
	/**
	 * Tạo một câu lệnh đánh dấu vị trí mở đầu
	 */
	public static FlagStatement newBeginFlag(){
		return new FlagStatement(BEGIN);
	}
	
	/**
	 * Tạo một câu lệnh đánh dấu vị trí kết thúc
	 */
	public static FlagStatement newEndFlag(){
		return new FlagStatement(END);
	}

	@Override
	public boolean isNormal() {
		return false;
	}

	@Override
	public boolean shouldDisplay() {
		return true;
	}

	@Override
	public boolean shouldInGroup() {
		return false;
	}
	
	
}
