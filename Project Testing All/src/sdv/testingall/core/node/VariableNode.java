/**
 * Represent a variable node
 * @file VariableNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import javafx.scene.image.Image;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.type.IType;

/**
 * Represent a variable, consist of a name, a type and extra modifier
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
@NonNullByDefault
public abstract class VariableNode extends BaseNode {

	private final IType				type;
	private final String			name;
	private @Nullable IExpression	value;

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
		return String.format("%s %s", type, name); //$NON-NLS-1$
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

	/**
	 * Get the value of the variable
	 * 
	 * @return the value
	 */
	@Nullable
	public IExpression getValue()
	{
		return value;
	}

	/**
	 * Set the value of the variable
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(IExpression value)
	{
		this.value = value;
	}

	/**
	 * Check whether this variable has set the value
	 * 
	 * @return has value or not
	 */
	public boolean hasValue()
	{
		return value != null;
	}

	@Override
	public @Nullable Image getIcon()
	{
		// This node will not be display in tree
		return null;
	}

	@Override
	public int compareTo(INode o)
	{
		return 0;
	}

	@Override
	public boolean shouldDisplay()
	{
		return false;
	}

	@Override
	public VariableNode clone()
	{
		return (VariableNode) super.clone();
	}

}
