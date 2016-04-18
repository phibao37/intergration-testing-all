package core.models;

import java.util.ArrayList;

import api.models.ITestpath;
import api.expression.IExpression;
import api.models.IStatement;
import api.solver.ISolution;
import core.solver.Solution;

/**
 * Mô tả một đường thi hành cơ bản, đó là một dãy có thứ tự duy nhất các câu lệnh
 * sẽ được chạy bởi chương trình
 * @author ducvu
 *
 */
public class Testpath extends ArrayList<IStatement> implements ITestpath {
	private static final long serialVersionUID = 8275921168369912688L;
	
	private ISolution result = Solution.DEFAULT;
	private IExpression returnEx;
	
	public String toString(){
		String content = "";
		
		for (int i = 0; i < size(); i++){
			IStatement stm = get(i);
			if (!stm.isNormal()) continue;
			
			content += " -> " + stm;
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i+1));
		}
		
		return content.substring(4);
	}
	
	public Testpath clone(){
		return (Testpath) super.clone();
	}

	@Override
	public ITestpath cloneAt(int index) {
		ITestpath clone = clone();
		
		for (int i = size() - 1; i > index; i--)
			clone.remove(i);
		return clone;
	}

	@Override
	public void setSolution(ISolution result) {
		this.result = result;
	}

	@Override
	public ISolution getSolution() {
		return result;
	}

	@Override
	public void setReturnExpression(IExpression returnEx) {
		this.returnEx = returnEx;
	}

	@Override
	public IExpression getReturnExpression() {
		return returnEx;
	}
}
