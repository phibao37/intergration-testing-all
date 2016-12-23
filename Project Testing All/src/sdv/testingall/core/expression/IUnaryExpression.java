/**
 * Interface for unary expression
 * @file IUnaryExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * A unary expression consist of an operator and a sub expression in the left/right side of the operator.<br/>
 * Example: <code>-x, ++i, j--</code>
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
@SuppressWarnings("nls")
public interface IUnaryExpression extends IExpressionGroup {

	/**
	 * Get the sub expression inside this unary group
	 * 
	 * @return sub expression
	 */
	IExpression getSubExpression();

	/**
	 * Get the operator that combine with the sub expression
	 * 
	 * @return unary operator
	 */
	String getOperator();

	/**
	 * Check whether the operator is in the left of the sub expression
	 * 
	 * @return is in left side
	 */
	boolean isLeftSide();

	/**
	 * Check if this unary expression contains assignment
	 * 
	 * @return is contains assignment
	 */
	default boolean isAssignExpression()
	{
		return getOperator().equals(INCREASE) || getOperator().equals(DECREASE);
	}

	/** Positive sign value: <code>x = +y;</code> */
	String	PLUS		= "+";
	/** Negative sign value: <code>x = -y;</code> */
	String	MINUS		= "-";
	/** Reverse logical value: <code>x = !(y == z);</code> */
	String	LOGIC_NOT	= "!";
	/** Add one increment: <code>x = y++;</code> */
	String	INCREASE	= "++";
	/** Subtract one decrement: <code>x = y--;</code> */
	String	DECREASE	= "--";

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
