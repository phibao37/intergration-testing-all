package core.unit;

import java.util.ArrayList;

import core.error.StatementNoRootException;
import core.models.Expression;
import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.solver.Solver.Result;
import core.visitor.ExpressionVisitor;
import javafx.util.Pair;

/**
 * Mô tả một đường thi hành cơ bản, đó là một dãy có thứ tự duy nhất các câu lệnh
 * sẽ được chạy bởi chương trình
 * @author ducvu
 *
 */
public class BasisPath extends ArrayList<Statement> {
	private static final long serialVersionUID = 8275921168369912688L;
	
	private ConstraintEquations mConstraint;
	private Result mResult = Result.DEFAULT;
	private ArrayList<Pair<Statement, ArrayList<Expression>>> mAnalyzic;
	
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
			Statement stm = get(i);
			
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
		
		for (Statement stm: this){
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
		
		for (Statement stm: this){
			process = visitor.visit(stm);
			
			if (process == ExpressionVisitor.PROCESS_ABORT)
				break;
			else if (process == ExpressionVisitor.PROCESS_CONTINUE
					&& !(stm instanceof ScopeStatement)
					&& !(stm instanceof FlagStatement)){
				
				try {
					process = stm.getRoot().accept(visitor);
					if (process == ExpressionVisitor.PROCESS_ABORT)
						break;
				} catch (StatementNoRootException e) {
					//Câu lệnh chưa có biểu thức gốc, chuyển sang câu lệnh khác
				}
				
			}
		}
	}

	/**
	 * Trả về hệ ràng buộc tương ứng với đường thi hành
	 */
	public ConstraintEquations getConstraint() {
		return mConstraint;
	}

	/**
	 * Thiết đặt hệ ràng buộc tương ứng với đường thi hành
	 */
	public void setConstraint(ConstraintEquations mConstraint) {
		this.mConstraint = mConstraint;
	}

	/**
	 * Trả về kết quả của việc giải hệ ràng buộc
	 */
	public Result getSolveResult() {
		return mResult;
	}

	/**
	 * Thiết đặt kết quả của việc giải hệ ràng buộc
	 */
	public void setSolveResult(Result mResult) {
		this.mResult = mResult;
	}
	
	/**
	 * Kiểm tra đường thi hành này là không khả thi, không thế thực hiện được
	 * @return true nếu đã được gán kết quả giải hệ và kết quả là vô nghiệm
	 */
//	public boolean isUnreachable(){
//		return mResult != null && mResult.getSolutionCode() == Result.ERROR;
//	}

	/**
	 * Trả về danh sách các cặp câu lệnh - tập ràng buộc ứng với đường thi hành
	 */
	public ArrayList<Pair<Statement, ArrayList<Expression>>> getAnalyzic() {
		return mAnalyzic;
	}

	/**
	 * Đặt danh sách các ràng buộc cho các câu lệnh trong đường thi hành
	 */
	public void setAnalyzic(ArrayList<Pair<Statement, ArrayList<Expression>>> al) {
		this.mAnalyzic = al;
	}
	
}
