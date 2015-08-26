package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
import core.models.Type;
import core.models.type.ArrayType;

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
	 * Trả về kích thước của danh sách mảng
	 */
	public int length(){
		return g.length;
	}
	
	/**
	 * Đặt giá trị cho phần tử 
	 * @throws NullPointerException vị trí ngoài khoảng
	 */
	public void setElement(int index, Expression ex) throws NullPointerException{
		g[index] = ex;
		notifyContentChanged();
	}
	
	/**
	 * Trả về phần tử trong mảng
	 * @param index vị trí của phần tử
	 * @return phần tử tại vị trí đã cho
	 * @throws NullPointerException vị trí ngoài khoảng
	 */
	public Expression getElement(int index) throws NullPointerException{
		return g[index];
	}
	
	@Override
	protected String generateContent() {
		if (g.length == 0) return "{}";
		String content = "{" + g[0];
		
		for (int i = 1; i < g.length; i++)
			content += ", " + g[i];
		return content + "}";
	}


	@Override
	public Type getType() {
		int len = length();
		if (len > 0)
			return new ArrayType(getElement(0).getSource().getType(), len);
		return null;
	}

}
