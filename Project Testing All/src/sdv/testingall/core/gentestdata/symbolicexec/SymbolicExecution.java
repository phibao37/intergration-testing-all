/**
 * Implementation for symbolic execution
 * @file SymbolicExecution.java
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
 * Implementation for symbolic execution. The test path obtained from function will be parsed, each statement inside
 * will be analyze and grab all constraint so that the test path can be execute
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public class SymbolicExecution implements ISymbolicExecution {

	private ProjectNode		project;
	private FunctionNode	function;
	private IGenTestConfig	config;
	private ITestPath		testpath;

	private IPathConstraint constraint;

	/**
	 * Create new symbolic execution controller
	 * 
	 * @param project
	 *            root of project, use to resolve binding, find reference, ...
	 * @param function
	 *            function node need to generate
	 * @param config
	 *            configuration during test
	 * @param testpath
	 *            test path to analyze
	 */
	public SymbolicExecution(ProjectNode project, FunctionNode function, IGenTestConfig config, ITestPath testpath)
	{
		this.project = project;
		this.function = function;
		this.config = config;
		this.testpath = testpath;
	}

	@Override
	public FunctionNode getFunction()
	{
		return function;
	}

	@Override
	public ProjectNode getRootProject()
	{
		return project;
	}

	@Override
	public IGenTestConfig getConfig()
	{
		return config;
	}

	@Override
	public ITestPath getPath()
	{
		return testpath;
	}

	@Override
	public IPathConstraint getConstraint()
	{
		return constraint;
	}

}
