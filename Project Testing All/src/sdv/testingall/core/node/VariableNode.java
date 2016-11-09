/**
 * Represent a variable node
 * @file VariableNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import javax.swing.Icon;

import sdv.testingall.core.type.IType;

/**
 * Represent a variable, consist of a name, a type and extra modifier
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
public abstract class VariableNode extends BaseNode {

	private IType	type;
	private String	name;

	/**
	 * Create new variable node from type and name
	 * 
	 * @param type
	 *            the type of the variable
	 * @param name
	 *            the name of the variable
	 */
	public VariableNode(IType type, String name)
	{
		super(null);
		this.type = type;
		this.name = name;
		setContent(generateContent(type, name));
	}

	/**
	 * Generate content of the variable to be display
	 * 
	 * @param type
	 *            the type of the variable
	 * @param name
	 *            the name of the variable
	 * @return the generated content
	 */
	protected String generateContent(IType type, String name)
	{
		return String.format("%s %s", type, name);
	}

	/**
	 * Get type type of the variable
	 * 
	 * @return the type
	 */
	public IType getType()
	{
		return type;
	}

	/**
	 * Get type name of the variable
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public Icon getIcon()
	{
		// This node will not be display in tree
		return null;
	}

	@Override
	public int compareTo(INode o)
	{
		return 0;
	}

}
