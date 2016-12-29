/**
 * Represent a variable declaration expression
 * @file IDeclareExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.type.IType;

/**
 * Represent a variable declaration expression
 * 
 * @author VuSD
 *
 * @date 2016-12-29 VuSD created
 */
public interface IDeclareExpression extends IExpressionGroup {

	/**
	 * Represent each declarator part inside a declaration
	 */
	public interface IDeclarator extends IExpressionGroup {

		/**
		 * Get the name of declaring variable
		 * 
		 * @return variable name
		 */
		INameExpression getName();

		/**
		 * Get the initialize value for the variable
		 * 
		 * @return default initialize value
		 */
		@Nullable
		IExpression getValue();

		@Override
		default int handleVisit(IExpressionVisitor visitor)
		{
			return visitor.visit(this);
		}

		@Override
		default void handleLeave(IExpressionVisitor visitor)
		{
			visitor.leave(this);
		}
	}

	/**
	 * Get the type of the declaring variable list
	 * 
	 * @return declaring type
	 */
	@Override
	@NonNull
	IType getType();

	/**
	 * Get the list of declaring variable
	 * 
	 * @return declaring list
	 */
	IDeclarator[] getDeclarators();

	@Override
	default int handleVisit(IExpressionVisitor visitor)
	{
		return visitor.visit(this);
	}

	@Override
	default void handleLeave(IExpressionVisitor visitor)
	{
		visitor.leave(this);
	}
}
