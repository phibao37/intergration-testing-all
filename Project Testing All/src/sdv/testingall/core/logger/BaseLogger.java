/**
 * Abstract logger class
 * @file BaseLogger.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.logger;

/**
 * Abstract logger class
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public abstract class BaseLogger implements ILogger {

	private ILogger plug;

	@Override
	public ILogger log(int type, String message, Object... args)
	{
		if (isPlugged()) {
			plug.log(type, message, args);
		}
		return this;
	}

	@Override
	public void plug(ILogger plugger)
	{
		this.plug = plugger;

	}

	@Override
	public ILogger getPlug()
	{
		return plug;
	}

}
