/**
 * Factory to create new expression object
 * @file IExpressionFactory.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Factory to create new expression object
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public interface IExpressionFactory {

	/**
	 * Create new binary expression
	 * 
	 * @param left
	 *            left expression inside binary
	 * @param operator
	 *            binary joining operator
	 * @param right
	 *            right expression inside binary
	 */
	IBinaryExpression createBinary(IExpression left, String operator, IExpression right);

	/**
	 * Create new unary expression
	 * 
	 * @param child
	 *            sub expression inside
	 * @param operator
	 *            unary operator
	 * @param leftSide
	 *            the operator is in the left of sub expression
	 */
	IUnaryExpression createUnary(IExpression child, String operator, boolean leftSide);
}
