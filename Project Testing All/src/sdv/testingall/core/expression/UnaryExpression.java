/**
 * Implement for unary expression
 * @file UnaryExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Implement for unary expression
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public abstract class UnaryExpression extends ExpressionGroup implements IUnaryExpression {

	private String	operator;
	private boolean	leftSide;

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
	public UnaryExpression(IExpression child, String operator, boolean leftSide)
	{
		super(child);
		this.operator = operator;
		this.leftSide = leftSide;
	}

	@Override
	public String computeContent()
	{
		if (leftSide) {
			return getOperator() + getSubExpression();
		} else {
			return getSubExpression() + getOperator();
		}
	}

	@Override
	public IExpression getSubExpression()
	{
		return childs[0];
	}

	@Override
	public String getOperator()
	{
		return operator;
	}

	@Override
	public boolean isLeftSide()
	{
		return leftSide;
	}

}
