/**
 * Implement for symbolic variable
 * @file Variable.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.symbolicexec;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.type.IType;

/**
 * Implement for symbolic variable
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class Variable implements IVariable {

	private String		name;
	private IType		type;
	private int			scope;
	private IExpression	value;

	/**
	 * Create new symbolic variable
	 * 
	 * @param name
	 *            variable name
	 * @param type
	 *            variable type
	 * @param scope
	 *            variable scope
	 */
	public Variable(String name, IType type, int scope)
	{
		this.name = name;
		this.type = type;
		this.scope = scope;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public IType getType()
	{
		return type;
	}

	@Override
	public IExpression getValue()
	{
		return value;
	}

	@Override
	public void setValue(IExpression value)
	{
		this.value = value;
	}

	@Override
	public int getScope()
	{
		return scope;
	}

	@Override
	public void setScope(int scope)
	{
		this.scope = scope;
	}

}
