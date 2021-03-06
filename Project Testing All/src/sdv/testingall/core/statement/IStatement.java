/**
 * Interface for all statement
 * @file IStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import sdv.testingall.core.element.IDisplayable;
import sdv.testingall.core.element.IFileLocation;
import sdv.testingall.core.expression.IExpression;

/**
 * Interface for all statement
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public interface IStatement extends IDisplayable, IFileLocation {

	/**
	 * Get the root expression attached to this statement
	 * 
	 * @return root expression
	 */
	IExpression getRoot();

	/**
	 * Check if this statement is condition (has TRUE/FALSE condition)
	 * 
	 * @return condition state
	 */
	boolean isCondition();

	/**
	 * Should this statement be list in test path
	 * 
	 * @return display state
	 */
	default boolean sholdDisplayInTestPath()
	{
		return true;
	}

	/**
	 * Set the visit state for this statement
	 * 
	 * @param visit
	 *            visit state
	 */
	void setVisit(boolean visit);

	/**
	 * Get the visit state for this statement<br/>
	 * Default value is <code>false</code>
	 * 
	 * @return visit state
	 */
	boolean isVisited();
}
