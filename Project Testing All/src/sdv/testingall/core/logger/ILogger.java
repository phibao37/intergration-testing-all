/**
 * Interface for all logger event
 * @file ILogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.logger;

/**
 * Interface for all logger event used in "Project Testing All" application
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public interface ILogger {

	/**
	 * Log an message for an event
	 * 
	 * @param type
	 *            log type
	 * @param message
	 *            string (formatted) to log
	 * @param args
	 *            optional argument in formatted message
	 * @return current logger
	 */
	ILogger log(int type, String message, Object... args);

	/** Indicate the log is an error */
	int	ERROR	= 0;
	/** Indicate the log is an information */
	int	INFO	= 1;

	/** The last type in this interface, implement may declare its own */
	int _TYPE_LAST = INFO;
}
