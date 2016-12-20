/**
 * Represent a C/C++ name reference expression
 * @file INameExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Represent a name reference expression
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public interface INameExpression extends ISetTypaableExpression {

	/**
	 * Get the last name in a name
	 * 
	 * @return last name
	 */
	String getName();

	/**
	 * Get the qualified name part excluding the {@link #getName()}
	 * 
	 * @return qualified name part, can be <code>null</code>
	 */
	String[] getNameParts();

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
