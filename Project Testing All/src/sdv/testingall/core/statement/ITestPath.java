/**
 * Implementation for test path interface
 * @file ITestPath.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import java.util.List;

/**
 * A test path consist of a list of statement which will be executed one by one
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public interface ITestPath extends List<IStatement> {

	/**
	 * Display test path in following format: statement1 -> statement2 -> ... -> statementN
	 * 
	 * @return test path content
	 */
	@Override
	String toString();

	/**
	 * Clone the test path, copy statement from begin to the given index
	 * 
	 * @param index
	 *            last statement to be copied to
	 * @return cloned test path
	 */
	ITestPath cloneAt(int index);

	/**
	 * Get a copy of test path
	 * 
	 * @return cloned test path
	 */
	ITestPath clone();
}
