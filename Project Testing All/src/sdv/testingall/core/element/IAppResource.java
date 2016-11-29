/**
 * Interface to manage application resource
 * @file IAppResource.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.element;

/**
 * Interface to manage application resource like international string
 * 
 * @author VuSD
 *
 * @date 2016-11-25 VuSD created
 */
public interface IAppResource {

	/**
	 * Get the translated string based on key
	 * 
	 * @param key
	 *            key identify
	 * @return corresponding string
	 */
	default String resString(String key)
	{
		return key;
	}
}
