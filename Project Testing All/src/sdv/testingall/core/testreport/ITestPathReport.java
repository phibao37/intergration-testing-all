/**
 * Report for each test path inside a function
 * @file ITestPathReport.java
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
 * Report for each test path inside a function
 * 
 * @author VuSD
 *
 * @date 2016-12-13 VuSD created
 */
@NonNullByDefault
public interface ITestPathReport {

	/**
	 * Output state after testing function is executed
	 */
	public interface IOutputValue {

		/**
		 * Get type of result for this test path
		 * 
		 * @return type of result
		 */
		int getResultType();

		/** (Symbolic only) No solver can prove this test path is feasible or not */
		int RESULT_UNKNOWN = 0;

		/** (Symbolic only) This test path is not feasible (may be proved by solver) */
		int RESULT_UNSAT = -1;

		/** This test path is executed complete and return value to function caller (can be <code>void</code>) */
		int RESULT_RETURN_VALUE = 1;

		/** Executing this test path will lead to an exception to be throw */
		int RESULT_EXCEPTION = 2;

		/** A fatal error occur during executing, example in C++: division by 0, call <code>exit()</code> */
		int RESULT_ERROR = 3;

		/**
		 * Get the returned value after executing this test path
		 * 
		 * @return return value as expression or <code>null</code> if result type is not {@link #RESULT_RETURN_VALUE}
		 */
		@Nullable
		IExpression getReturnValue();
	}

	/**
	 * Get the unique identify for each test path report
	 * 
	 * @return report id
	 */
	int getId();

	/**
	 * Get the test path for this report
	 * 
	 * @return executed test path
	 */
	ITestPath getPath();

	/**
	 * Get list of input variable to execute following this test path, including function parameter and global
	 * variable.<br/>
	 * This list can be empty if the function does not has any parameter or access global variable
	 * 
	 * @return list of input variable or <code>null</code> if symbolic result type is {@link IOutputValue#RESULT_UNSAT}
	 *         or {@link IOutputValue#RESULT_UNKNOWN}
	 */
	@Nullable
	List<VariableNode> getInputData();

	/**
	 * Get the result that is proved by this application.<br/>
	 * This result may be differ with expected output or actual output
	 * 
	 * @return symbolic output data
	 */
	IOutputValue getSymblicOutput();

	/**
	 * Get the result that is user-expected value
	 * 
	 * @return expected output data
	 */
	@Nullable
	IOutputValue getExpectedOutput();

	/**
	 * Get the result after test execution
	 * 
	 * @return actual output data
	 */
	@Nullable
	IOutputValue getActualOutput();

	/**
	 * Set the result that is user-expected value
	 * 
	 * @param output
	 *            expected output data
	 */
	void setExpectedOutput(IOutputValue output);

	/**
	 * Set the result after test execution
	 * 
	 * @param output
	 *            actual output data
	 */
	void setActualOutput(IOutputValue output);

	/**
	 * Check for two report is same based on their id
	 * 
	 * @param obj
	 *            object to check
	 * @return two report are the same
	 */
	@Override
	boolean equals(@Nullable Object obj);
}
