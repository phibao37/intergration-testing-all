/**
 * Implementation for path constraint
 * @file PathConstraint.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver;

import java.util.List;

import sdv.testingall.core.expression.IExpression;

/**
 * Implementation for path constraint
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public class PathConstraint implements IPathConstraint {

	private List<IExpression>	constraint;
	private IExpression			returnValue;

	/**
	 * Create new path constraint
	 */
	public PathConstraint()
	{
	}

	@Override
	public List<IExpression> getConstraint()
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

}
