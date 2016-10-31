/**
 * Utility method used in project
 * @file SDVUtils.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.util;

/**
 * Utility method used in project
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class SDVUtils {

	/**
	 * Get the exception details message or exception class name if no message provided
	 * 
	 * @param e
	 *            exception object
	 * @return exception message
	 */
	public static String gxceptionMsg(Exception e)
	{
		String msg = e.getMessage();
		return msg == null || msg.isEmpty() ? e.getClass().getSimpleName() : msg;
	}
}
