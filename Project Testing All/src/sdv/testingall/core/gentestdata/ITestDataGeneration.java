/**
 * Interface for all type of test data generation
 * @file ITestDataGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import sdv.testingall.core.gentestdata.symbolicexec.ISymbolicExecution;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.statement.ITestPath;
import sdv.testingall.core.testreport.IFunctionReport;

/**
 * Interface for all type of test data generation
 * 
 * @author VuSD
 *
 * @date 2016-12-15 VuSD created
 */
public interface ITestDataGeneration {

	/**
	 * Get the function to generate test data
	 * 
	 * @return function node
	 */
	FunctionNode getFunction();

	/**
	 * Get the physical root of project
	 * 
	 * @return project node
	 */
	ProjectNode getRootProject();

	/**
	 * Get configuration during generate test
	 * 
	 * @return test configuration
	 */
	IGenTestConfig getConfig();

	/**
	 * Check whether this type of test generation is available to do its work
	 * 
	 * @return available state
	 */
	boolean isAvailable();

	/**
	 * Create new symbolic execution object for given test path
	 * 
	 * @param testpath
	 *            test path to symbolic execute
	 * @return symbolic execution controller
	 */
	ISymbolicExecution createSymbolicExecution(ITestPath testpath);

	/**
	 * Generate test data for given function
	 * 
	 * @return report for function
	 */
	IFunctionReport generateData();
}
