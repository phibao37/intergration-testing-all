/**
 * Abstract implement for solver
 * @file BaseSolver.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver;

import java.util.List;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.gentestdata.symbolicexec.IVariable;

/**
 * Abstract implement for solver
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public class BaseSolver implements ISolver {

	private IPathConstraint constraint;

	protected int				resultType;
	protected List<IVariable>	inputData;
	protected IExpression		returnData;

	/**
	 * Create new solver to execute solving constraint
	 * 
	 * @param constraint
	 *            path constraint need to solve
	 */
	protected BaseSolver(IPathConstraint constraint)
	{
		this.constraint = constraint;
	}

	@Override
	public IPathConstraint getConstraint()
	{
		return constraint;
	}

	@Override
	public int getResultType()
	{
		return resultType;
	}

	@Override
	public List<IVariable> getInputData()
	{
		return inputData;
	}

	@Override
	public IExpression getPathReturnData()
	{
		return returnData;
	}

}
