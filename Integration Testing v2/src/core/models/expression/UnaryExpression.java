package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
/**
 * Biểu thức một bên, bao gồm một phép toán và một biểu thức con ở bên trái hoặc
 * bên phải phép toán
 * @example
 * <ul>
 * 	<li>-(x+y): phép toán lấy giá trị đối</li>
 * 	<li>!(x<y): phép toán logic lấy giá trị phủ định NOT</li>
 * 	<li>++i: phép toán tăng giá trị lên 1 và trả về kết quả</li>
 * 	<li>i--: phép toán trả về kết quả và giảm giá trị đi 1</li>
 * </ul>
 * @author ducvu
 *
 */
public class UnaryExpression extends ExpressionGroup {
	
	private boolean mLeft;
	private String mOperator;
	
	/**
	 * Tạo biểu thức một bên với biểu thức con và có phép toán ở bên trái
	 * @param operator chuỗi hiển thị của phép toán
	 * @param subElement biểu thức con
	 */
	public UnaryExpression(String operator, Expression subElement){
		this(subElement, operator, true);
	}
	
	/**
	 * Tạo biểu thức một bên với biểu thức con và có phép toán ở bên phải
	 * @param operator chuỗi hiển thị của phép toán
	 * @param subElement biểu thức con
	 */
	public UnaryExpression(Expression subElement, String operator){
		this(subElement, operator, false);
	}
	
	private UnaryExpression(Expression subElement, String operator, boolean left){
		super(subElement);
		mOperator = operator;
		mLeft = left;
	}

	@Override
	protected String generateContent() {
		if (isLeftOperator())
			return String.format("(%s%s)", getOperator(), getSubElement());
		else
			return String.format("(%s%s)", getSubElement(), getOperator());
	}
	
	/**
	 * Kiểm tra phép toán nằm ở bên trái của biểu thức con
	 */
	public boolean isLeftOperator(){
		return mLeft;
	}
	
	/**
	 * Trả về phép toán của biểu thức
	 */
	public String getOperator(){
		return mOperator;
	}
	
	/**
	 * Trả về biểu thức con
	 */
	public Expression getSubElement(){
		return g[0];
	}
}













