/**
 * Base for all node
 * @file INode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.util.ArrayList;

import javax.swing.Icon;

/**
 * Base for all node in a project structure
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
public abstract class INode extends ArrayList<INode> implements Cloneable, Comparable<INode> {

	private String content;

	/**
	 * Create new node
	 * 
	 * @param content
	 *            node content
	 */
	public INode(String content)
	{
		setContent(content);
	}

	/**
	 * Set the node content to be display in the project tree
	 * 
	 * @param content
	 *            node content
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * Return the node content
	 * 
	 * @return the content of the node to be display in the project tree
	 */
	@Override
	public String toString()
	{
		return content;
	}

	/**
	 * Get the image icon to be display in the left of node content
	 * 
	 * @return image icon
	 */
	public abstract Icon getIcon();

	/**
	 * Get the copy of the node
	 */
	@Override
	public INode clone()
	{
		return (INode) super.clone();
	}

	/**
	 * Compare with other node for sorting in tree display
	 */
	@Override
	public abstract int compareTo(INode o);
}
