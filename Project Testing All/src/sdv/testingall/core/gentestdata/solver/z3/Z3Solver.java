/**
 * Implement for Z3 solver
 * @file Z3Solver.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver.z3;

import sdv.testingall.core.gentestdata.solver.BaseSolver;
import sdv.testingall.core.gentestdata.solver.IPathConstraint;

/**
 * Solve path constraint using Z3 solver - a external solver from Microsoft.<br/>
 * This library will be loaded from PATH environment variable
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public class Z3Solver extends BaseSolver {

	/**
	 * Create new Z3 solver to execute solving constraint
	 * 
	 * @param constraint
	 *            path constraint need to solve
	 */
	Z3Solver(IPathConstraint constraint)
	{
		super(constraint);
	}

}
