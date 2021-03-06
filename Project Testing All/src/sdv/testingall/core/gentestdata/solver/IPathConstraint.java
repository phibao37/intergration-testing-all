/**
 * Interface for test path constraint
 * @file IPathConstraint.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.gentestdata.symbolicexec.IVariable;

/**
 * Constraint for test path can be execute
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public interface IPathConstraint {

	/**
	 * Get the list of input variable need to be generate value
	 * 
	 * @return list of input variable, include function parameter and global variable
	 */
	List<IVariable> getInputs();

	/**
	 * Get the list of constraint that make test path can be execute
	 * 
	 * @return path constraint
	 */
	List<IExpression> getConstraints();

	/**
	 * Get the return expression that this test path procedure
	 * 
	 * @return return value as expression or <code>null</code> if test path does not return value
	 */
	@Nullable
	IExpression getPathReturnValue();
}
