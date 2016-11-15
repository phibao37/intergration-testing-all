/**
 * Extra modifier for C/C++ type
 * @file CppTypeModifier.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.type;

import sdv.testingall.core.type.ITypeModifier;

/**
 * Extra modifier for C/C++ type
 * 
 * @author VuSD
 *
 * @date 2016-11-03 VuSD created
 */
public class CppTypeModifier implements ITypeModifier {

	private boolean	isConst;
	private boolean	isRef;
	private boolean	isStatic;
	private int		pointerLevel;

	/**
	 * Create new C/C++ type modifier
	 */
	public CppTypeModifier()
	{
	}

	/**
	 * Set whether this variable type is constant
	 * 
	 * @param isConst
	 *            constant state
	 * @return current object
	 */
	public CppTypeModifier setConst(boolean isConst)
	{
		this.isConst = isConst;
		return this;
	}

	@Override
	public boolean isConst()
	{
		return isConst;
	}

	/**
	 * Check whether this variable type is reference
	 * 
	 * @return reference state
	 */
	public boolean isReference()
	{
		return isRef;
	}

	/**
	 * Set whether this variable type is reference
	 * 
	 * @param isRef
	 *            reference state
	 */
	public void setReference(boolean isRef)
	{
		this.isRef = isRef;
	}

	/**
	 * Get current pointer level
	 * 
	 * @return pointer level
	 */
	public int getPointerLevel()
	{
		return pointerLevel;
	}

	/**
	 * Set current pointer level
	 * 
	 * @param pointerLevel
	 *            pointer level
	 */
	public void setPointerLevel(int pointerLevel)
	{
		this.pointerLevel = pointerLevel;
	}

	@Override
	public CppTypeModifier clone()
	{
		try {
			return (CppTypeModifier) super.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
	}

	/**
	 * Check whether this variable type is static
	 * 
	 * @return static state
	 */
	public boolean isStatic()
	{
		return isStatic;
	}

	/**
	 * Set whether this variable type is static
	 * 
	 * @param isStatic
	 *            static state
	 */
	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}
}
