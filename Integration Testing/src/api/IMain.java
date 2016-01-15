package api;

import java.io.File;
import java.util.List;

import api.models.IBasisPath;
import api.models.IFunction;
import api.models.IVariable;

/**
 * Giao diện tiến trình chính của ứng dụng, quản lý danh sách các đối tượng đầu vào trong
 * một phiên làm việc cũng như các công việc xử lý chúng
 */
public interface IMain {
	
	/**
	 * Trả về danh sách các hàm trong phiên làm việc
	 */
	public List<IFunction> getFunctions();
	
	public void addFunction(IFunction function);
	
	/**
	 * Trả về danh sách các biến toàn cục trong phiên làm việc
	 */
	public List<IVariable> getGlobalVars();
	
	public void addGlobalVar(IVariable global);
	
	public void parseUnit(File source);
	
	/**
	 * Quá trình kiểm thử một hàm đơn vị
	 * @param function hàm cần kiểm thử
	 * @return danh sách các đường thi hành đã được gán kết quả giải hệ
	 */
	public List<IBasisPath> testFunction(IFunction function);
}
