/**
 * Basic implement for generate test data
 * @file BaseTestDataGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;

/**
 * Basic implement for generate test data
 * 
 * @author VuSD
 *
 * @date 2016-12-15 VuSD created
 */
public abstract class BaseTestDataGeneration implements ITestDataGeneration {

	private ProjectNode		project;
	private FunctionNode	function;
	private IGenTestConfig	config;

	/**
	 * Create new test generation object
	 * 
	 * @param project
	 *            root of project, use to resolve binding, find reference, ...
	 * @param function
	 *            function node need to generate
	 * @param config
	 *            configuration during test
	 */
	protected BaseTestDataGeneration(ProjectNode project, FunctionNode function, IGenTestConfig config)
	{
		this.project = project;
		this.function = function;
		this.config = config;
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

}
