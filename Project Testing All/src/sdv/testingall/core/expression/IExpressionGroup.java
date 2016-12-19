/**
 * Interface for expression group
 * @file IExpressionGroup.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Represent a expression that have a group of child expression
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public interface IExpressionGroup extends IExpression {

	/**
	 * Get the array of child expression inside this group
	 * 
	 * @return child expressions
	 */
	IExpression[] getChilds();

	/**
	 * Replace a child expression with a new one
	 * 
	 * @param find
	 *            old expression to find, can be match if two expression is cloned from same source
	 * @param replace
	 *            new expression to replace (must not be any super expression or the cyclic looping error will occur)
	 * @return child expression has been replaced or not
	 */
	boolean replaceChild(IExpression find, IExpression replace);

	/**
	 * Generate the content based on child expressions for this group.<br/>
	 * Can be called from constructor or when child has been modified
	 * 
	 * @return computed string content
	 */
	String computeContent();

	/**
	 * Set that the content has been invalidated due to child modification
	 */
	void invalidateChildContent();
}
