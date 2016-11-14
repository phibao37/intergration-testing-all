/**
 * Represent a function node
 * @file FunctionNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.type.IType;

/**
 * Represent a function node
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
@NonNullByDefault
public abstract class FunctionNode extends BaseNode {

	private static final ImageIcon ICON = new ImageIcon(ImageIcon.class.getResource("/node/function.png"));

	private IType			type;
	private String			name;
	private VariableNode[]	params;

	/**
	 * Create new function node
	 * 
	 * @param type
	 *            return type
	 * @param name
	 *            function name
	 * @param params
	 *            list of function parameter
	 */
	public FunctionNode(IType type, String name, VariableNode[] params)
	{
		this.type = type;
		this.name = name;
		this.params = params;
		setContent(generateContent(type, name, params));
	}

	/**
	 * Generate content of the function to be display
	 * 
	 * @param type
	 *            the return type of the function
	 * @param name
	 *            the function name
	 * @param params
	 *            the function parameter
	 * @return the generated content
	 */
	protected String generateContent(IType type, String name, VariableNode[] params)
	{
		String b = String.format("%s %s(", type, name);

		if (params.length > 0) {
			b += params[0];
			for (int i = 1; i < params.length; i++) {
				b += ", " + params[i];
			}
		}

		return b + ")";
	}

	/**
	 * Get the return type of the function
	 * 
	 * @return return type
	 */
	public IType getType()
	{
		return type;
	}

	/**
	 * Get the function name
	 * 
	 * @return function name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the function parameter
	 * 
	 * @return function parameter
	 */
	public VariableNode[] getParameter()
	{
		return params;
	}

	@Override
	@Nullable
	public Icon getIcon()
	{
		return ICON;
	}

	@Override
	public int compareTo(INode o)
	{
		return 0;
	}

}
