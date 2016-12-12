/**
 * Implementation for condition statement
 * @file IConditionStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

/**
 * After a condition statement is executed, the evaluated value will be used to decide which statement in TRUE/FALSE
 * branch will be execute
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public interface IConditionStatement extends IStatement {

	@Override
	default boolean isCondition()
	{
		return true;
	}

	/**
	 * Get the statement will be executed if evaluated value is TRUE
	 * 
	 * @return statement in TRUE branch
	 */
	IStatement trueBranch();

	/**
	 * Get the statement will be executed if evaluated value is FALSE
	 * 
	 * @return statement in FALSE branch
	 */
	IStatement falseBranch();

	/**
	 * Set branch statement
	 * 
	 * @param trueBranch
	 *            statement in TRUE branch
	 * @param falseBranch
	 *            statement in FALSE branch
	 */
	void setBranch(IStatement trueBranch, IStatement falseBranch);
}
