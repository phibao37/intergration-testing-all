/**
 * Interface for all expression
 * @file IExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import sdv.testingall.core.type.IType;

/**
 * Interface for all expression
 * 
 * @author VuSD
 *
 * @date 2016-11-10 VuSD created
 */
public interface IExpression extends Cloneable {

	/**
	 * Set the content to be display
	 * 
	 * @param content
	 *            expression content
	 */
	void setContent(String content);

	/**
	 * Return the expression content
	 * 
	 * @return the content of the expression to be display
	 */
	@Override
	String toString();

	/**
	 * Get the copy of the expression
	 * 
	 * @return cloned expression
	 */
	IExpression clone();

	/**
	 * Resolve the type correspond to this expression
	 * 
	 * @return corresponding type
	 */
	IType bind();

}
