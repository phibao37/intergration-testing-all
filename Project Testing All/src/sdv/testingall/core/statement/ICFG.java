/**
 * Implementation for CFG interface
 * @file ICFG.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import java.util.List;

/**
 * CFG represent a Control Flow Graph, consist a tree of statement which the order is following by execution control
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public interface ICFG {

	/**
	 * Get the array of statement inside the CFG
	 * 
	 * @return cached statement array
	 */
	IStatement[] getStatements();

	/**
	 * Travel all possible path in the CFG
	 * 
	 * @return cached list of all basis path
	 */
	List<ITestPath> getAllBasisPath();

	/**
	 * Compute the statement coverage of given list of test path against this CFG
	 * 
	 * @param listTestPath
	 *            list of test path
	 * @return statement coverage in percent [0, 100]
	 */
	int computeStatementCoverage(List<ITestPath> listTestPath);

	/**
	 * Compute the branch coverage of given list of test path against this CFG
	 * 
	 * @param listTestPath
	 *            list of test path
	 * @return branch coverage in percent [0, 100]
	 */
	int computeBranchCoverage(List<ITestPath> listTestPath);
}
