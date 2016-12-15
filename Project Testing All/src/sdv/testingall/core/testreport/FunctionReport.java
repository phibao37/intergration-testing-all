/**
 * Implement for function report
 * @file FunctionReport.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.testreport;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement for function report
 * 
 * @author VuSD
 *
 * @date 2016-12-15 VuSD created
 */
public class FunctionReport extends ArrayList<IFunctionReport.ICoverageReport> implements IFunctionReport {

	/**
	 * Report for each coverage scenario
	 */
	public static class CoverageReport implements ICoverageReport {

		private Coverage				coverage;
		private List<ITestPathReport>	listTestPath;
		private int						percent;

		/**
		 * Create new coverage report
		 * 
		 * @param coverage
		 *            coverage scenario
		 * @param listTestPath
		 *            list of test path to cover
		 * @param percent
		 *            percent computed to show how list of test path cover for testing function
		 */
		public CoverageReport(Coverage coverage, List<ITestPathReport> listTestPath, int percent)
		{
			this.coverage = coverage;
			this.listTestPath = listTestPath;
			this.percent = percent;
		}

		@Override
		public Coverage getCoverage()
		{
			return coverage;
		}

		@Override
		public List<ITestPathReport> getPath()
		{
			return listTestPath;
		}

		@Override
		public int computePercent()
		{
			return percent;
		}

	}
}
