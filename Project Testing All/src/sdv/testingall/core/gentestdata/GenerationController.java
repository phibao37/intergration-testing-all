/**
 * Controller for all type of test data generation
 * @file GenerationController.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import java.util.ArrayList;
import java.util.List;

import sdv.testingall.core.gentestdata.solver.ISolver.ISolverFactory;
import sdv.testingall.core.gentestdata.symbolicexec.ISymbolicExecution;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.statement.ITestPath;
import sdv.testingall.core.testreport.FunctionReport;
import sdv.testingall.core.testreport.IFunctionReport;

/**
 * Controller for all type of test data generation
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public class GenerationController extends BaseTestDataGeneration {

	private List<ITestDataGeneration> listStrategy;

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
		listStrategy = new ArrayList<>();
	}

	/**
	 * Add strategy to generate test data together<br/>
	 * All solver factory will be append to this strategy
	 * 
	 * @param strategy
	 *            strategy to generate test data
	 */
	public void addStraitgy(ITestDataGeneration strategy)
	{
		listStrategy.add(strategy);
		for (ISolverFactory factory : getSolvers()) {
			strategy.addSolver(factory);
		}
	}

	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public ISymbolicExecution createSymbolicExecution(ITestPath testpath)
	{
		// Will not to be call
		return null;
	}

	@Override
	public IFunctionReport generateData()
	{
		// Currently support static solution only
		if (listStrategy.size() == 1 && listStrategy.get(0).isAvailable()) {
			return listStrategy.get(0).generateData();
		}

		return new FunctionReport(getFunction());
	}

}
