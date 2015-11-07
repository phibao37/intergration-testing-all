package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
import core.models.Function;
import core.models.Type;

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
public class FunctionCallExpression extends ExpressionGroup implements NamedAttribute{
	
	private Function mFunc;
	
	/**
	 * Tạo một biểu thức gọi hàm với tên và danh sách tham số
	 * @param name tên của hàm được gọi
	 * @param argument danh sách các biểu thức (đối số của hàm)
	 */
	public FunctionCallExpression(Expression name, Expression... argument) {
		g = new Expression[argument.length + 1];
		g[0] = name;
		System.arraycopy(argument, 0, g, 1, argument.length);
		
		if (name instanceof NameExpression)
			((NameExpression) name).setRole(NameExpression.ROLE_FUNCTION);
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
	
	/**
	 * Trả về danh sách các tham số gọi hàm<br/>
	 * Mảng được truy cập trực tiếp, do vậy, không nên thay đổi các phần tử trong mảng này
	 */
	public Expression[] getArguments(){
		Expression[] indexs = new Expression[g.length - 1];
		System.arraycopy(g, 1, indexs, 0, indexs.length);
		return indexs;
	}
	
	/**
	 * Thiết đặt hàm số được cho là đúng với lời gọi hàm này
	 */
	public void setFunction(Function func){
		mFunc = func;
	}
	
	/**
	 * Trả về hàm số tương ứng với lời gọi hàm này, hoặc null nếu không có
	 */
	public Function getFunction(){
		return mFunc;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	/**
	 * Không hỗ trợ, lấy từ hàm tương ứng
	 */
	@Override
	public void setType(Type type) {}

	@Override
	public Type getType() {
		if (mFunc != null)
			return mFunc.getReturnType();
		return null;
	}

	@Override
	public Expression getNameExpression() {
		return g[0];
	}
	
	
}
