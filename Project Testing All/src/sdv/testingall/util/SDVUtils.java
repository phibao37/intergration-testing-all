/**
 * Utility method used in project
 * @file SDVUtils.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.util;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Utility method used in project
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
@NonNullByDefault
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

	/**
	 * Remove the comment flag, retain only the text
	 * 
	 * @param comment
	 *            string to be format
	 * @return formatted comment
	 */
	public static String removeCommentFlag(String comment)
	{
		return comment.replaceAll("(?m)(^\\/{2,}[ \\t]?)|(^\\/\\*+)|(^ *\\*(?!\\/) ?)|(\\*+\\/$)", "")
				.replaceAll("(?m)(^[\\n\\x0B\\f\\r]+)", "");
	}

}
