/**
 * Represent a function node
 * @file FunctionNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import javafx.scene.image.Image;
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

	private static final Image ICON = new Image(BaseNode.class.getResourceAsStream("/node/function.png"));

	private final IType				type;
	private final String			name;
	private final VariableNode[]	params;

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
		// String b = String.format("%s %s(", type, name);
		StringBuilder b = new StringBuilder();

		b.append(type).append(' ').append(name).append('(');
		if (params.length > 0) {
			b.append(params[0]);
			for (int i = 1; i < params.length; i++) {
				b.append(", ").append(params[i]);
			}
		}

		return b.append(')').toString();
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
	public @Nullable Image getIcon()
	{
		return ICON;
	}

	@Override
	public int compareTo(INode o)
	{
		return 0;
	}

}
