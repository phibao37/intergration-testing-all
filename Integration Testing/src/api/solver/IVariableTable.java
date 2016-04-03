package api.solver;

import java.util.List;

import api.expression.IExpression;
import api.expression.IExpressionEval;
import api.models.IVariable;

public interface IVariableTable extends List<IVariable> {

	void increaseScope();
	void decreaseScope();
	
	void addVariable(IVariable var);
	void updateVariable(String name, IExpression value);
	void updateArrayElement(String name, IExpression[] indexes, IExpression value);
	
	/**
	 * Làm cho mọi biểu thức tham chiếu đều được gán kiểu 
	 */
	void injectTypeExpression(IExpression ex);
	
	/**
	 * Lấp các tham chiếu trong biểu thức bằng giá trị<br/>
	 * <pre>
	 * (1 + x) / a[2]  ===>  (1 + 3) / 2 
	 * </pre>
	 */
	IExpression fill(IExpression expression);
	IExpressionEval getExpressionEval();
	
	/**
	 * Lấp các tham chiếu trong biểu thức bằng giá trị, 
	 * sau đó tính toán và rút gọn giá trị<br/>
	 * <pre>
	 * (1 + x) / a[2]  ===>  (1 + 3) / 2  ===> 2
	 * </pre>
	 */
	default IExpression eval(IExpression expression){
		return fill(getExpressionEval().eval(expression));
	}
	
	IVariable find(String name);
	
	default int getScope(String name){
		return find(name).getScope();
	}
}
