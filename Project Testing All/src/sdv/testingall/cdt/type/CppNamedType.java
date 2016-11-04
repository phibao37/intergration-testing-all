/**
 * A use-defined type name
 * @file CppNamedType.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.type;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;

import sdv.testingall.core.type.BaseType;
import sdv.testingall.core.type.ITypeModifier;

/**
 * A use-defined type name
 * 
 * @author VuSD
 *
 * @date 2016-11-04 VuSD created
 */
public class CppNamedType extends BaseType {

	private boolean	isFullQualified;
	private int		elaborated;

	/**
	 * Create new C/C++ use-defined type name
	 * 
	 * @param name
	 *            AST name node
	 * @param mdf
	 *            extra type modifier
	 */
	public CppNamedType(IASTName name, ITypeModifier mdf)
	{
		super(name.getLastName().toString(), parseNamePart(name), mdf);

		if (name.isQualified()) {
			isFullQualified = ((ICPPASTQualifiedName) name).isFullyQualified();
		}
	}

	/**
	 * Parse the name-part of this type name
	 * 
	 * @param name
	 *            AST name node
	 * @return list of name part or <code>null</code> if this is simple name
	 */
	static String[] parseNamePart(IASTName name)
	{
		if (name.isQualified()) {
			ICPPASTNameSpecifier[] listQualified = ((ICPPASTQualifiedName) name).getQualifier();
			String[] listPart = new String[listQualified.length];

			for (int i = 0; i < listPart.length; i++) {
				listPart[i] = new String(listQualified[i].toCharArray());
			}
			return listPart;
		}
		return null;
	}

	/**
	 * Check whether this is full qualified name: <code>::std::cout</code>
	 * 
	 * @return full qualified state
	 */
	public boolean isFullQualified()
	{
		return isFullQualified;
	}

	/**
	 * Get the exact type (enum/class/struct/union) that this name should belong to
	 * 
	 * @return the exact elaborated type
	 */
	public int getElaborated()
	{
		return elaborated;
	}

	/**
	 * Set the exact type (enum/class/struct/union) that this name should belong to
	 * 
	 * @param elaborated
	 *            exact elaborated type
	 */
	public void setElaborated(int elaborated)
	{
		this.elaborated = elaborated;
	}

	/** This name does not explicit define exact type */
	public static final int ELA_NONE = 0;

	/** This name must be a struct */
	public static final int ELA_STRUCT = 1;

	/** This name must be a union */
	public static final int ELA_UNION = 2;

	/** This name must be a enum */
	public static final int ELA_ENUM = 3;

	/** This name must be a class */
	public static final int ELA_CLASS = 4;
}
