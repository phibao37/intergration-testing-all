/**
 * Interface for all expression
 * @file IExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.element.IDisplayable;
import sdv.testingall.core.expression.IExpressionVisitor.IVisitorState;
import sdv.testingall.core.type.IType;

/**
 * Interface for all expression
 * 
 * @author VuSD
 *
 * @date 2016-11-10 VuSD created
 */
@NonNullByDefault
public interface IExpression extends Cloneable, IDisplayable, IVisitorState {

	/**
	 * Get the copy of the expression
	 * 
	 * @return cloned expression
	 */
	IExpression clone();

	/**
	 * Get the expression that this expression cloned from
	 * 
	 * @return source expression, or <code>this</code> if this is already a origin
	 */
	IExpression getSource();

	/**
	 * Get the associated type with this expression
	 * 
	 * @return corresponding type
	 */
	@Nullable
	IType getType();

	/**
	 * Set whether this expression can be replace or not
	 * 
	 * @param replace
	 *            replaceable state
	 */
	void setReplaceable(boolean replace);

	/**
	 * Check whether this expression can be replace or not
	 * 
	 * @return replaceable state
	 */
	boolean isReplaceable();

	/**
	 * Accept a visitor to visit all expression inside
	 * 
	 * @param visitor
	 *            object to visit expression
	 * @return flag to indicate visited state, include {@link IVisitorState#PROCESS_ABORT},
	 *         {@link IVisitorState#PROCESS_SKIP}, {@link IVisitorState#PROCESS_CONTINUE}
	 */
	int accept(IExpressionVisitor visitor);

	/**
	 * Called when a visitor visiting this expression. <br/>
	 * Must call corresponding method from {@link IExpressionVisitork} class
	 * 
	 * @param visitor
	 *            object to visit expression
	 * @return flag to indicate visited state
	 */
	int handleVisit(IExpressionVisitor visitor);

	/**
	 * Called when a visitor leaving this expression. <br/>
	 * Must call corresponding method from {@link IExpressionVisitork} class
	 * 
	 * @param visitor
	 *            object to visit expression
	 */
	void handleLeave(IExpressionVisitor visitor);

}
