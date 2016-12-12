/**
 * CFG Generation implementation
 * @file CFGGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gencfg;

import sdv.testingall.cdt.node.CppFunctionNode;
import sdv.testingall.core.cfggen.BaseCFGGeneration;
import sdv.testingall.core.statement.ICFG.ICFGType;

/**
 * CFG Generation for C/C++ function
 * 
 * @author VuSD
 *
 * @date 2016-12-08 VuSD created
 */
public class CFGGeneration extends BaseCFGGeneration {

	/**
	 * Generate CFG for given function
	 * 
	 * @param function
	 *            function node contains body to generate CFG
	 * @param option
	 *            parameter to specify CFG
	 */
	public CFGGeneration(CppFunctionNode function, ICFGType option)
	{
		super(function, option);
	}

	@Override
	public CppFunctionNode getFunction()
	{
		return (CppFunctionNode) super.getFunction();
	}

	@Override
	protected void buildCFG()
	{

	}

}
