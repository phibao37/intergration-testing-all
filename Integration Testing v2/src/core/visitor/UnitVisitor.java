package core.visitor;

import java.io.File;
import java.util.ArrayList;

import core.models.Function;
import core.models.Variable;

/**
 * Duyệt qua toàn bộ nội dung của một tập tin mã nguồn, sau đó lọc ra danh sách các 
 * đối tượng toàn cục được khai báo như: danh sách hàm, danh sách biến toàn cục, ...
 * @author ducvu
 *
 */
public interface UnitVisitor {
	
	/**
	 * Bắt đầu quá trình phân tích nội dung của mã nguồn, lọc ra danh sách các đối tượng
	 * cần thiết
	 * @param source nội dung của một tập tin mã nguồn cần phân tích
	 * @param file đối tượng tập tin mã nguồn
	 * @param args các tham số bổ sung, chẳng hạn các marco, include-path (C/C++), ...
	 * @return trả về chính đối tượng duyệt này để tiếp tục lấy các giá trị khác
	 */
	public UnitVisitor parseSource(String source, File file, Object... args);
	
	/**
	 * Trả về danh sách các hàm số được khai báo trong mã nguồn
	 */
	public ArrayList<Function> getFunctionList();
	
	/**
	 * Trả về danh sách các biến toàn cục được khai báo trong mã nguồn
	 */
	public ArrayList<Variable> getGlobalVariableList();
}
