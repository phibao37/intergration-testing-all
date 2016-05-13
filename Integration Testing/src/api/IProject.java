package api;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;

import api.graph.IProjectNode;
import api.models.IFunction;
import api.models.IFunctionTestResult;
import api.models.IType;
import api.models.IVariable;
import api.parser.IProjectParser;
import api.parser.ISymbolicExecutor;
import api.solver.ISolver;

/**
 * Các phương thức cơ bản của 1 project
 */
public interface IProject extends FileFilter {
	
	public File getRoot();
	
	public void loadProject();
	
	/**
	 * Kiểm thử một hàm nhất định
	 */
	public IFunctionTestResult testFunction(IFunction func) throws InterruptedException;
	
	public default void checkStop() throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
	}
	
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
	public IProjectParser getProjectParser();
	
	/**
	 * Tạo đối tượng phân tích hệ ràng buộc từ đường thi hành
	 */
	public ISymbolicExecutor getConstraintParser();
	
	/**
	 * Trả về bộ giải hệ
	 */
	public List<ISolver> getListSolver();
	
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
	
	public String getStatus();
	
	public IRunProcess<?> getProcess();
}
