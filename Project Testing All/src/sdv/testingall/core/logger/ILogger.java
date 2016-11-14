/**
 * Interface for all logger event
 * @file ILogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.logger;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Interface for all logger event used in "Project Testing All" application
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
@NonNullByDefault
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
	ILogger log(int type, String message, @Nullable Object... args);

	/**
	 * Plug another logger so that when this logger is called, the plugged logger also be called
	 * 
	 * @param plugger
	 *            logger to plug
	 */
	void plug(@Nullable ILogger plugger);

	/**
	 * Get the plugged logger
	 * 
	 * @return logger
	 */
	@Nullable
	ILogger getPlug();

	/**
	 * Test whether a logger is plugged
	 * 
	 * @return plugged state
	 */
	default boolean isPlugged()
	{
		return getPlug() != null;
	}

	/**
	 * Release current plugged logger
	 * 
	 * @return removed logger
	 */
	default @Nullable ILogger unplug()
	{
		ILogger last = getPlug();
		plug(null);
		return last;
	}

	/** Indicate the log is an error */
	int	ERROR	= 0;
	/** Indicate the log is an information */
	int	INFO	= 1;

	/** The last type in this interface, implement may declare its own */
	int _TYPE_LAST = INFO;
}
