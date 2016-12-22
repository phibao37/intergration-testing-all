/**
 * Implementation for path constraint
 * @file PathConstraint.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver;

import java.util.ArrayList;
import java.util.List;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.gentestdata.symbolicexec.IVariable;

/**
 * Implementation for path constraint
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public class PathConstraint implements IPathConstraint {

	private List<IVariable>		inputs;
	private List<IExpression>	constraint;
	private IExpression			returnValue;

	/**
	 * Create new path constraint
	 */
	public PathConstraint()
	{
		inputs = new ArrayList<>();
		constraint = new ArrayList<>();
	}

	@Override
	public List<IExpression> getConstraints()
	{
		return constraint;
	}

	/**
	 * Set the path return value
	 * 
	 * @param returnValue
	 *            return value as expression
	 */
	public void setPathReturnValue(IExpression returnValue)
	{
		this.returnValue = returnValue;
	}

	@Override
	public IExpression getPathReturnValue()
	{
		return returnValue;
	}

	@Override
	public List<IVariable> getInputs()
	{
		return inputs;
	}

}
