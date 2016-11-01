/**
 * Log into a string builder
 * @file StringLogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test;

import sdv.testingall.core.logger.BaseLogger;
import sdv.testingall.core.logger.ILogger;

/**
 * Log into a string builder
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public class StringLogger extends BaseLogger {

	private StringBuilder build;

	/**
	 * Create new logger that log into a string
	 */
	public StringLogger()
	{
		build = new StringBuilder();
	}

	@Override
	public ILogger log(int type, String message, Object... args)
	{

		build.append(type == ERROR ? "[ERROR]: " : "[INFO]: ");
		build.append(String.format(message, args));
		return super.log(type, message, args);
	}

	/**
	 * Get the logged string
	 */
	@Override
	public String toString()
	{
		return build.toString();
	}

}
