/**
 * CFG implementation
 * @file CFG.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javafx.util.Pair;

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
			scanBasisPath(new TestPath(), basisPaths = new ArrayList<>(), statements[0]);
		}
		return basisPaths;
	}

	/**
	 * Scan all basis path and push to result list
	 * 
	 * @param scanner
	 *            a test path act as a scanner, should be an empty + temporary path
	 * @param result
	 *            list of test path to put result into
	 * @param current
	 *            current statement to analyze, should be the beginning of CFG
	 */
	protected void scanBasisPath(ITestPath scanner, List<ITestPath> result, IStatement current)
	{
		// Scan to end of CFG, add the test path to result
		if (current == null) {
			result.add(scanner.clone());
		}

		// Check if this statement is valid to add to scanner
		else if (checkValidNextStatement(scanner, current)) {
			scanner.add(current);

			if (current.isCondition()) {
				IConditionStatement condStm = (IConditionStatement) current;
				scanBasisPath(scanner, result, condStm.falseBranch());
				scanBasisPath(scanner, result, condStm.trueBranch());
			} else {
				scanBasisPath(scanner, result, ((INormalStatement) current).nextStatement());
			}

			scanner.remove(scanner.size() - 1);
		}
	}

	/**
	 * Check if the statement occur in the test path not more than once time
	 * 
	 * @param path
	 *            test path to check
	 * @param stm
	 *            statement to check
	 * @return valid statement to add to path
	 */
	protected static boolean checkValidNextStatement(ITestPath path, IStatement stm)
	{
		int count = 0;
		for (IStatement statement : path) {
			if (statement == stm) {
				count++;
			}
		}
		return count <= 1;
	}

	@Override
	public int computeStatementCoverage(List<ITestPath> listTestPath)
	{
		// Reset visit state
		int countStatement = 0;
		for (IStatement stm : statements) {
			if (stm.sholdDisplayInTestPath()) {
				stm.setVisit(false);
				countStatement++;
			}
		}
		if (countStatement == 0) {
			return 0;
		}

		// Visit statement in list test path
		for (ITestPath testpath : listTestPath) {
			for (IStatement stm : testpath) {
				stm.setVisit(true);
			}
		}

		// Count number of visited
		int count = 0;
		for (IStatement stm : statements) {
			if (stm.isVisited() && stm.sholdDisplayInTestPath()) {
				count++;
			}
		}

		return count * 100 / countStatement;
	}

	@Override
	public int computeBranchCoverage(List<ITestPath> listTestPath)
	{
		// Count number of branch
		int countBranch = 0;
		for (IStatement stm : statements) {
			if (stm.isCondition()) {
				countBranch += 2;
			}
		}
		if (countBranch == 0) {
			return computeStatementCoverage(listTestPath);
		}

		// Add set of visited edge
		HashSet<Pair<IStatement, IStatement>> setEdge = new HashSet<>();
		for (ITestPath testpath : listTestPath) {
			for (int i = 0; i < testpath.size(); i++) {
				if (testpath.get(i).isCondition()) {
					setEdge.add(new Pair<>(testpath.get(i), testpath.get(i + 1)));
				}
			}
		}

		return setEdge.size() * 100 / countBranch;
	}

	/** Configuration set to obtain normal CFG */
	public static final ICFGType TYPE_NORMAL = new CFGType(false);

	/** Configuration set to obtain CFG with complex condition expanded */
	public static final ICFGType TYPE_SUBCONDITION = new CFGType(true);

	/**
	 * Implementation for ICFGType
	 */
	static class CFGType implements ICFGType {

		boolean expandSubCondition;

		/**
		 * Create new CFG parameter set
		 * 
		 * @param expandSubCondition
		 *            should complex condition be expanding
		 */
		CFGType(boolean expandSubCondition)
		{
			this.expandSubCondition = expandSubCondition;
		}

		@Override
		public boolean isExpandSubCondition()
		{
			return expandSubCondition;
		}

	}

}
