package core.expression;

import api.expression.IExpression;
import api.expression.IUnaryExpression;
import api.models.IType;
import core.Utils;
import core.models.type.BasicType;
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
public class UnaryExpression extends ExpressionGroup implements IUnaryExpression {
	
	/** Phép toán lấy dấu dương (3 + (+3))*/
	public static final String PLUS = "+";
	
	/** Phép toán lấy dấu âm (3 + (-3))*/
	public static final String MINUS = "-";
	
	/** Phép toán phủ định*/
	public static final String LOGIC_NOT = "!";
	
	/** Phép toán tăng thêm 1*/
	public static final String INCREASE = "++";
	
	/** Phép toán giảm đi 1*/
	public static final String DECREASE = "--";
	
	private static final String[] ASSIGNS = {INCREASE, DECREASE};
	
	private boolean mLeft;
	private String mOperator;
	
	/**
	 * Tạo biểu thức một bên với biểu thức con và có phép toán ở bên trái
	 * @param operator chuỗi hiển thị của phép toán
	 * @param subElement biểu thức con
	 */
	public UnaryExpression(String operator, IExpression subElement){
		this(subElement, operator, true);
	}
	
	/**
	 * Tạo biểu thức một bên với biểu thức con và có phép toán ở bên phải
	 * @param operator chuỗi hiển thị của phép toán
	 * @param subElement biểu thức con
	 */
	public UnaryExpression(IExpression subElement, String operator){
		this(subElement, operator, false);
	}
	
	private UnaryExpression(IExpression subElement, String operator, boolean left){
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
	
	/* (non-Javadoc)
	 * @see core.expression.IUnaryExpression#isLeftOperator()
	 */
	@Override
	public boolean isLeftOperator(){
		return mLeft;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IUnaryExpression#isAssignOperator()
	 */
	@Override
	public boolean isAssignOperator(){
		return Utils.find(ASSIGNS, getOperator());
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IUnaryExpression#getOperator()
	 */
	@Override
	public String getOperator(){
		return mOperator;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IUnaryExpression#getSubElement()
	 */
	@Override
	public IExpression getSubElement(){
		return g[0];
	}

	/* (non-Javadoc)
	 * @see core.expression.IUnaryExpression#isConditionExpression()
	 */
	@Override
	public boolean isConditionExpression() {
		return getOperator().equals(LOGIC_NOT);
	}

	/**
	 * Nếu là biểu thức logic phủ định (!(x < y)), kiểu trả về là BOOL, nếu không kiểu
	 * trả về trùng với kiểu của biểu thức con
	 */
	@Override
	public IType getType() {
		return isConditionExpression() ? BasicType.BOOL : 
			getSubElement().getSource().getType();
	}
}













