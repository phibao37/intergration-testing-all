/**
 * Represent a node that is inside a source file
 * @file IInsideFileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import sdv.testingall.core.element.IFileLocation;

/**
 * Represent a node that is inside a source file
 * 
 * @author VuSD
 *
 * @date 2016-11-29 VuSD created
 */
public interface IInsideFileNode extends INode, IFileLocation {

	/**
	 * Set the location of this node inside a file
	 * 
	 * @param offset
	 *            the starting position of node inside the file
	 * @param length
	 *            the number of character in the content of this node
	 */
	void setFileLocation(int offset, int length);

	/**
	 * Get the source file node that this node is belongs to
	 * 
	 * @return source file node
	 */
	default IFileNode getFileNode()
	{
		INode parent = getParent();
		while (!(parent instanceof IFileNode)) {
			assert parent != null;
			parent = parent.getParent();
		}
		return (IFileNode) parent;
	}

	@Override
	default File getFile()
	{
		return getFileNode().getFile();
	}
}
