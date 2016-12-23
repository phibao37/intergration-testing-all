/**
 * Interface for solver to solve logic constraint
 * @file ISolver.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver;

import java.util.List;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.gentestdata.symbolicexec.IVariable;

/**
 * Interface for solver to solve logic constraint
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public interface ISolver {

	/**
	 * Factory to create new solver for each constraint
	 */
	public interface ISolverFactory {

		/**
		 * Check whether this solver is available to perform solving
		 * 
		 * @return available state
		 */
		boolean isAvailable();

		/**
		 * Create solver for given path constraint
		 * 
		 * @param constraint
		 *            path constraint need to solve
		 * @return solver object
		 */
		ISolver createSolver(IPathConstraint constraint);
	}

	/**
	 * Get the path constraint need to solve
	 * 
	 * @return path constraint object
	 */
	IPathConstraint getConstraint();

	/**
	 * Get type of result after solving
	 * 
	 * @return one of {@link #RESULT_UNSAT}, {@link #RESULT_UNKNOWN}, {@link #RESULT_SAT}
	 */
	int getResultType();

	/** The solver prove that the constraint is unsatisfied */
	int RESULT_UNSAT = -1;

	/** The solver can't solve the constraint by some reason */
	int RESULT_UNKNOWN = 0;

	/** The solver solve the constraint with satisfied, input data can then be obtained from {@link #getInputData()} */
	int RESULT_SAT = 1;

	/**
	 * Get the input data after solving
	 * 
	 * @return input data
	 */
	List<IVariable> getInputData();

	/**
	 * Get the value after execute test path with input from {@link #getInputData()}
	 * 
	 * @return return value as expression
	 */
	IExpression getPathReturnData();
}
