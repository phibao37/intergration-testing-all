/**
 * Binary expression for C/C++
 * @file CppBinaryExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import sdv.testingall.core.expression.BinaryExpression;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.type.IType;

/**
 * Binary expression for C/C++
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class CppBinaryExpression extends BinaryExpression {

	/**
	 * Create new C/C++ binary expression
	 * 
	 * @param left
	 *            left expression inside binary
	 * @param operator
	 *            binary joining operator
	 * @param right
	 *            right expression inside binary
	 */
	public CppBinaryExpression(IExpression left, String operator, IExpression right)
	{
		super(left, operator, right);
	}

	@Override
	public IType getType()
	{
		return null;
	}

}
