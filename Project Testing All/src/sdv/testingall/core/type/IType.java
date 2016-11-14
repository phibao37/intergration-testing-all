/**
 * Basic type interface
 * @file IType.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.type;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.node.INode;

/**
 * Basic type interface
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
@NonNullByDefault
public interface IType extends Cloneable {

	/**
	 * Get the name of the type
	 * 
	 * @return type name
	 */
	String getName();

	/**
	 * Get the type name description, include type modifier
	 * 
	 * @return type name
	 */
	@Override
	String toString();

	/**
	 * Get the extra modifier about the type
	 * 
	 * @return type modifier
	 */
	ITypeModifier getTypeModifier();

	/**
	 * Get the target node that belong to this type name
	 * 
	 * @return corresponding node
	 */
	@Nullable
	INode bind();

	/**
	 * Set the target node of this type name
	 * 
	 * @param bind
	 *            target node
	 */
	void setBind(INode bind);

	/**
	 * Check whether this type has attached to the node
	 * 
	 * @return binding state
	 */
	default boolean hasBind()
	{
		return bind() != null;
	}

	/**
	 * Get the list of name-part construct to this name (exclusion the name)
	 * 
	 * @return list of name-part or <code>null</code> if this name has only one part (as {@link #getName()})
	 */
	default String @Nullable [] getNameParts()
	{
		return null;
	}

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
	 * Get a clone of type
	 * 
	 * @return cloned type
	 */
	IType clone();
}
