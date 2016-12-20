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
import sdv.testingall.core.element.IDisplayable;

/**
 * Base for all node in a project structure
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
@NonNullByDefault
public interface INode extends List<INode>, Cloneable, Comparable<INode>, IDisplayable {

	/**
	 * Get the parent node that this node belongs to. This can be <code>null</code> if this is a root node
	 * 
	 * @return parent node
	 */
	@Nullable
	INode getParent();

	/**
	 * Set the parent node that this node belongs to. This can be <code>null</code> if this is a root node
	 * 
	 * @param parent
	 *            parent node
	 */
	void setParent(@Nullable INode parent);

	/**
	 * Get the image icon to be display in the left of node content
	 * 
	 * @return image icon
	 */
	@Nullable
	Image getIcon();

	/**
	 * Check if this node should be display in project tree
	 * 
	 * @return should display state
	 */
	boolean shouldDisplay();

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

}
