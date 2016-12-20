/**
 * Expression that can set their type
 * @file ISetTypaableExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import sdv.testingall.core.type.IType;

/**
 * Expression that can set their type
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public interface ISetTypaableExpression extends IExpression {

	/**
	 * Set the type for this expression
	 * 
	 * @param type
	 *            expression type
	 */
	void setType(IType type);
}
