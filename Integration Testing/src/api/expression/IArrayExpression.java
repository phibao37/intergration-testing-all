package api.expression;

public interface IArrayExpression extends IExpressionGroup {

	public int length();
	
	public void setElement(int index, IExpression value);
	
	public IExpression getElement(int index);
	
}
