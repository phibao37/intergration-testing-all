package api.parser;

import java.util.List;

import api.IProject;
import api.models.IFunction;
import api.models.IStatement;
import core.models.Statement;
import core.models.statement.ScopeStatement;

/**
 * Thiết kế chung cho các đối tượng dùng để phân tích nội dung của một thân hàm trong 
 * chương trình và lấy ra cấu trúc các câu lệnh ({@link Statement}) có liên kết với nhau
 * trong phần thân của nó
 * @author ducvu
 *
 */
public interface IFunctionParser {
	
	/**
	 * Phân tích nội dung của một hàm và tạo ra cấu trúc các câu lệnh liên kết với nhau
	 * @param fn hàm có chứa phần thân cần phân tích, thường là một nút trong
	 * cây AST ứng với thân hàm, hoặc có thể chỉ là chuỗi mã nguồn ({@link String})
	 * của phần thân
	 * @param subCondition có phân tích và "bóc" các điều kiện phức hợp thành các
	 * điều kiện con hay không
	 * @param project project tiến trình chính
	 * @return danh sách các câu lệnh đã được liên kết, câu lệnh đầu danh sách luôn
	 * là câu lệnh được thực hiện trước tiên khi hàm số được chạy trong chương trình.<br/>
	 * Thông thường, nó sẽ phải là "câu lệnh" mở khối thân hàm { - {@link ScopeStatement}
	 */
	public IStatement[] parseFunction(IFunction fn, boolean subCondition, 
			IProject project);
	
	
	 /**
	  * Câu lệnh trung gian, không mang dữ liệu.<br/>
	  * Nếu dùng phương pháp nút trung gian để liên kết các câu lệnh với nhau, có thể
	  * sử dụng loại câu lệnh này
	  * @see IFunctionParser#linkStatement(Statement, List)
	 */
	public static class ForwardStatement extends Statement{
		
		/**
		 * Tạo một câu lệnh trung gian mới
		 */
		public ForwardStatement() {
			super(null);
		}
	}
	
	/**
	 * Bỏ qua các nút chuyển tiếp và nối các câu lệnh lại với nhau
	 * @param root câu lệnh đang cần duyệt để nối 2 nhánh của nó tới 2 câu lệnh
	 * không trung gian
	 * @param stmList danh sách rỗng dùng để lấy tập các câu lệnh sau khi toàn bộ việc
	 * ghép nối đã hoàn tất
	 */
	public default void linkStatement(IStatement root, List<IStatement> stmList){
		if (root == null || root.isVisited())
			return;
		root.setVisit(true);
		stmList.add(root);
		
		IStatement stmTrue = root.getTrue();
		while (stmTrue instanceof ForwardStatement)
				//|| stmTrue instanceof ScopeStatement)
			stmTrue = stmTrue.getTrue();
		root.setTrue(stmTrue);
		
		IStatement stmFalse = root.getFalse();
		while (stmFalse instanceof ForwardStatement)
				//|| stmFalse instanceof ScopeStatement)
			stmFalse = stmFalse.getTrue();
		root.setFalse(stmFalse);

		linkStatement(stmTrue, stmList);
		linkStatement(stmFalse, stmList);
	}
}
