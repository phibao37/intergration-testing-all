/**
 * Represent all element that can be display via its content
 * @file IDisplayable.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.element;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Represent all element that can be display via its content
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public interface IDisplayable {

	/**
	 * Set the content to be display
	 * 
	 * @param content
	 *            string content
	 */
	void setContent(@NonNull String content);

	/**
	 * Return the content
	 * 
	 * @return the content of the element to be display
	 */
	@NonNull
	@Override
	String toString();
}
