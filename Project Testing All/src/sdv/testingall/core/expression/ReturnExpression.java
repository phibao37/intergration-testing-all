/**
 * Implement for return expression
 * @file ReturnExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import sdv.testingall.core.type.IType;

/**
 * Implement for return expression
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public class ReturnExpression extends UnaryExpression implements IReturnExpression {

	/**
	 * Create new return expression
	 * 
	 * @param child
	 *            sub expression inside, can be {@code null} if is void return
	 */
	public ReturnExpression(IExpression child)
	{
		super(child, RETURN, true);
	}

	@Override
	public IType getType()
	{
		return getSubExpression().getType();
	}

}
