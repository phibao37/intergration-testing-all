/**
 * Statically generate test data
 * @file StaticTestDataGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import java.util.ArrayList;
import java.util.List;

import sdv.testingall.core.gentestdata.solver.IPathConstraint;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.statement.CFG;
import sdv.testingall.core.statement.ICFG;
import sdv.testingall.core.statement.ITestPath;
import sdv.testingall.core.testreport.Coverage;
import sdv.testingall.core.testreport.FunctionReport;
import sdv.testingall.core.testreport.IFunctionReport;
import sdv.testingall.core.testreport.IFunctionReport.ICoverageReport;
import sdv.testingall.core.testreport.ITestPathReport;
import sdv.testingall.core.testreport.TestPathReport;
import sdv.testingall.core.testreport.TestPathReport.OutputValue;

/**
 * Generate test data that do not need to invoke real execution in testing function.<br/>
 * Basic follow for this approach:
 * <ol>
 * <li>Generate control flow graph for given coverage configuration</li>
 * <li>Discover all basis test path and some special path such as looping</li>
 * <li>For each test path, using symbolic execution to obtains all path constrains</li>
 * <li>Using solver to solve path constrains and get back the solution as input value</li>
 * <li>For each type of coverage, choose list of test path that best satisfy coverage</li>
 * </ol>
 * 
 * @author VuSD
 *
 * @date 2016-12-15 VuSD created
 */
public abstract class StaticTestDataGeneration extends BaseTestDataGeneration {

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
	public StaticTestDataGeneration(ProjectNode project, FunctionNode function, IGenTestConfig config)
	{
		super(project, function, config);
	}

	@Override
	public boolean isAvailable()
	{
		// Static strategy is always available
		return true;
	}

	@Override
	public IFunctionReport generateData()
	{
		FunctionNode function = getFunction();

		// Generate control flow graph for given coverage configuration
		List<Coverage> coverages = getConfig().genCoverage();
		boolean coverStatement = coverages.contains(Coverage.STATEMENT);
		boolean coverBranch = coverages.contains(Coverage.BRANCH);
		boolean coverSubcond = coverages.contains(Coverage.SUBCONDITION);
		ICFG cfg_12 = null, cfg_3 = null;
		List<ITestPathReport> tpReport_12 = null, tpReport_3 = null;
		ICoverageReport[] cvReport = new ICoverageReport[3];

		// Generate for statement + branch
		if (coverStatement || coverBranch) {
			cfg_12 = function.getCFG(CFG.TYPE_NORMAL);
			tpReport_12 = generateDataAllPath(cfg_12);

			if (coverStatement) {
				cvReport[0] = getStatementCoverage(cfg_12, tpReport_12, Coverage.STATEMENT);
			}
			if (coverBranch) {
				cvReport[1] = getBranchCoverage(cfg_12, tpReport_12, Coverage.BRANCH);
			}
		}

		// Generate for sub-condition
		if (coverSubcond) {
			cfg_3 = function.getCFG(CFG.TYPE_SUBCONDITION);

			// There is no different between two CFG, use the old generated data
			if (cfg_12 != null && cfg_12.getStatements().length == cfg_3.getStatements().length) {
				tpReport_3 = tpReport_12;

				// Check for branch coverage exist, use same report
				if (cvReport[1] != null) {
					cvReport[2] = new FunctionReport.CoverageReport(Coverage.SUBCONDITION, cvReport[1].getPath(),
							cvReport[1].computePercent());
				} else {
					cvReport[2] = getBranchCoverage(cfg_3, tpReport_3, Coverage.SUBCONDITION);
				}
			} else {
				tpReport_3 = generateDataAllPath(cfg_3);
				cvReport[2] = getBranchCoverage(cfg_3, tpReport_3, Coverage.SUBCONDITION);
			}
		}

		// Create final report and push data into
		IFunctionReport report = new FunctionReport(function);
		for (ICoverageReport cv : cvReport) {
			if (cv != null) {
				report.add(cv);
			}
		}
		return report;
	}

	/**
	 * Discover all possible path from given CFG and generate input data for these path
	 * 
	 * @param cfg
	 *            graph to find path
	 * @return list of test path report for each test path discovered
	 */
	protected List<ITestPathReport> generateDataAllPath(ICFG cfg)
	{
		// Discover all path from given CFG
		List<ITestPath> allPath = cfg.getAllBasisPath();
		List<ITestPathReport> listReport = new ArrayList<>(allPath.size());
		int id = 0;

		for (ITestPath testpath : allPath) {
			OutputValue symbolicOutput = new OutputValue();
			List<VariableNode> inputData = null;

			System.out.printf("#### Gen for path: [%s]%n", testpath); //$NON-NLS-1$
			IPathConstraint constraint = createSymbolicExecution(testpath).getConstraint();
			System.out.printf("Constraint: %s%n", constraint); //$NON-NLS-1$
			System.out.println();

			TestPathReport pathReport = new TestPathReport(testpath, inputData, symbolicOutput);
			pathReport.setId(id++);
			listReport.add(pathReport);
		}

		return listReport;
	}

	/**
	 * Compute coverage report to best cover statement
	 * 
	 * @param cfg
	 *            graph to analyze path
	 * @param listReport
	 *            list of test path report
	 * @param cover
	 *            coverage constant
	 * @return coverage report contains list of test path report
	 */
	protected ICoverageReport getStatementCoverage(ICFG cfg, List<ITestPathReport> listReport, Coverage cover)
	{
		return null;
	}

	/**
	 * Compute coverage report to best cover branch
	 * 
	 * @param cfg
	 *            graph to analyze path
	 * @param listReport
	 *            list of test path report
	 * @param cover
	 *            coverage constant
	 * @return coverage report contains list of test path report
	 */
	protected ICoverageReport getBranchCoverage(ICFG cfg, List<ITestPathReport> listReport, Coverage cover)
	{
		return null;
	}

}
