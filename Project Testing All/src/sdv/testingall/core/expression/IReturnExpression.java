/**
 * Interface for 
 * @file IReturnExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Represent a return expression that finish the function and pass back the value
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public interface IReturnExpression extends IUnaryExpression {

	@Override
	default String getOperator()
	{
		return RETURN;
	}

	@Override
	default boolean isLeftSide()
	{
		return true;
	}

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

	/** Return operator */
	String RETURN = "return "; //$NON-NLS-1$

}
