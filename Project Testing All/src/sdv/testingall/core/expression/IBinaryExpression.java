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
