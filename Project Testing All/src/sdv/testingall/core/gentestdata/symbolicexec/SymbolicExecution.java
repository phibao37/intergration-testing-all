/**
 * Implementation for symbolic execution
 * @file SymbolicExecution.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.symbolicexec;

import sdv.testingall.core.expression.ExpressionVisitor;
import sdv.testingall.core.expression.IBinaryExpression;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.IExpressionFactory;
import sdv.testingall.core.expression.INameExpression;
import sdv.testingall.core.expression.IReturnExpression;
import sdv.testingall.core.expression.IUnaryExpression;
import sdv.testingall.core.gentestdata.IGenTestConfig;
import sdv.testingall.core.gentestdata.solver.IPathConstraint;
import sdv.testingall.core.gentestdata.solver.PathConstraint;
import sdv.testingall.core.gentestdata.symbolicexec.VariableTable.GroupWrapper;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.statement.IConditionStatement;
import sdv.testingall.core.statement.IScopeStatement;
import sdv.testingall.core.statement.IStatement;
import sdv.testingall.core.statement.ITestPath;

/**
 * Implementation for symbolic execution. The test path obtained from function will be parsed, each statement inside
 * will be analyze and grab all constraint so that the test path can be execute
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public abstract class SymbolicExecution extends ExpressionVisitor implements ISymbolicExecution, IExpressionFactory {

	private ProjectNode		project;
	private FunctionNode	function;
	private IGenTestConfig	config;
	private ITestPath		testpath;

	protected PathConstraint	constraint;
	protected IVariableTable	varTable;

	protected int			curStmIndex;
	protected GroupWrapper	rootGroup;

	/**
	 * Create new symbolic execution controller
	 * 
	 * @param project
	 *            root of project, use to resolve binding, find reference, ...
	 * @param function
	 *            function node need to generate
	 * @param config
	 *            configuration during test
	 * @param testpath
	 *            test path to analyze
	 */
	public SymbolicExecution(ProjectNode project, FunctionNode function, IGenTestConfig config, ITestPath testpath)
	{
		this.project = project;
		this.function = function;
		this.config = config;
		this.testpath = testpath;
	}

	/**
	 * Generate constraint for given test path
	 * 
	 */
	protected void generateConstraint()
	{
		constraint = new PathConstraint();
		varTable = createVariableTable();

		// Add global variable to table ...
		varTable.increaseScope();

		// Add function parameter to table
		for (VariableNode para : function.getParameter()) {
			Variable input = new Variable(para.getName(), para.getType());
			varTable.addVariable(input);
		}

		// Visit all statement inside test path
		for (curStmIndex = 0; curStmIndex < testpath.size(); curStmIndex++) {
			IStatement statement = testpath.get(curStmIndex);
			IExpression statementRoot = statement.getRoot();

			// This is a normal statement, accept expression visitor
			if (statementRoot != null) {
				rootGroup = new GroupWrapper(statementRoot.clone());
				rootGroup.accept(this);
				statementRoot = rootGroup.getChild();

				// Add condition statement to constraint
				if (statement instanceof IConditionStatement) {
					boolean isFalse = ((IConditionStatement) statement).falseBranch() == testpath.get(curStmIndex + 1);
					statementRoot = varTable.fill(statementRoot);

					// Reverse logical if is in false branch
					if (isFalse) {
						statementRoot = createUnary(statementRoot, IUnaryExpression.LOGIC_NOT, true);
					}

					constraint.getConstraints().add(statementRoot);
				}
			}

			// Visit a scope sign "{"/"}", reflect to the table
			else if (statement instanceof IScopeStatement) {
				IScopeStatement scopeStm = (IScopeStatement) statement;

				if (scopeStm.isOpening()) {
					varTable.increaseScope();
				} else {
					varTable.decreaseScope();
				}
			}
		}
	}

	/**
	 * Handle for assignment expression
	 * 
	 * @param assign
	 *            binary assignment expression
	 */
	protected void handleAssignment(IBinaryExpression assign)
	{
		IExpression left = assign.getLeft();
		IExpression right = assign.getRight();
		String operator = assign.getOperator();

		// This is nested assign and mathematics
		// Change from a += b to a = a+b
		if (!operator.equals(IBinaryExpression.ASSIGN)) {
			operator = operator.substring(0, 1);
			right = createBinary(left, operator, right);
		}

		// Direct assignment
		if (left instanceof INameExpression) {
			String name = ((INameExpression) left).getName();
			varTable.updateVariable(name, right);
		}

		// Replace in super group
		// call(a = b + 1) --> call(a)
		rootGroup.replaceChild(assign, left);
	}

	@Override
	public void leave(IBinaryExpression bin)
	{
		if (bin.isAssignExpression()) {
			handleAssignment(bin);
		}
	}

	@Override
	public void leave(IUnaryExpression unary)
	{
		if (unary.isAssignExpression()) {
			// Later
		}
	}

	@Override
	public void leave(IReturnExpression returnEx)
	{
		IExpression value = returnEx.getReturnValue();

		if (value != null) {
			constraint.setPathReturnValue(varTable.fill(value));
		}
	}

	@Override
	public FunctionNode getFunction()
	{
		return function;
	}

	@Override
	public ProjectNode getRootProject()
	{
		return project;
	}

	@Override
	public IGenTestConfig getConfig()
	{
		return config;
	}

	@Override
	public ITestPath getPath()
	{
		return testpath;
	}

	@Override
	public IVariableTable createVariableTable()
	{
		return new VariableTable();
	}

	@Override
	public IPathConstraint getConstraint()
	{
		if (constraint == null) {
			generateConstraint();
		}
		return constraint;
	}

}
