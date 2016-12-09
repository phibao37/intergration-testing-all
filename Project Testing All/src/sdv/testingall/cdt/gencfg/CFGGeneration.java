/**
 * CFG Generation implementation
 * @file CFGGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gencfg;

import sdv.testingall.cdt.node.CppFunctionNode;
import sdv.testingall.core.statement.ICFG;
import sdv.testingall.core.statement.ICFG.ICFGType;
import sdv.testingall.core.statement.IFlagStatement;

/**
 * CFG Generation for C/C++ function
 * 
 * @author VuSD
 *
 * @date 2016-12-08 VuSD created
 */
public class CFGGeneration {

	private CppFunctionNode	function;
	private ICFGType		option;
	private ICFG			cfg;

	private IFlagStatement	BEGIN;
	private IFlagStatement	END;

	public CFGGeneration(CppFunctionNode function, ICFGType option)
	{
		this.function = function;
		this.option = option;
		BEGIN = IFlagStatement.newBegin();
		END = IFlagStatement.newEnd();
	}

	public ICFG getCFG()
	{
		return cfg;
	}
}
