/**
 * Utility method used in project
 * @file SDVUtils.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

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
	public static String gxceptionMsg(Throwable e)
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
	@SuppressWarnings("nls")
	public static String removeCommentFlag(String comment)
	{
		return comment.replaceAll("(?m)(^\\/{2,}[ \\t]?)|(^\\/\\*+)|(^ *\\*(?!\\/) ?)|(\\*+\\/$)", "")
				.replaceAll("(?m)(^[\\n\\x0B\\f\\r]+)", "");
	}

	/**
	 * Reads the contents of a file into a String. The file is always closed.
	 *
	 * @param file
	 *            the file to read
	 * @param encoding
	 *            the encoding to use
	 * @return the file contents
	 * @throws IOException
	 *             in case of an I/O error
	 * @since 2.3
	 */
	@SuppressWarnings("nls")
	public static String readFileToString(File file, @Nullable Charset encoding) throws IOException
	{
		return FileUtils.readFileToString(file, encoding).replace("\r\n", "\n");
	}

}
