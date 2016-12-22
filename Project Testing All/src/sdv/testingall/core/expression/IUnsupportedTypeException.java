/**
 * Interface for expression that is unsupported type
 * @file IUnsupportedTypeException.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import sdv.testingall.core.type.IType;

/**
 * Interface for expression that is unsupported type
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public interface IUnsupportedTypeException extends IExpression {

	/**
	 * Get the class type for unsupported object
	 * 
	 * @return Java class type
	 */
	Class<?> getUnsupportedClass();

	/**
	 * Get the unsupported object
	 * 
	 * @return unsupported object
	 */
	Object getUnsupportedObject();

	@Override
	default IType getType()
	{
		return null;
	}

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
