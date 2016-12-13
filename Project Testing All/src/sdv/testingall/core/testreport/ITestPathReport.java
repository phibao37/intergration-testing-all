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
	 * Get the test path for this report
	 * 
	 * @return executed test path
	 */
	ITestPath getPath();

	/**
	 * Get type of result for this test path
	 * 
	 * @return type of result
	 */
	int getResultType();

	/** This test path is not feasible (may be proved by solver) */
	int RESULT_UNSAT = 0;

	/** This test path is executed complete and return value to function caller (can be <code>void</code>) */
	int RESULT_RETURN_VALUE = 1;

	/** Executing this test path will lead to an exception to be throw */
	int RESULT_EXCEPTION = 2;

	/** A fatal error occur during executing, example in C++: division by 0, call <code>exit()</code> */
	int RESULT_ERROR = -1;

	/**
	 * Get list of input variable to execute following this test path, including function parameter and global
	 * variable.<br/>
	 * This list can be empty if the function does not has any parameter or access global variable
	 * 
	 * @return list of input variable or <code>null</code> if result type is {@link #RESULT_UNSAT}
	 */
	@Nullable
	List<VariableNode> getInputData();

	/**
	 * Get the returned value after executing this test path
	 * 
	 * @return return value as expression or <code>null</code> if result type is {@link #RESULT_UNSAT}
	 */
	@Nullable
	IExpression getReturnValue();
}
