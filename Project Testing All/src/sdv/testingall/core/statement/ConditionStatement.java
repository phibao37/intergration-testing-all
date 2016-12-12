/**
 * Implementation for condition statement
 * @file ConditionStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import sdv.testingall.core.expression.IExpression;

/**
 * Represent a condition statement
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public class ConditionStatement extends BaseStatement implements IConditionStatement {

	private IStatement	trueBranch;
	private IStatement	falseBranch;

	/**
	 * Create new condition statement
	 * 
	 * @param root
	 *            root expression
	 */
	public ConditionStatement(IExpression root)
	{
		super(root);
	}

	@Override
	public void setBranch(IStatement trueBranch, IStatement falseBranch)
	{
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	@Override
	public IStatement trueBranch()
	{
		return trueBranch;
	}

	@Override
	public IStatement falseBranch()
	{
		return falseBranch;
	}

}
