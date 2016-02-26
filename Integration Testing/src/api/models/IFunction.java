package api.models;

import java.io.File;
import java.util.List;

import api.Value;
import core.expression.ExpressionVisitor;
import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;

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
	
	public Object getBody();
	
	/**
	 * Trả về danh sách các hàm được tham chiếu
	 */
	public List<IFunction> getRefers();
	
	public void setSourceFile(File file);
	
	public File getSourceFile();
	
	/**
	 * Duyệt lần lượt qua các câu lệnh (và các biểu thức gốc ở bên trong câu lệnh) 
	 * ở trong phần thân hàm
	 * @param visitor bộ duyệt biểu thức. 
	 * Sử dụng {@link ExpressionVisitor#visit(Statement)} để "bắt" được khi các
	 * câu lệnh được duyệt vào
	 * @throws NullPointerException chưa có đồ thị CFG
	 */
	public default void accept(ExpressionVisitor visitor) 
			throws NullPointerException{
		int process;
		
		for (IStatement stm: getCFG(Value.COVER_STATEMENT).getStatements()){
			process = visitor.visit(stm);
			
			if (process == ExpressionVisitor.PROCESS_ABORT)
				break;
			else if (process == ExpressionVisitor.PROCESS_CONTINUE
					&& !(stm instanceof ScopeStatement)
					&& !(stm instanceof FlagStatement)){
				
				process = stm.getRoot().accept(visitor);
				if (process == ExpressionVisitor.PROCESS_ABORT)
					break;
			}
		}
	}
}
