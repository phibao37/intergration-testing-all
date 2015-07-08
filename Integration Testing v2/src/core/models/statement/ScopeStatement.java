package core.models.statement;

import core.models.Statement;

/**
 * Dạng câu lệnh đặc biệt dùng để đánh dấu phạm vi khả dụng của các khai báo bên trong nó.
 * <br/>Trong ngôn ngữ lập trình, đó chính là cặp dấu ngoặc nhọn {}
 * @author ducvu
 *
 */
public class ScopeStatement extends Statement {
	
	/**
	 * Tạo câu lệnh mở khối mới
	 * @param branch 1 câu lệnh ở nhánh tiếp theo (tùy chọn)
	 * @return câu lệnh mở khối được tạo
	 */
	public static ScopeStatement newOpenScope(Statement... branch){
		ScopeStatement open = new ScopeStatement(SCOPE_OPEN);
		
		if (branch.length == 1)
			open.setBranch(branch[0]);
		return open;
	}
	
	/**
	 * Tạo câu lệnh đóng khối mới
	 * @param branch 1 câu lệnh ở nhánh tiếp theo (tùy chọn)
	 * @return câu lệnh đóng khối được tạo
	 */
	public static ScopeStatement newCloseScope(Statement... branch){
		ScopeStatement close = new ScopeStatement(SCOPE_CLOSE);
		
		if (branch.length == 1)
			close.setBranch(branch[0]);
		return close;
	}
	
	/**
	 * Mô tả rằng đang bắt đầu mở một khối câu lệnh
	 */
	public static final String SCOPE_OPEN = "{";
	
	/**
	 * Mô tả rằng đang đóng lại một khối câu lệnh
	 */
	public static final String SCOPE_CLOSE = "}";
	
	/**
	 * Khởi tạo câu lệnh với nội dung của nó
	 * @param content biểu diễn của phạm vi, bao gồm:
	 * <ul>
	 * 	<li>{@link #SCOPE_OPEN}</li>
	 * 	<li>{@link #SCOPE_CLOSE}</li>
	 * </ul>
	 */
	public ScopeStatement(String content) {
		super(content);
	}
	
	/**
	 * Khởi tạo câu lệnh với nội dung và câu lệnh tiếp theo của nó
	 * @param content biểu diễn của phạm vi, bao gồm:
	 * <ul>
	 * 	<li>{@link #SCOPE_OPEN}</li>
	 * 	<li>{@link #SCOPE_CLOSE}</li>
	 * </ul>
	 * @param next câu lệnh tiếp theo trong đường thi hành (cả 2 nhánh như nhau)
	 * @see #setBranch(Statement, Statement)
	 */
	public ScopeStatement(String content, Statement next){
		super(content);
		setBranch(next, next);
	}
	
	/**
	 * Kiểm tra đây là câu lệnh mở khối, nếu không nó là câu lệnh đóng khối
	 */
	public boolean isOpenScope(){
		return SCOPE_OPEN.equals(mContent);
	}
}




