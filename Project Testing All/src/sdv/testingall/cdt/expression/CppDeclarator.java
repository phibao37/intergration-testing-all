/**
 * Implement for C/C++ declarator part
 * @file CppDeclarator.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import sdv.testingall.cdt.type.CppTypeModifier;
import sdv.testingall.core.expression.DeclareExpression.Declarator;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.INameExpression;

/**
 * Implement for C/C++ declarator part
 * 
 * @author VuSD
 *
 * @date 2016-12-29 VuSD created
 */
public class CppDeclarator extends Declarator implements ICppDeclarator {

	private CppTypeModifier mdf;

	/**
	 * Create new declarator part
	 * 
	 * @param name
	 *            declaring name
	 * @param value
	 *            default declaring value, can be {@code null}
	 * @param mdf
	 *            extra modifier for the declarator
	 */
	public CppDeclarator(INameExpression name, IExpression value, CppTypeModifier mdf)
	{
		super(name, value);
		this.mdf = mdf;
	}

	@Override
	public CppTypeModifier getDeclareModifier()
	{
		return mdf;
	}

}
