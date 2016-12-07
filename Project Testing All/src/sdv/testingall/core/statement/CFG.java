/**
 * CFG implementation
 * @file CFG.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import java.util.List;

/**
 * Represent a control flow graph
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public class CFG implements ICFG {

	private IStatement[]	statements;
	private List<ITestPath>	basisPaths;

	/**
	 * Create new CFG object
	 * 
	 * @param cfgTree
	 *            list of statement linked as a tree
	 */
	public CFG(List<IStatement> cfgTree)
	{
		statements = new IStatement[cfgTree.size()];
		cfgTree.toArray(statements);
	}

	@Override
	public IStatement[] getStatements()
	{
		return statements;
	}

	@Override
	public List<ITestPath> getAllBasisPath()
	{
		if (basisPaths == null) {
			// TODO Auto-generated method stub
		}
		return basisPaths;
	}

	@Override
	public int computeStatementCoverage(List<ITestPath> listTestPath)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeBranchCoverage(List<ITestPath> listTestPath)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
