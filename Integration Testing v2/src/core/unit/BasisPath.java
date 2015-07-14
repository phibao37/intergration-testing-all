package core.unit;

import java.util.ArrayList;

import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.solver.Solver.Result;

/**
 * Mô tả một đường thi hành cơ bản, đó là một dãy có thứ tự duy nhất các câu lệnh
 * sẽ được chạy bởi chương trình
 * @author ducvu
 *
 */
public class BasisPath extends ArrayList<Statement> {
	private static final long serialVersionUID = 8275921168369912688L;
	
	private ConstraintEquations mConstraint;
	private Result mResult;
	
	/**
	 * Tạo ra bản sao đường thi hành
	 */
	public BasisPath clone(){
		BasisPath clone = (BasisPath) super.clone();
		
		/*for (int i = 0; i < size(); i++)
			clone.set(i, (Statement) get(i).clone());*/
		
		return clone;
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
			if (stm instanceof FlagStatement || stm instanceof ScopeStatement)
				continue;
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i+1));
			i++;
		}
		
		return content.isEmpty() ? content : content.substring(4);
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
	
}
