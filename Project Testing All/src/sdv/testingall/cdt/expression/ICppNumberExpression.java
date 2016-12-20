/**
 * Interface for constant number expression for C/C++
 * @file ICppNumberExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import sdv.testingall.core.expression.INumberExpression;

/**
 * Interface for constant number expression for C/C++
 * 
 * @see http://www.cplusplus.com/doc/tutorial/variables/
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public interface ICppNumberExpression extends INumberExpression {

	/**
	 * Returns the value of the specified number expression as an {@code char} in C/C++
	 * 
	 * @return
	 *         <ul>
	 *         <li>{@code signed char} value: [-128, 127]</li>
	 *         <li>{@code unsigned char} value: [0, 255]</li>
	 *         </ul>
	 */
	@Override
	char charValue();

	/**
	 * Returns the value of the specified number expression as an {@code long}/{@code long long} in C/C++
	 * 
	 * @return {@code long}/{@code long long} value [-2^63, 2^63 - 1]
	 */
	@Override
	long longValue();

	/**
	 * Returns the value of the specified number expression as an {@code unsigned short} in C/C++
	 * 
	 * @return {@code unsigned short} value [0, 2^16 - 1]
	 */
	int unsignedShortValue();

	/**
	 * Returns the value of the specified number expression as an {@code unsigned int} in C/C++
	 * 
	 * @return {@code unsigned int} value [0, 2^32 - 1]
	 */
	long unsignedIntValue();

	// TODO: for unsigned long [0, 2^64 - 1]
}
