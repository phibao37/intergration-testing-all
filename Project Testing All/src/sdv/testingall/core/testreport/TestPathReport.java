/**
 * Implement for test path report
 * @file TestPathReport.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.testreport;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.statement.ITestPath;

/**
 * Implement for test path report
 * 
 * @author VuSD
 *
 * @date 2016-12-15 VuSD created
 */
@NonNullByDefault
public class TestPathReport implements ITestPathReport {

	private ITestPath		testpath;
	private IOutputValue	symbolicOutput;
	private int				id;

	private @Nullable List<VariableNode>	inputData;
	private @Nullable IOutputValue			expectOutput;
	private @Nullable IOutputValue			actualOutput;

	/**
	 * Create new test path report
	 * 
	 * @param testpath
	 *            test path contains list of statement have been executed
	 * @param inputData
	 *            input value data for function to execute following test path
	 * @param symbolicOutput
	 *            predict the output based on symbolic execution
	 */
	public TestPathReport(int id, ITestPath testpath, @Nullable List<VariableNode> inputData,
			IOutputValue symbolicOutput)
	{
		this.id = id;
		this.testpath = testpath;
		this.inputData = inputData;
		this.symbolicOutput = symbolicOutput;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public ITestPath getPath()
	{
		return testpath;
	}

	@Override
	public @Nullable List<VariableNode> getInputData()
	{
		return inputData;
	}

	@Override
	public IOutputValue getSymblicOutput()
	{
		return symbolicOutput;
	}

	@Override
	public @Nullable IOutputValue getExpectedOutput()
	{
		return expectOutput;
	}

	@Override
	public @Nullable IOutputValue getActualOutput()
	{
		return actualOutput;
	}

	@Override
	public void setExpectedOutput(IOutputValue output)
	{
		this.expectOutput = output;
	}

	@Override
	public void setActualOutput(IOutputValue output)
	{
		this.actualOutput = output;
	}

	@Override
	public boolean equals(@Nullable Object obj)
	{
		if (obj == this) {
			return true;
		}
		if (obj == null || !(obj instanceof ITestPathReport)) {
			return false;
		}
		return getId() == ((ITestPathReport) obj).getId();
	}

	/**
	 * Implement for output value
	 * 
	 */
	public static class OutputValue implements IOutputValue {

		private int						resultType;
		private @Nullable IExpression	returnValue;

		/**
		 * Create new default output value with unknown solution
		 */
		public OutputValue()
		{
			resultType = RESULT_UNKNOWN;
		}

		/**
		 * Mark this output solution is infeasible
		 */
		public void setUnsat()
		{
			resultType = RESULT_UNSAT;
		}

		/**
		 * Mark this output to return a value
		 * 
		 * @param value
		 *            returned value as expression
		 */
		public void setReturnValue(IExpression value)
		{
			resultType = RESULT_RETURN_VALUE;
			returnValue = value;
		}

		@Override
		public int getResultType()
		{
			return resultType;
		}

		@Override
		public @Nullable IExpression getReturnValue()
		{
			return returnValue;
		}

	}

}
