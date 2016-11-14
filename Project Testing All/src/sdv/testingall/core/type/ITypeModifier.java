/**
 * Extra modifier
 * @file ITypeModifier.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.type;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Extra modifier for the type name
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
@NonNullByDefault
public interface ITypeModifier extends Cloneable {

	/**
	 * Check whether this type variable is constant
	 * 
	 * @return constant state
	 */
	boolean isConst();

	/**
	 * Create a copy of modifier
	 * 
	 * @return cloned modifier
	 */
	ITypeModifier clone();
}
