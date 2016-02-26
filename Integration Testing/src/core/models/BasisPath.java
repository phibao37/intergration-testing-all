package core.models;

import java.util.ArrayList;

import api.models.IBasisPath;
import api.models.IStatement;
import core.expression.ExpressionVisitor;
import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;

/**
 * Mô tả một đường thi hành cơ bản, đó là một dãy có thứ tự duy nhất các câu lệnh
 * sẽ được chạy bởi chương trình
 * @author ducvu
 *
 */
public class BasisPath extends ArrayList<IStatement> implements IBasisPath {
	private static final long serialVersionUID = 8275921168369912688L;
	
	/**
	 * Tạo ra bản sao đường thi hành
	 */
	public BasisPath clone(){
		
		/*for (int i = 0; i < size(); i++)
			clone.set(i, (Statement) get(i).clone());*/
		
		return (BasisPath) super.clone();
	}
	
	public String toString(){
		String content = get(0).toString();
		
		for (int i = 1; i < size(); i++){
			IStatement stm = get(i);
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i+1));
		}
		
		return content;
	}
	
	/**
	 * Trả về chuỗi mô tả đường thi hành, bỏ qua các câu lệnh BEGIN, END, {, }, ...
	 */
	public String toStringSkipMarkdown(){
		String content = "";
		int i = 0;
		
		for (IStatement stm: this){
			i++;
			if (stm instanceof FlagStatement || stm instanceof ScopeStatement)
				continue;
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i));
		}
		
		return content.isEmpty() ? content : content.substring(4);
	}
	
	/**
	 * Duyệt lần lượt qua các câu lệnh (và các biểu thức gốc ở bên trong câu lệnh) 
	 * theo danh sách trong đường thi hành
	 * @param visitor bộ duyệt biểu thức. 
	 * Sử dụng {@link ExpressionVisitor#visit(Statement)} để "bắt" được khi các
	 * câu lệnh được duyệt vào
	 */
	public void accept(ExpressionVisitor visitor){
		int process;
		
		for (IStatement stm: this){
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


	@Override
	public Iterable<IStatement> iter() {
		return this;
	}
	
}
