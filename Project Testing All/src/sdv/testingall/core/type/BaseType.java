/**
 * Basic type implementation
 * @file BaseType.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.type;

import sdv.testingall.core.node.INode;

/**
 * Basic type implementation
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
public class BaseType implements IType {

	private String			name;
	private String[]		namePart;
	private INode			bindNode;
	private ITypeModifier	mdf;

	/**
	 * Create new simple type from the name and the name part
	 * 
	 * @param typeName
	 *            name of the type
	 * @param namePart
	 *            list of name-part to construct this type name
	 * @param mdf
	 *            extra modifier about the type
	 */
	public BaseType(String typeName, String[] namePart, ITypeModifier mdf)
	{
		this.name = typeName;
		this.namePart = namePart;
		this.mdf = mdf;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public String[] getNameParts()
	{
		return namePart;
	}

	@Override
	public INode bind()
	{
		return bindNode;
	}

	@Override
	public void setBind(INode bind)
	{
		bindNode = bind;
	}

	@Override
	public ITypeModifier getTypeModifier()
	{
		return mdf;
	}

	@Override
	public BaseType clone()
	{
		try {
			BaseType clone = (BaseType) super.clone();
			clone.mdf = mdf.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			return this;
		}
	}
}
