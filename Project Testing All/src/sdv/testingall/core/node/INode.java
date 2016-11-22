/**
 * Base for all node
 * @file INode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import javafx.scene.image.Image;

/**
 * Base for all node in a project structure
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
@NonNullByDefault
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
	@Nullable
	Image getIcon();

	/**
	 * Get the copy of the node
	 * 
	 * @return the cloned node
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
		System.out.printf("%s%s [%s]%n", margin, this, getClass().getSimpleName());
		this.forEach((child) -> {
			child.printTree(margin + "   ");
		});
	}
}
