/**
 * Implementation for normal statement
 * @file INormalStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A normal statement when executed will lead to next statement to be execute
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public interface INormalStatement extends IStatement {

	@Override
	default boolean isCondition()
	{
		return false;
	}

	/**
	 * Get the next statement to be execute
	 * 
	 * @return next statement or <code>null</code> if this is final statement
	 */
	@Nullable
	IStatement nextStatement();

}
