/**
 * Abstract for all expression
 * @file Expression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import sdv.testingall.core.type.IType;

/**
 * Abstract for all expression
 * 
 * @author VuSD
 *
 * @date 2016-12-08 VuSD created
 */
public abstract class Expression implements IExpression {

	private String content;

	@Override
	public void setContent(String content)
	{
		this.content = content;
	}

	@Override
	public String toString()
	{
		return content;
	}

	@Override
	public Expression clone()
	{
		try {
			return (Expression) super.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
	}

	@Override
	public IType bind()
	{
		return null;
	}

}
