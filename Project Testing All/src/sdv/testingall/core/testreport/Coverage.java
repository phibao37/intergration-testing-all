/**
 * Constant for coverage scenario
 * @file Coverage.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.testreport;

/**
 * Constant for coverage scenario
 * 
 * @author VuSD
 *
 * @date 2016-12-14 VuSD created
 */
public enum Coverage {

	/**
	 * Coverage scenario that all statement is executed
	 */
	STATEMENT,

	/**
	 * Coverage scenario that all branch (example TRUE/FALSE branch in <code>if, while, ...</code> statement) is
	 * executed
	 */
	BRANCH,

	/**
	 * Coverage scenario that all condition-part is executed in both branch
	 */
	SUBCONDITION
}
