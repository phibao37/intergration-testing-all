/**
 * A list of variable for symbolic execution
 * @file IVariableTable.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.symbolicexec;

import sdv.testingall.core.expression.IExpression;

/**
 * A list of variable for symbolic execution
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public interface IVariableTable {

	/**
	 * Add new variable to table. The scope of variable will be set from current scope of table
	 * 
	 * @param var
	 *            variable to add
	 */
	void addVariable(IVariable var);

	/**
	 * Increase the table scope by 1. Default scope is 0
	 */
	void increaseScope();

	/**
	 * Decrease the table scope by 1. Default scope is 0<br/>
	 * Any variable that is in current scope before decrease will be remove from table
	 */
	void decreaseScope();

	/**
	 * Update variable with new value
	 * 
	 * @param name
	 *            name of variable to look for
	 * @param value
	 *            new value for variable
	 */
	void updateVariable(String name, IExpression value);

	/**
	 * Fill the given value with the data from this variable table. Example:
	 * 
	 * <pre>
	 * table = [a = 1; b = 0; c = 2]<br/>
	 * (a + 1 - d) / (b - c) ---> (1 + 1 - d) / (0 - 2)
	 * </pre>
	 * 
	 * <i>Note:</i> this method only fill expression with value but does not evaluate or simplify expression value
	 * 
	 * @param ex
	 *            expression to fill value
	 * @return expression after filled value
	 */
	IExpression fill(IExpression ex);

	/**
	 * Find the variable with given name. If there are more than one with same name, the variable with highest scope
	 * will be returned
	 * 
	 * @param name
	 *            variable name to find
	 * @return variable with name as given
	 */
	IVariable find(String name);
}
