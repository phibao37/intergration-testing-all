package core.expression;

import api.expression.IExpression;
import api.expression.IFunctionCallExpression;
import api.expression.INameExpression;
import api.models.IFunction;
import api.models.IType;

/**
 * Mô tả biểu thức gọi hàm, bao gồm tên của hàm cần gọi và một danh sách các
 * tham số sẽ được truyền khi hàm được gọi
 * @example
 * <ul>
 * 	<li>test()</li>
 * 	<li>min(1, 'a', 3.0)</li>
 * </ul>
 * @author ducvu
 *
 */
public class FunctionCallExpression extends ExpressionGroup implements IFunctionCallExpression{
	
	private IFunction mFunc;
	
	/**
	 * Tạo một biểu thức gọi hàm với tên và danh sách tham số
	 * @param name tên của hàm được gọi
	 * @param argument danh sách các biểu thức (đối số của hàm)
	 */
	public FunctionCallExpression(IExpression name, IExpression... argument) {
		g = new Expression[argument.length + 1];
		g[0] = name;
		System.arraycopy(argument, 0, g, 1, argument.length);
		
		if (name instanceof INameExpression)
			((INameExpression) name).setRole(INameExpression.ROLE_FUNCTION);
	}
	
	@Override
	protected String generateContent() {
		if (g.length == 1){
			return getName() + "()";
		}
		
		String args = g[1].getContent();
		for (int i = 2; i < g.length; i++)
			args += "," + g[i].getContent();
		return String.format("%s(%s)", getName(), args);
	}

	@Override
	public IExpression[] getArguments(){
		IExpression[] indexs = new IExpression[g.length - 1];
		System.arraycopy(g, 1, indexs, 0, indexs.length);
		return indexs;
	}
	
	@Override
	public void setFunction(IFunction func){
		mFunc = func;
	}
	
	@Override
	public IFunction getFunction(){
		return mFunc;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public IType getType() {
		if (mFunc != null)
			return mFunc.getReturnType();
		return null;
	}


	@Override
	public IExpression getNameExpression() {
		return g[0];
	}

	@Override
	public void setType(IType type) {
		throw new RuntimeException("Use setFunction instead");
	}
	
}
