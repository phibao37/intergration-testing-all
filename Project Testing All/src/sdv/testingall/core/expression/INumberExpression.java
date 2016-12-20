/**
 * Interface for number constant expression
 * @file INumberExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Represent a number constant (int, long, float, double, ...), including boolean type
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public interface INumberExpression extends IExpression {

	/**
	 * Returns the value of the specified number expression as an {@code boolean}
	 * 
	 * @return {@code boolean} value
	 */
	boolean boolValue();

	/**
	 * Returns the value of the specified number expression as an {@code char}
	 * 
	 * @return {@code char} value [0, 2^16-1]
	 */
	char charValue();

	/**
	 * Returns the value of the specified number expression as an {@code int}
	 * 
	 * @return {@code int} value [-2^31, 2^31 - 1]
	 */
	int intValue();

	/**
	 * Returns the value of the specified number expression as an {@code long}
	 * 
	 * @return {@code long} value [-2^63, 2^63 - 1]
	 */
	long longValue();

	/**
	 * Returns the value of the specified number expression as an {@code float}
	 * 
	 * @return {@code float} value
	 */
	float floatValue();

	/**
	 * Returns the value of the specified number expression as an {@code double}
	 * 
	 * @return {@code double} value
	 */
	double doubleValue();

	/**
	 * Returns the value of the specified number expression as an {@code byte}
	 * 
	 * @return {@code byte} value [-2^7, 2^7 - 1]
	 */
	default byte byteValue()
	{
		return (byte) intValue();
	}

	/**
	 * Returns the value of the specified number expression as an {@code short}
	 * 
	 * @return {@code short} value [-2^15, 2^15 - 1]
	 */
	default short shortValue()
	{
		return (short) intValue();
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
