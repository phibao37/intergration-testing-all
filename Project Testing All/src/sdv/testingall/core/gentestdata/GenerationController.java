/**
 * Controller for all type of test data generation
 * @file GenerationController.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.testreport.IFunctionReport;

/**
 * Controller for all type of test data generation
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public class GenerationController extends BaseTestDataGeneration {

	/**
	 * Create new test generation controller
	 * 
	 * @param project
	 *            root of project, use to resolve binding, find reference, ...
	 * @param function
	 *            function node need to generate
	 * @param config
	 *            configuration during test
	 */
	public GenerationController(ProjectNode project, FunctionNode function, IGenTestConfig config)
	{
		super(project, function, config);
	}

	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public IFunctionReport generateData()
	{
		// Merge all report together
		return null;
	}

}
