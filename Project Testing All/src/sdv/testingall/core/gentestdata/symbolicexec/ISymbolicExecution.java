/**
 * Interface for symbolic execution
 * @file ISymbolicExecution.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.symbolicexec;

import sdv.testingall.core.gentestdata.IGenTestConfig;
import sdv.testingall.core.gentestdata.solver.IPathConstraint;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.statement.ITestPath;

/**
 * Symbolic execute a test path and get back the path constraint
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public interface ISymbolicExecution {

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
	 * Get the test path to parse constraint
	 * 
	 * @return test path object
	 */
	ITestPath getPath();

	/**
	 * Create new variable table for symbolic execution
	 * 
	 * @return variable table object
	 */
	IVariableTable createVariableTable();

	/**
	 * Get the constraint parsed by given test path
	 * 
	 * @return test path constraint
	 */
	IPathConstraint getConstraint();
}
