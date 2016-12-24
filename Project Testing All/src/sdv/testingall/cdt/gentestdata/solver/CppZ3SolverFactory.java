/**
 * Factory to create new Z3 solver
 * @file CppZ3SolverFactory.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gentestdata.solver;

import sdv.testingall.cdt.gentestdata.ICppGenTestConfig;
import sdv.testingall.core.gentestdata.IGenTestConfig;
import sdv.testingall.core.gentestdata.solver.IPathConstraint;
import sdv.testingall.core.gentestdata.solver.ISolver.ISolverFactory;

/**
 * Factory to create new Z3 solver
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public class CppZ3SolverFactory implements ISolverFactory {

	private static boolean Z3_AVAILABE;

	/**
	 * Load Z3 library from system
	 */
	static {
		try {
			com.microsoft.z3.Global.ToggleWarningMessages(false);
			Z3_AVAILABE = true;
		} catch (Throwable e) {
			Z3_AVAILABE = false;
		}
	}

	@Override
	public boolean isAvailable()
	{
		return Z3_AVAILABE;
	}

	@Override
	public CppZ3Solver createSolver(IPathConstraint constraint, IGenTestConfig config)
	{
		return new CppZ3Solver(constraint, (ICppGenTestConfig) config);
	}

}
