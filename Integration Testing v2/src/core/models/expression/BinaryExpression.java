package core.models.expression;

import core.Utils;
import core.models.Expression;
import core.models.ExpressionGroup;
import core.visitor.ExpressionVisitor;

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
public class BinaryExpression extends ExpressionGroup {
	
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
	
	private String mOperator;
	
	
	/**
	 * Tạo một biểu thức nhị phân cùng với 2 biểu thức con và phép toán
	 * @param leftElement biểu thức biểu thức ở bên trái phép toán
	 * @param operator chuỗi hiển thị của phép toán
	 * @param rightElement biểu thức biểu thức ở bên phải phép toán
	 */
	public BinaryExpression(Expression leftElement, String operator, 
			Expression rightElement){
		super(leftElement, rightElement);
		mOperator = operator;
	}

	@Override
	protected String generateContent() {
		return String.format("(%s%s%s)", getLeft(), getOperator(), getRight());
	}
	
	/**
	 * Trả về biểu thức nằm ở bên trái
	 */
	public Expression getLeft(){
		return g[0];
	}
	
	/**
	 * Trả về phép toán của biểu thức
	 */
	public String getOperator(){
		return mOperator;
	}
	
	/**
	 * Trả về biểu thức nằm ở bên phải
	 */
	public Expression getRight(){
		return g[1];
	}
	
	/**
	 * Kiểm tra biểu thức gán
	 */
	public boolean isAssignOperator(){
		return Utils.find(ASSIGNS, getOperator());
	}

	@Override
	protected int handle(ExpressionVisitor visitor) {
		return visitor.visit(this);
	}
	
}
