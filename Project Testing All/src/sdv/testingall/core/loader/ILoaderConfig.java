/**
 * Configuration during loader task
 * @file ILoaderConfig.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.loader;

import java.nio.charset.Charset;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.element.IAppResource;
import sdv.testingall.core.logger.ILogger;

/**
 * Configuration during loader task
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
@NonNullByDefault
public interface ILoaderConfig extends IAppResource {

	/**
	 * Get the encoding to read the source file
	 * 
	 * @return encoding to read source code or <code>null</code> to use system default
	 */
	@Nullable
	default Charset getFileCharset()
	{
		return null;
	}

	/**
	 * Get the logger
	 * 
	 * @return the logger to log event
	 */
	ILogger getLogger();

}
