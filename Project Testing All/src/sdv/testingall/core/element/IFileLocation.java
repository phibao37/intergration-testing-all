/**
 * Represent a element that is inside a file
 * @file IFileLocation.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.element;

import java.io.File;

/**
 * Represent a element that is inside a file
 * 
 * @author VuSD
 *
 * @date 2016-11-29 VuSD created
 */
public interface IFileLocation {

	/**
	 * Get the source file that contains content of this node
	 * 
	 * @return source file
	 */
	File getFile();

	/**
	 * Get the starting position of element inside the file
	 * 
	 * @return offset position
	 */
	int fileOffset();

	/**
	 * Get the number of character in the content of this element
	 * 
	 * @return element content length
	 */
	int fileLength();

	/**
	 * Get the ending position of element inside the file
	 * 
	 * @return ending offset
	 */
	default int fileEndOffset()
	{
		return fileOffset() + fileLength();
	}
}
