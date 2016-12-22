/**
 * Interface for symbolic variable
 * @file IVariable.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.symbolicexec;

import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.type.IType;

/**
 * Interface for symbolic variable
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public interface IVariable {

	/**
	 * Get the name of variable
	 * 
	 * @return symbolic name
	 */
	String getName();

	/**
	 * Get the type of variable value
	 * 
	 * @return variable type
	 */
	IType getType();

	/**
	 * Get the value of variable
	 * 
	 * @return variable value
	 */
	IExpression getValue();

	/**
	 * Get the value for the variable
	 * 
	 * @param variable
	 *            variable value
	 */
	void setValue(IExpression value);

	/**
	 * Check whether this variable has value or not
	 * 
	 * @return has value or not
	 */
	default boolean isValueSet()
	{
		return getValue() != null;
	}

	/**
	 * Get the symbolic scope for the variable
	 * 
	 * @return
	 *         <ul>
	 *         <li>0: global variable</li>
	 *         <li>1: parameter variable</li>
	 *         <li>2+: local variable</li>
	 *         </ul>
	 */
	int getScope();

}
