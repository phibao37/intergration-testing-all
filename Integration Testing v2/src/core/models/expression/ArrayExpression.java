package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;

/**
 * Mô tả một biểu thức mảng, thường ở dưới dạng: {a1, a2,..., an}<br/>
 * Biểu thức này chính là giá trị của các biến kiểu mảng
 * @author ducvu
 *
 */
public class ArrayExpression extends ExpressionGroup {
	
	/**
	 * Tạo một biểu thức mảng mới
	 * @param elements danh sách các phần tử trong mảng
	 */
	public ArrayExpression(Expression... elements){
		super(elements);
	}
	
	/**
	 * Trả về danh sách các phần tử trong mảng
	 */
	public Expression[] getElements(){
		return g;
	}
	
	/**
	 * Trả về phần tử trong mảng
	 * @param index vị trí của phần tử
	 * @return phần tử tại vị trí đã cho
	 */
	public Expression getElement(int index){
		return g[index];
	}
	
	@Override
	protected String generateContent() {
		String content = "{" + g[0];
		
		for (int i = 1; i < g.length; i++)
			content += ", " + g[i];
		return content + "}";
	}

}
