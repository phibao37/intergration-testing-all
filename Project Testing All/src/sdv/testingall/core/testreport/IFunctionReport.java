/**
 * Test report for a function
 * @file IFunctionReport.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.testreport;

import java.util.List;

import sdv.testingall.core.node.FunctionNode;

/**
 * Test report for a function, include some list of test path report and provide test result information
 * 
 * @author VuSD
 *
 * @date 2016-12-14 VuSD created
 */
public interface IFunctionReport extends List<IFunctionReport.ICoverageReport> {

	/**
	 * Test report for each type of coverage scenario
	 */
	public interface ICoverageReport {

		/**
		 * Get the coverage for report
		 * 
		 * @return coverage scenario
		 */
		Coverage getCoverage();

		/**
		 * Get the list report for test path that cover this scenario
		 * 
		 * @return list of test path report
		 */
		List<ITestPathReport> getPath();

		/**
		 * Get percent of coverage that all path in {@link #getPath()} contribute to
		 * 
		 * @return coverage in percent [0, 100]
		 */
		int computePercent();
	}

	/**
	 * Get the function that this report belongs to
	 * 
	 * @return function node
	 */
	FunctionNode getFunction();
}
