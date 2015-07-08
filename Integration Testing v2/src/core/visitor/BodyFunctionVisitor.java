package core.visitor;

import core.models.Statement;
import core.models.statement.ScopeStatement;

/**
 * Thiết kế chung cho các đối tượng dùng để phân tích nội dung của một thân hàm trong 
 * chương trình và lấy ra cấu trúc các câu lệnh ({@link Statement}) có liên kết với nhau
 * trong phần thân của nó
 * @author ducvu
 *
 */
public interface BodyFunctionVisitor {
	
	/**
	 * Phân tích nội dung của một hàm và tạo ra cấu trúc các câu lệnh liên kết với nhau
	 * @param body đối tượng liên kết với thân của hàm số, thường là một nút trong
	 * cây AST ứng với thân hàm, hoặc có thể chỉ là chuỗi mã nguồn ({@link String})
	 * của phần thân
	 * @param subCondition có phân tích và "bóc" các điều kiện phức hợp thành các
	 * điều kiện con hay không
	 * @return danh sách các câu lệnh đã được liên kết, câu lệnh đầu danh sách luôn
	 * là câu lệnh được thực hiện trước tiên khi hàm số được chạy trong chương trình.<br/>
	 * Thông thường, nó sẽ phải là "câu lệnh" mở khối thân hàm { - {@link ScopeStatement}
	 */
	public Statement[] parseBody(Object body, boolean subCondition);
}
