/**
 * Declarator part inside a declaration for C/C++
 * @file ICppDeclarator.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import sdv.testingall.cdt.type.CppTypeModifier;
import sdv.testingall.core.expression.IDeclareExpression.IDeclarator;

/**
 * Declarator part inside a declaration for C/C++
 * 
 * @author VuSD
 *
 * @date 2016-12-29 VuSD created
 */
public interface ICppDeclarator extends IDeclarator {

	/**
	 * Get extra modifier for the declarator
	 * 
	 * @return extra modifier
	 */
	CppTypeModifier getDeclareModifier();
}
