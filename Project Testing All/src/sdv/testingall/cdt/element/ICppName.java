/**
 * Represent a C/C++ name reference
 * @file ICppName.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.element;

/**
 * Represent a C/C++ name reference
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public interface ICppName {

	/**
	 * Get the last name in a C/C++ name
	 * 
	 * @return last name
	 */
	String getName();

	/**
	 * Get the qualified name part excluding the {@link #getName()}
	 * 
	 * @return qualified name part, can be <code>null</code>
	 */
	String[] getNameParts();

	/**
	 * Check whether this name consist of more than one name-part
	 * 
	 * @return multiple name-part state
	 */
	default boolean isMultipleNamePart()
	{
		return getNameParts() != null;
	}

	/**
	 * Check if this name is full-qualified, example: <code>::std::cout</code>
	 * 
	 * @return full-qualified state
	 */
	boolean isFullQualifiedName();
}
