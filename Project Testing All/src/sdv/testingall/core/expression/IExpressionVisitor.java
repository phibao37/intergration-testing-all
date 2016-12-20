/**
 * Visitor interface inside expression
 * @file IExpressionVisitor.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Visitor interface inside expression. The visitor will travel from outer most expression and go to each child. When an
 * expression is visited or leaved, corresponding handle will be trigger based on expression type
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public interface IExpressionVisitor {

	/**
	 * State during visitor
	 */
	public interface IVisitorState {

		/** Abort visit all remaining expression */
		int PROCESS_ABORT = -1;

		/** Abort visit child expression and continue to visit remaining expression */
		int PROCESS_SKIP = 0;

		/** Visit all child expression inside, then visit remaining expression */
		int PROCESS_CONTINUE = 1;
	}

	/**
	 * Fired when expression to be visited
	 * 
	 * @param expression
	 *            expression to be visit
	 * @return should visit this expression
	 */
	boolean preVisit(IExpression expression);

	/**
	 * Fired when expression has finishing visited.<br/>
	 * This method will be trigger even if {@link #preVisit(IExpression)} return <code>false</code>
	 * 
	 * @param expression
	 *            expression has finish visit
	 */
	void postVisit(IExpression expression);

	int visit(INumberExpression number);

	void leave(INumberExpression number);
}
