/**
 * Log into standard output stream
 * @file ConsoleLogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test;

import sdv.testingall.core.logger.BaseLogger;
import sdv.testingall.core.logger.ILogger;

/**
 * Log into standard output stream
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class ConsoleLogger extends BaseLogger {

	@Override
	public ILogger log(int type, String message, Object... args)
	{
		System.out.print(type == ERROR ? "[ERROR]: " : "[INFO]: ");
		System.out.printf(message, args);
		return super.log(type, message, args);
	}

}
