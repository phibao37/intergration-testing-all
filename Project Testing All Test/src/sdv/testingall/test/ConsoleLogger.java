/**
 * Log into standard output stream
 * @file ConsoleLogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test;

import sdv.testingall.core.logger.ILogger;

/**
 * Log into standard output stream
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class ConsoleLogger implements ILogger {

	@Override
	public ILogger log(int type, String message, Object... args)
	{
		String formatted = String.format(message, args);
		System.out.printf("[%s]: %s", type == ERROR ? "ERROR" : "INFO", formatted);
		return this;
	}

}
