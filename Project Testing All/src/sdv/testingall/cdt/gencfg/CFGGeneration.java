/**
 * CFG Generation implementation
 * @file CFGGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gencfg;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;

import sdv.testingall.cdt.expression.CppConverter;
import sdv.testingall.cdt.node.CppFunctionNode;
import sdv.testingall.core.gencfg.BaseCFGGeneration;
import sdv.testingall.core.gencfg.TemporaryStatement;
import sdv.testingall.core.statement.ConditionStatement;
import sdv.testingall.core.statement.ICFG.ICFGType;
import sdv.testingall.core.statement.IConditionStatement;
import sdv.testingall.core.statement.INormalStatement;
import sdv.testingall.core.statement.IScopeStatement;
import sdv.testingall.core.statement.IStatement;
import sdv.testingall.core.statement.NormalStatement;

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
		visitStatement(getFunction().getBody(), BEGIN, END, null, null, END);
	}

	/**
	 * Base control for all type of statement
	 * 
	 * @param astStm
	 *            AST statement node
	 * @param begin
	 *            normal statement to set next execution to first statement discovered from AST
	 * @param end
	 *            last statement has been discovered will set next execution to
	 * @param break_
	 *            <break> statement will set next execution to
	 * @param continue_
	 *            <continue> statement will set next execution to
	 * @param throw_
	 *            <throw> statement will set next execution to
	 */
	void visitStatement(IASTStatement astStm, INormalStatement begin, IStatement end, IStatement break_,
			IStatement continue_, IStatement throw_)
	{
		/********************* VISIT "{...}" STATEMENT ********************/
		if (astStm instanceof IASTCompoundStatement) {
			visitBlock((IASTCompoundStatement) astStm, begin, end, break_, continue_, throw_);
		}

		/********************* VISIT "if (...) else" STATEMENT ********************/
		else if (astStm instanceof IASTIfStatement) {
			IASTIfStatement astIf = (IASTIfStatement) astStm;
			IASTExpression astCond = astIf.getConditionExpression();
			IASTStatement astThen = astIf.getThenClause();
			IASTStatement astElse = astIf.getElseClause();
			INormalStatement midTrue = new TemporaryStatement();
			INormalStatement midFalse = new TemporaryStatement();

			visitCondition(astCond, begin, midTrue, midFalse);
			visitStatement(astThen, midTrue, end, break_, continue_, throw_);
			visitStatement(astElse, midFalse, end, break_, continue_, throw_);
		}

		/********************* VISIT "for (...) {}" STATEMENT ********************/
		else if (astStm instanceof IASTForStatement) {
			IASTForStatement astFor = (IASTForStatement) astStm;
			IASTStatement astInit = astFor.getInitializerStatement();
			IASTExpression astCond = astFor.getConditionExpression();
			IASTExpression astIter = astFor.getIterationExpression();
			IASTStatement astBody = astFor.getBody();

			// Create new virtual scope {} because "for" statement can declare its own variable
			IScopeStatement scopeIn = IScopeStatement.newScopeStatement(true);
			IScopeStatement scopeOut = IScopeStatement.newScopeStatement(false);
			begin.setNextStatement(scopeIn);
			scopeOut.setNextStatement(end);

			// Intermediate temporary statement
			INormalStatement beforeCond = new TemporaryStatement();
			INormalStatement beforeBody = new TemporaryStatement();
			INormalStatement afterBody = new TemporaryStatement();

			// Link initialize statement
			visitStatement(astInit, scopeIn, beforeCond, break_, continue_, throw_);

			// Link condition expression
			if (isNullStatement(astCond)) {
				beforeCond.setNextStatement(beforeBody);
			} else {
				visitCondition(astCond, beforeCond, beforeBody, scopeOut);
			}

			// Link body
			visitStatement(astBody, beforeBody, afterBody, scopeOut, afterBody, throw_);

			// Link iteration expression
			if (isNullStatement(astIter)) {
				afterBody.setNextStatement(beforeCond);
			} else {
				INormalStatement iterStm = new SimpleCppStatement(astIter);
				afterBody.setNextStatement(iterStm);
				iterStm.setNextStatement(beforeCond);
			}
		}

		/********************* VISIT "while (...) {}" STATEMENT ********************/
		else if (astStm instanceof IASTWhileStatement) {
			IASTWhileStatement astWhile = (IASTWhileStatement) astStm;
			INormalStatement beforeCond = new TemporaryStatement();
			INormalStatement afterCond = new TemporaryStatement();

			begin.setNextStatement(beforeCond);
			visitCondition(astWhile.getCondition(), beforeCond, afterCond, end);
			visitStatement(astWhile.getBody(), afterCond, beforeCond, end, beforeCond, throw_);
		}

		/********************* VISIT "do {} while (...)" STATEMENT ********************/
		else if (astStm instanceof IASTDoStatement) {
			IASTDoStatement astDo = (IASTDoStatement) astStm;
			INormalStatement beforeBody = new TemporaryStatement();
			INormalStatement beforeCond = new TemporaryStatement();

			begin.setNextStatement(beforeBody);
			visitStatement(astDo.getBody(), beforeBody, beforeCond, end, beforeCond, throw_);
			visitCondition(astDo.getCondition(), beforeCond, beforeBody, end);
		}

		/********************* VISIT "switch (...) {}" STATEMENT ********************/
		else if (astStm instanceof IASTSwitchStatement) {
			// TODO Handle for switch
			begin.setNextStatement(end);
		}

		/********************* VISIT "break" STATEMENT ********************/
		else if (astStm instanceof IASTBreakStatement) {
			begin.setNextStatement(break_);
		}

		/********************* VISIT "continue" STATEMENT ********************/
		else if (astStm instanceof IASTContinueStatement) {
			begin.setNextStatement(continue_);
		}

		/********************* VISIT "try {} catch (...)" STATEMENT ********************/
		else if (astStm instanceof ICPPASTTryBlockStatement) {
			// TODO Handle for try..catch
			begin.setNextStatement(end);
		}

		/********************* VISIT LABEL STATEMENT ********************/
		else if (astStm instanceof IASTLabelStatement) {
			// TODO Handle for label
			begin.setNextStatement(end);
		}

		/********************* VISIT "goto" STATEMENT ********************/
		else if (astStm instanceof IASTGotoStatement) {
			// TODO Handle for goto
			begin.setNextStatement(end);
		}

		/********************* VISIT ";" STATEMENT ********************/
		else if (isNullStatement(astStm)) {
			begin.setNextStatement(end);
		}

		/********************* VISIT NORMAL STATEMENT ********************/
		else {
			visitSimpleStatement(astStm, begin, end, throw_);
		}
	}

	/**
	 * Visiting "{...}" block of statements
	 */
	void visitBlock(IASTCompoundStatement astBlock, INormalStatement begin, IStatement end, IStatement break_,
			IStatement continue_, IStatement throw_)
	{
		IASTStatement[] childs = astBlock.getStatements();

		if (childs.length == 0) {
			begin.setNextStatement(end);
			return;
		}

		IScopeStatement scopeIn = IScopeStatement.newScopeStatement(true);
		IScopeStatement scopeOut = IScopeStatement.newScopeStatement(false);
		INormalStatement[] midPoints = new INormalStatement[childs.length + 1];

		begin.setNextStatement(scopeIn);
		scopeOut.setNextStatement(end);
		midPoints[0] = scopeIn;
		midPoints[childs.length] = scopeOut;

		for (int i = 1; i < childs.length; i++) {
			midPoints[i] = new TemporaryStatement();
		}
		for (int i = 0; i < childs.length; i++) {
			visitStatement(childs[i], midPoints[i], midPoints[i + 1], break_, continue_, throw_);
		}
	}

	/**
	 * Visiting a condition
	 */
	void visitCondition(IASTExpression cond, INormalStatement begin, IStatement endTrue, IStatement endFalse)
	{
		cond = normalize(cond);
		boolean isVisited = false;

		// Parse with sub-condition
		if (getOption().isExpandSubCondition()) {

			// Check for binary &&, ||
			if (cond instanceof IASTBinaryExpression) {
				IASTBinaryExpression astBin = (IASTBinaryExpression) cond;
				IASTExpression op1 = astBin.getOperand1();
				int op = astBin.getOperator();
				IASTExpression op2 = astBin.getOperand2();

				if (op == IASTBinaryExpression.op_logicalAnd) {
					INormalStatement midTrue = new TemporaryStatement();
					INormalStatement midFalse = new TemporaryStatement();

					// If OP1 is FALSE, all (OP1 && OP2) will be FALSE
					midFalse.setNextStatement(endFalse);
					visitCondition(op1, begin, midTrue, midFalse);
					visitCondition(op2, midTrue, endTrue, endFalse);
					isVisited = true;
				} else if (op == IASTBinaryExpression.op_logicalOr) {
					INormalStatement midTrue = new TemporaryStatement();
					INormalStatement midFalse = new TemporaryStatement();

					// If OP1 is TRUE, all (OP1 || OP2) will be TRUE
					midTrue.setNextStatement(endTrue);
					visitCondition(op1, begin, midTrue, midFalse);
					visitCondition(op2, midFalse, endTrue, endFalse);
					isVisited = true;
				}
			}

			// Check for unary !
			else if (cond instanceof IASTUnaryExpression) {
				IASTUnaryExpression astUnary = (IASTUnaryExpression) cond;
				int op = astUnary.getOperator();
				IASTExpression opr = astUnary.getOperand();

				if (op == IASTUnaryExpression.op_not) {
					// Reverse TRUE and FALSE
					visitCondition(opr, begin, endFalse, endTrue);
					isVisited = true;
				}
			}
		}

		if (!isVisited) {
			visitSimpleCondition(cond, begin, endTrue, endFalse);
		}
	}

	/**
	 * Visiting a single condition and create node
	 */
	void visitSimpleCondition(IASTExpression cond, INormalStatement begin, IStatement endTrue, IStatement endFalse)
	{
		cond = normalize(cond);
		IConditionStatement condStm = new ConditionStatement(CppConverter.convert(cond));

		begin.setNextStatement(condStm);
		condStm.setBranch(endTrue, endFalse);
	}

	/**
	 * Visiting simple statement
	 */
	void visitSimpleStatement(IASTStatement astStm, INormalStatement begin, IStatement end, IStatement throw_)
	{
		INormalStatement normal = new SimpleCppStatement(astStm);
		begin.setNextStatement(normal);

		if (astStm instanceof IASTReturnStatement) {
			normal.setNextStatement(END);
		} else if (isThrowStatement(astStm)) {
			normal.setNextStatement(throw_);
		} else {
			normal.setNextStatement(end);
		}
	}

	/**
	 * Check for null statement ";", or reference is null
	 * 
	 * @param stm
	 *            AST statement to check
	 * @return is null statement
	 */
	static boolean isNullStatement(IASTNode stm)
	{
		return stm == null || stm instanceof IASTNullStatement;
	}

	/**
	 * Check if this is throw exception statement
	 * 
	 * @param stm
	 *            AST statement to check
	 * @return is throw statement
	 */
	static boolean isThrowStatement(IASTStatement stm)
	{
		if (stm instanceof IASTExpressionStatement) {
			IASTExpression ex = ((IASTExpressionStatement) stm).getExpression();

			if (ex instanceof IASTUnaryExpression) {
				return ((IASTUnaryExpression) ex).getOperator() == IASTUnaryExpression.op_throw;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Normalize bracket in the expression
	 * 
	 * @param ex
	 *            AST expression to normalize
	 * @return normalized AST expression
	 */
	static IASTExpression normalize(IASTExpression ex)
	{
		while (ex instanceof IASTUnaryExpression) {
			IASTUnaryExpression unary = (IASTUnaryExpression) ex;
			if (unary.getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
				ex = unary.getOperand();
			} else {
				break;
			}
		}
		return ex;
	}
}

/**
 * Represent a single C/C++ statement inside CFG
 *
 * @date 2016-12-12 VuSD created
 */
class SimpleCppStatement extends NormalStatement {

	/**
	 * Create new simple C/C++ statement
	 * 
	 * @param stm
	 *            AST node corresponding to this statement
	 */
	public SimpleCppStatement(IASTNode stm)
	{
		super(CppConverter.convert(stm));

		String raw = stm.getRawSignature();
		if (!raw.isEmpty()) {
			setContent(raw);
		}
	}
}
