/**
 * Log into a string builder
 * @file StringLogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.logger;

/**
 * Log into a string builder
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public class StringLogger extends BaseLogger {

	private final StringBuilder build;

	/**
	 * Create new logger that log into a string
	 */
	public StringLogger()
	{
		build = new StringBuilder();
	}

	@SuppressWarnings("nls")
	@Override
	public ILogger log(int type, String message, Object... args)
	{

		build.append(type == ERROR ? "[ERROR]: " : "[INFO]: ");
		build.append(String.format(message, args));
		return super.log(type, message, args);
	}

	/**
	 * Get the logged string
	 * 
	 * @return logged string
	 */
	@Override
	public String toString()
	{
		return build.toString();
	}

}
