package api.models;

import java.io.File;
import java.util.List;

public interface IFunction extends IElement {
	
	/**
	 * Trả về tên của hàm
	 */
	public String getName();
	
	/**
	 * Trả về danh sách các tham số của hàm 
	 */
	public IVariable[] getParameters();
	
	/**
	 * Lấy kiểu trả về của hàm
	 */
	public IType getReturnType();
	
	/**
	 * Thiết đặt đồ thị dòng điều khiển ứng với hàm
	 * @param cover mức độ phủ của đồ thị
	 * @param cfg đồ thị dòng điều khiển ứng với phần thân hàm
	 */
	public void setCFG(int cover, ICFG cfg);
	
	/**
	 * Trả về đồ thị dòng điều khiển của hàm ứng với một mức độ phủ xác định
	 */
	public ICFG getCFG(int cover);
	
	/**
	 * Thêm một hàm vào danh sách hàm được tham chiếu
	 * @param refer hàm được tham chiếu (được gọi) trong phần thân hàm
	 */
	public void addRefer(IFunction refer);
	
	/**
	 * Trả về danh sách các hàm được tham chiếu
	 */
	public List<IFunction> getRefers();
	
	public void setSourceFile(File file);
	
	public File getSourceFile();
}
