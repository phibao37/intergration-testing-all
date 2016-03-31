package api.parser;

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
	IExpression fill(IExpression expression);
	IExpressionEval getExpressionEval();
	
	default IExpression eval(IExpression expression){
		return fill(getExpressionEval().eval(expression));
	}
	
	IVariable find(String name);
}
