/**
 * Base for all node
 * @file INode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.util.List;

import javax.swing.Icon;

import com.sun.istack.internal.Nullable;

/**
 * Base for all node in a project structure
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
public interface INode extends List<INode>, Cloneable, Comparable<INode> {

	/**
	 * Set the node content to be display in the project tree
	 * 
	 * @param content
	 *            node content
	 */
	void setContent(String content);

	/**
	 * Return the node content
	 * 
	 * @return the content of the node to be display in the project tree
	 */
	@Override
	String toString();

	/**
	 * Get the image icon to be display in the left of node content
	 * 
	 * @return image icon
	 */
	Icon getIcon();

	/**
	 * Get the copy of the node
	 */
	INode clone();

	/**
	 * Compare with other node for sorting in tree display
	 */
	@Override
	int compareTo(INode o);

	/**
	 * Set the description details for this node
	 * 
	 * @param des
	 *            description about node
	 */
	void setDescription(String des);

	/**
	 * Get the description details for this node
	 * 
	 * @return description about node
	 */
	@Nullable
	String getDescription();

	/**
	 * Print the node structure
	 * 
	 * @param margin
	 *            left-alignment to print
	 */
	default void printTree(String margin)
	{
		System.out.printf("%s%s [%s]\n", margin, this, getClass().getSimpleName());
		for (INode child : this) {
			child.printTree(margin + "   ");
		}
	}
}
