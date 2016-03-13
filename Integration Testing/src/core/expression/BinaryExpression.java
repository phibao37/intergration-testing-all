package core.expression;

import api.expression.IBinaryExpression;
import api.expression.IExpression;
import api.models.IType;
import core.Utils;
import core.models.type.BasicType;

/**
 * Mô tả một biểu thức nhị phân, bao gồm một dấu phép toán và hai
 * biểu thức con ở hai bên dấu phép toán
 * @example
 * <ul>
 * 	<li>x == y: phép toán so sánh bằng</li>
 * 	<li>x = y: phép toán gán</li>
 * 	<li>(x < 1) && isValid(): phép toán logic AND</li>
 * 	<li>x + y: phép toán đại số</li>
 * </ul>
 * @author ducvu
 *
 */
public class BinaryExpression extends ExpressionGroup implements IBinaryExpression{
	
	/**
	 * Phép toán gán (=)
	 */
	public static final String ASSIGN = "=";
	
	/** Phép toán cộng và gán  (+=) */
	public static final String ASSIGN_ADD = "+=";
	
	/** Phép toán trừ và gán */
	public static final String ASSIGN_MINUS = "-=";
	
	/** Phép toán nhân và gán */
	public static final String ASSIGN_MUL = "*=";
	
	/** Phép toán chia và gán */
	public static final String ASSIGN_DIV = "/=";
	
	/** Phép toán lấy dư và gán */
	public static final String ASSIGN_MOD = "%=";
	
	/** Phép toán so sánh bằng  (==) */
	public static final String EQUALS = "==";
	
	/** Phép toán so sánh khác  (!=) */
	public static final String NOT_EQUALS = "!=";
	
	/** Phép toán so sánh nhỏ hơn  (<) */
	public static final String LESS = "<";
	
	/** Phép toán so sánh nhỏ hơn hoặc bằng */
	public static final String LESS_EQUALS = "<=";
	
	/** Phép toán so sánh lón hơn */
	public static final String GREATER = ">";
	
	/** Phép toán so sánh lớn hơn hoặc bằng */
	public static final String GREATER_EQUALS = ">=";
	
	/** Phép toán cộng */
	public static final String ADD = "+";
	
	/** Phép toán trừ */
	public static final String MINUS = "-";
	
	/** Phép toán nhân */
	public static final String MUL = "*";
	
	/** Phép toán chia */
	public static final String DIV = "/";
	
	/** Phép toán lấy phần dư */
	public static final String MOD = "%";
	
	/** Phép toán logic và */
	public static final String LOGIC_AND = "&&";
	
	/** Phép toán logic hoặc */
	public static final String LOGIC_OR = "||";
	
	private static final String[] ASSIGNS = {ASSIGN, ASSIGN_ADD, ASSIGN_MINUS,
		ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD};
	
	private static final String[] CONDITIONS = {LOGIC_AND, LOGIC_OR, EQUALS, 
		NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS};
	
	private String mOperator;
	
	
	/**
	 * Tạo một biểu thức nhị phân cùng với 2 biểu thức con và phép toán
	 * @param leftElement biểu thức biểu thức ở bên trái phép toán
	 * @param operator chuỗi hiển thị của phép toán
	 * @param rightElement biểu thức biểu thức ở bên phải phép toán
	 */
	public BinaryExpression(IExpression leftElement, String operator, 
			IExpression rightElement){
		super(leftElement, rightElement);
		mOperator = operator;
	}

	@Override
	protected String generateContent() {
		return String.format("(%s%s%s)", getLeft(), getOperator(), getRight());
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IBinaryExpression#getLeft()
	 */
	@Override
	public IExpression getLeft(){
		return g[0];
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IBinaryExpression#getOperator()
	 */
	@Override
	public String getOperator(){
		return mOperator;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IBinaryExpression#getRight()
	 */
	@Override
	public IExpression getRight(){
		return g[1];
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IBinaryExpression#isAssignOperator()
	 */
	@Override
	public boolean isAssignOperator(){
		return Utils.find(ASSIGNS, getOperator());
	}

	/* (non-Javadoc)
	 * @see core.expression.IBinaryExpression#isConditionExpression()
	 */
	@Override
	public boolean isConditionExpression() {
		return Utils.find(CONDITIONS, getOperator());
	}

	/**
	 * Với biểu thức logic, kiểu trả về là BOOL<br/>
	 * Với biểu thức gán, kiểu trả về là kiểu của biểu thức bên trái<br/>
	 * Các trường hợp còn lại (phép toán +,-,*,/,%), kiểu trả về là kiểu có cỡ 
	 * lớn hơn trong 2 kiểu của 2 biểu thức 2 bên
	 */
	@Override
	public IType getType() {
		if (isConditionExpression())
			return BasicType.BOOL;
		
		IType left = getLeft().getSource().getType(),
			right = getRight().getSource().getType();
		
		if (isAssignOperator())
			return left;
		return left.compareTo(right) > 0 ? left : right;
	}
}
