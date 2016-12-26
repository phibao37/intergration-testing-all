/**
 * Interface for binary expression
 * @file IBinaryExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * A binary expression consist of a left expression, an operator, and a right expression.<br/>
 * Example: <code>a + 1, x && y, cout << 1</code>
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
@SuppressWarnings("nls")
public interface IBinaryExpression extends IExpressionGroup {

	/**
	 * Get the expression in the left of this binary group
	 * 
	 * @return left expression
	 */
	IExpression getLeft();

	/**
	 * Get the expression in the right of this binary group
	 * 
	 * @return right expression
	 */
	IExpression getRight();

	/**
	 * Get the operator that join left and right expression together
	 * 
	 * @return binary operator
	 */
	String getOperator();

	/**
	 * Check if this binary expression contains assignment
	 * 
	 * @return is contains assignment
	 */
	default boolean isAssignExpression()
	{
		switch (getOperator()) {
		case ASSIGN:
		case ADD_ASSIGN:
		case MINUS_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:

		case BIT_AND_ASSIGN:
		case BIT_OR_ASSIGN:
		case BIT_XOR_ASSIGN:
		case LEFT_SHIFT_ASSIGN:
		case RIGHT_SHIFT_ASSIGN:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check if this unary expression contains bitwise operator
	 * 
	 * @return is contains bitwise operator
	 */
	default boolean isBitwiseExpression()
	{
		switch (getOperator()) {
		case BIT_AND:
		case BIT_OR:
		case BIT_XOR:
		case LEFT_SHIFT:
		case RIGHT_SHIFT:
			return true;
		default:
			return false;
		}
	}

	/** Assign variable: <code>a = b+1</code> */
	String	ASSIGN	= "=";
	/** Add value: <code>a + b</code> */
	String	ADD		= "+";
	/** Subtract value: <code>a - b</code> */
	String	MINUS	= "-";
	/** Multiply value: <code>a * b</code> */
	String	MUL		= "*";
	/** Divide value: <code>a / b</code> */
	String	DIV		= "/";
	/** Remainder value: <code>a % b</code> */
	String	MOD		= "%";

	/** Add and assign value: <code>a += b</code> */
	String	ADD_ASSIGN		= "+=";
	/** Subtract and assign value: <code>a -= b</code> */
	String	MINUS_ASSIGN	= "-=";
	/** Multiply and assign value: <code>a *= b</code> */
	String	MUL_ASSIGN		= "*=";
	/** Divide and assign value: <code>a /= b</code> */
	String	DIV_ASSIGN		= "/=";
	/** Remainder and assign value: <code>a %= b</code> */
	String	MOD_ASSIGN		= "%=";

	/** Logic and: <code>x > 0 && y > 0</code> */
	String	LOGIC_AND	= "&&";
	/** Logic or: <code>x > 0 || y > 0</code> */
	String	LOGIC_OR	= "||";

	/** Compare equals: <code>a == b</code> */
	String	EQUALS			= "==";
	/** Compare not equals: <code>a != b</code> */
	String	NOT_EQUALS		= "!=";
	/** Compare less than: <code>a < b</code> */
	String	LESS			= "<";
	/** Compare less than or equals: <code>a <= b</code> */
	String	LESS_EQUALS		= "<=";
	/** Compare greater than: <code>a > b</code> */
	String	GREATER			= ">";
	/** Compare greater than or equals: <code>a >= b</code> */
	String	GREATER_EQUALS	= ">=";

	/** Bitwise AND: <code>a & b</code> */
	String	BIT_AND		= "&";
	/** Bitwise OR: <code>a | b</code> */
	String	BIT_OR		= "|";
	/** Bitwise XOR: <code>a ^ b</code> */
	String	BIT_XOR		= "^";
	/** Left shift: <code>0b11 << 3 --> 0b11000 </code> */
	String	LEFT_SHIFT	= "<<";
	/** Right shift: <code>0b11000 >> 3 --> 0b11 </code> */
	String	RIGHT_SHIFT	= ">>";

	/** Bitwise AND and assign: <code>a &= b</code> */
	String	BIT_AND_ASSIGN		= "&=";
	/** Bitwise OR and assign: <code>a |= b</code> */
	String	BIT_OR_ASSIGN		= "|=";
	/** Bitwise XOR and assign: <code>a ^= b</code> */
	String	BIT_XOR_ASSIGN		= "^=";
	/** Left shift and assign: <code>a <<= 3</code> */
	String	LEFT_SHIFT_ASSIGN	= "<<=";
	/** Right shift and assign: <code>a >>= 3</code> */
	String	RIGHT_SHIFT_ASSIGN	= ">>=";

	@Override
	default int handleVisit(IExpressionVisitor visitor)
	{
		return visitor.visit(this);
	}

	@Override
	default void handleLeave(IExpressionVisitor visitor)
	{
		visitor.leave(this);
	}

}
