/**
 * Unary expression for C/C++
 * @file CppUnaryExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.UnaryExpression;
import sdv.testingall.core.type.IType;

/**
 * Unary expression for C/C++
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class CppUnaryExpression extends UnaryExpression {

	/**
	 * Create new C/C++ unary expression
	 * 
	 * @param child
	 *            sub expression inside
	 * @param operator
	 *            unary operator
	 * @param leftSide
	 *            the operator is in the left of sub expression
	 */
	public CppUnaryExpression(IExpression child, String operator, boolean leftSide)
	{
		super(child, operator, leftSide);
	}

	@Override
	public IType getType()
	{
		return null;
	}

}
