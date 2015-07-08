package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;

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
	
	private String mName;
	
	/**
	 * Tạo một biểu thức gọi hàm với tên và danh sách tham số
	 * @param name tên của hàm được gọi
	 * @param argument danh sách các biểu thức (đối số của hàm)
	 */
	public FunctionCallExpression(String name, Expression... argument) {
		super(argument);
		mName = name;
	}
	
	@Override
	protected String generateContent() {
		if (g == null){
			return getName() + "()";
		}
		
		String args = g[0].getContent();
		for (int i = 1; i < g.length; i++)
			args += "," + g[i].getContent();
		return String.format("%s(%s)", getName(), args);
	}
	
	/**
	 * Trả về tên hàm của lời gọi hàm
	 */
	public String getName(){
		return mName;
	}
	
	/**
	 * Trả về danh sách các tham số gọi hàm<br/>
	 * Mảng được truy cập trực tiếp, do vậy, không nên thay đổi các phần tử trong mảng này
	 */
	public Expression[] getArguments(){
		return g;
	}
}
