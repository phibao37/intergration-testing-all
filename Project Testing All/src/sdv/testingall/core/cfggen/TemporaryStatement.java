/**
 * A temporary statement
 * @file TemporaryStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.cfggen;

import sdv.testingall.core.statement.IStatement;
import sdv.testingall.core.statement.NormalStatement;

/**
 * A temporary statement which will be skipped after generating CFG
 * 
 * @author VuSD
 *
 * @date 2016-12-12 VuSD created
 */
public class TemporaryStatement extends NormalStatement {

	/**
	 * Create new temporary statement
	 */
	public TemporaryStatement()
	{
		// do nothing
	}

	/**
	 * Create new temporary statement with next
	 * 
	 * @param next
	 *            next statement to be execute
	 */
	public TemporaryStatement(IStatement next)
	{
		setNextStatement(next);
	}
}
