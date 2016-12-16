/**
 * Configuration during generation of test data
 * @file IGenTestConfig.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata;

import java.util.List;

import sdv.testingall.core.element.IAppResource;
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
}