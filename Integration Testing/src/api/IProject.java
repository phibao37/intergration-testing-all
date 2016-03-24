package api;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;

import api.graph.IProjectNode;
import api.models.IFunction;
import api.models.ITestResult;
import api.models.IType;
import api.models.IVariable;
import api.parser.BodyParser;
import api.parser.ConstraintParser;
import api.parser.UnitParser;
import api.solver.ISolver;

/**
 * Các phương thức cơ bản của 1 project
 */
public interface IProject extends FileFilter {
	
	/**
	 * Nạp các cấu trúc và tạo các liên kết
	 */
	public void loadProject();
	
	/**
	 * Kiểm thử một hàm nhất định
	 */
	public ITestResult testFunction(IFunction func);
	
	/**
	 * Trả về danh sách các hàm trong project
	 */
	public List<IFunction> getFunctions();
	
	public void addFunction(IFunction function);
	
	/**
	 * Trả về danh sách các biến toàn cục trong phiên làm việc
	 */
	public List<IVariable> getGlobalVars();
	
	public void addGlobalVar(IVariable global);
	
	/**
	 * Trả về danh sách các kiểu co bản/cấu trúc được nạp vào project
	 */
	public List<IType> getLoadedType();
	
	public void addLoadedType(IType type);
	
	/**
	 * Tạo đối tượng phân tích mã nguồn
	 */
	public UnitParser getUnitParser();
	
	/**
	 * Tạo đối tượng phân tích thân hàm
	 */
	public BodyParser getBodyParser();
	
	/**
	 * Tạo đối tượng phân tích hệ ràng buộc từ đường thi hành
	 */
	public ConstraintParser getConstraintParser();
	
	/**
	 * Trả về bộ giải hệ
	 */
	public ISolver getSolver();
	
	public Map<File, IProjectNode> getMapProjectStruct();
	
	/**
	 * Tìm kiểu theo tên của nó
	 */
	public default IType findType(String type){
		for (IType item: getLoadedType())
			if (item.getContent().equals(type))
				return item;
		return null;
	}
}
