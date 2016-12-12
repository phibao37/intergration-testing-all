/**
 * Abstract class to generate CFG
 * @file BaseCFGGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.cfggen;

import java.util.ArrayList;
import java.util.List;

import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.statement.CFG;
import sdv.testingall.core.statement.ICFG;
import sdv.testingall.core.statement.ICFG.ICFGType;
import sdv.testingall.core.statement.IConditionStatement;
import sdv.testingall.core.statement.IFlagStatement;
import sdv.testingall.core.statement.INormalStatement;
import sdv.testingall.core.statement.IStatement;

/**
 * Abstract class to generate CFG
 * 
 * @author VuSD
 *
 * @date 2016-12-12 VuSD created
 */
public abstract class BaseCFGGeneration implements ICFGGeneration {

	private FunctionNode	function;
	private ICFGType		option;
	private ICFG			cfg;

	protected IFlagStatement	BEGIN;
	protected IFlagStatement	END;

	/**
	 * Generate CFG for given function
	 * 
	 * @param function
	 *            function node contains body to generate CFG
	 * @param option
	 *            parameter to specify CFG
	 */
	protected BaseCFGGeneration(FunctionNode function, ICFGType option)
	{
		this.function = function;
		this.option = option;
		this.cfg = generateCFG();
	}

	/**
	 * Generate CFG
	 * 
	 * @return generated CFG
	 */
	protected ICFG generateCFG()
	{
		BEGIN = IFlagStatement.newBegin();
		END = IFlagStatement.newEnd();
		buildCFG();

		ArrayList<IStatement> stmList = new ArrayList<>();
		linkStatement(BEGIN, stmList);
		return new CFG(stmList);
	}

	/**
	 * Main entry to build CFG graph
	 */
	protected abstract void buildCFG();

	/**
	 * Link all statement together, include removing temporary statement and inserting to list
	 * 
	 * @param root
	 *            beginning statement
	 * @param buildListStm
	 *            list to get all statement, should be empty
	 */
	protected static void linkStatement(IStatement root, List<IStatement> buildListStm)
	{
		if (root == null || root.isVisited()) {
			return;
		}
		root.setVisit(true);
		buildListStm.add(root);

		if (root.isCondition()) {
			IConditionStatement conStm = (IConditionStatement) root;
			IStatement onTrue = skipTemporary(conStm.trueBranch());
			IStatement onFalse = skipTemporary(conStm.falseBranch());

			conStm.setBranch(onTrue, onFalse);
			linkStatement(onTrue, buildListStm);
			linkStatement(onFalse, buildListStm);
		}

		else {
			INormalStatement normalStm = (INormalStatement) root;
			IStatement next = skipTemporary(normalStm.nextStatement());

			normalStm.setNextStatement(next);
			linkStatement(next, buildListStm);
		}
	}

	/**
	 * Skip all temporary statement and get final "real" statement
	 * 
	 * @param stm
	 *            first statement to check
	 * @return last statement that is not temporary one
	 */
	protected static IStatement skipTemporary(IStatement stm)
	{
		while (stm instanceof TemporaryStatement) {
			stm = ((TemporaryStatement) stm).nextStatement();
		}
		return stm;
	}

	@Override
	public ICFG getCFG()
	{
		return cfg;
	}

	@Override
	public FunctionNode getFunction()
	{
		return function;
	}

	@Override
	public ICFGType getOption()
	{
		return option;
	}

}
