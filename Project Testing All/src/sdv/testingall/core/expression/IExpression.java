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
import sdv.testingall.core.type.IType;

/**
 * Interface for all expression
 * 
 * @author VuSD
 *
 * @date 2016-11-10 VuSD created
 */
@NonNullByDefault
public interface IExpression extends Cloneable, IDisplayable {

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
	@Nullable
	IType bind();

}
