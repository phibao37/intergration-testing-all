/**
 * Represent a node that corresponding to a file/folder
 * @file IFileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Represent a node that corresponding to a file/folder
 * 
 * @author VuSD
 *
 * @date 2016-11-16 VuSD created
 */
@NonNullByDefault
public interface IFileNode extends INode {

	/**
	 * Get the corresponding file
	 * 
	 * @return file object
	 */
	File getFile();

	@Override
	default boolean shouldDisplay()
	{
		return true;
	}

}
