/**
 * Test data generation configuration for C/C++
 * @file ICppGenTestConfig.java
 * @author (SDV)[phibao37]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gentestdata;

import sdv.testingall.core.gentestdata.IGenTestConfig;

/**
 * Test data generation configuration for C/C++
 * 
 * @author phibao37
 *
 * @date 2016-12-24 phibao37 created
 */
public interface ICppGenTestConfig extends IGenTestConfig {

	/**
	 * Get size of {@code char}
	 * 
	 * @return size in byte
	 */
	int sizeOfChar();

	/**
	 * Get size of {@code short}
	 * 
	 * @return size in byte
	 */
	int sizeOfShort();

	/**
	 * Get size of {@code int}
	 * 
	 * @return size in byte
	 */
	int sizeOfInt();

	/**
	 * Get size of {@code long}
	 * 
	 * @return size in byte
	 */
	int sizeOfLong();

	/**
	 * Get size of {@code long long}
	 * 
	 * @return size in byte
	 */
	int sizeOfLongLong();

	/**
	 * Check for valid basic data size type constraint
	 * 
	 * @return if setup is valid or not
	 * @see http://www.cplusplus.com/doc/tutorial/variables/
	 */
	default boolean checkValidCppSize()
	{
		int sChar = sizeOfChar(), sShort = sizeOfShort(), sInt = sizeOfInt(), sLong = sizeOfLong(),
				sLongLong = sizeOfLongLong();
		return sChar >= 1 && sShort >= sChar && sShort >= 2 && sInt >= sShort && sInt >= 2 && sLong >= sInt
				&& sLong >= 4 && sLongLong >= sLong && sLongLong >= 8;
	}
}
