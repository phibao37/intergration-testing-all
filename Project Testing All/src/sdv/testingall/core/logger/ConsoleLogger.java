/**
 * Log into standard output stream
 * @file ConsoleLogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.logger;

import java.io.PrintStream;

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
		@SuppressWarnings("resource")
		PrintStream ss = type == ERROR ? System.err : System.out;

		ss.print(type == ERROR ? "[ERROR]: " : "[INFO]: ");
		ss.printf(message, args);
		ss.println();
		return super.log(type, message, args);
	}

}
