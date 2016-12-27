/**
 * Configuration during generation of test data
 * @file IGenTestConfig.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import java.util.List;

import sdv.testingall.core.element.IAppResource;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.testreport.Coverage;

/**
 * Configuration during generation of test data
 * 
 * @author VuSD
 *
 * @date 2016-12-14 VuSD created
 */
public interface IGenTestConfig extends IAppResource {

	/**
	 * List of coverage scenario to generate test data
	 * 
	 * @return coverage list
	 */
	List<Coverage> genCoverage();

	/**
	 * Get the logger
	 * 
	 * @return the logger to log event
	 */
	ILogger getLogger();

	/**
	 * Should use Z3 solver to solve constraint
	 * 
	 * @return use Z3 solver or not
	 */
	boolean enableZ3Solver();

	/**
	 * Get maximum timeout for Z3 solver
	 * 
	 * @return timeout in milliseconds (>= 0)
	 */
	int getZ3SolveTimeout();
}
