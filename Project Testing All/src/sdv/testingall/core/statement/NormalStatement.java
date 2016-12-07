/**
 * Implementation for normal statement
 * @file NormalStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import sdv.testingall.core.expression.IExpression;

/**
 * Represent a normal statement
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public class NormalStatement extends BaseStatement implements INormalStatement {

	private IStatement next;

	/**
	 * Create new normal statement
	 * 
	 * @param root
	 *            root expression
	 */
	public NormalStatement(IExpression root)
	{
		super(root);
	}

	/**
	 * Set the next statement to be execute
	 * 
	 * @param next
	 *            next statement
	 */
	public void setNextStatement(IStatement next)
	{
		this.next = next;
	}

	@Override
	public IStatement nextStatement()
	{
		return next;
	}

}
