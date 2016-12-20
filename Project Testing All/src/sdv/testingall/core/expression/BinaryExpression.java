/**
 * Implement for binary expression
 * @file BinaryExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Implement for binary expression
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public abstract class BinaryExpression extends ExpressionGroup implements IBinaryExpression {

	private String operator;

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
	public BinaryExpression(IExpression left, String operator, IExpression right)
	{
		super(left, right);
		this.operator = operator;
	}

	@Override
	public String computeContent()
	{
		return getLeft() + getOperator() + getRight();
	}

	@Override
	public IExpression getLeft()
	{
		return childs[0];
	}

	@Override
	public IExpression getRight()
	{
		return childs[1];
	}

	@Override
	public String getOperator()
	{
		return operator;
	}

}
