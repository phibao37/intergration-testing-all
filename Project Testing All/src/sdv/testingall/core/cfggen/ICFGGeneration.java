/**
 * Interface for generating CFG
 * @file ICFGGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.cfggen;

import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.statement.ICFG;
import sdv.testingall.core.statement.ICFG.ICFGType;

/**
 * Interface for generating CFG
 * 
 * @author VuSD
 *
 * @date 2016-12-12 VuSD created
 */
public interface ICFGGeneration {

	/**
	 * Get the function to generate CFG
	 * 
	 * @return function node
	 */
	FunctionNode getFunction();

	/**
	 * Get the parameter to generate CFG
	 * 
	 * @return CFG generating parameter
	 */
	ICFGType getOption();

	/**
	 * Get the generated CFG
	 * 
	 * @return CFG graph
	 */
	ICFG getCFG();
}
