/**
 * Expression for unsupported AST type
 * @file CppUnsupportedTypeExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import sdv.testingall.core.expression.Expression;
import sdv.testingall.core.expression.IUnsupportedTypeException;

/**
 * Expression for unsupported AST type
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class CppUnsupportedTypeExpression extends Expression implements IUnsupportedTypeException {

	private IASTNode node;

	/**
	 * Create new C/C++ unsupported type expression
	 * 
	 * @param node
	 *            unsupported AST node
	 */
	public CppUnsupportedTypeExpression(IASTNode node)
	{
		this.node = node;
		setContent(node.getRawSignature());
		System.out.printf("Unsupport expression %s of type %s%n", this, node.getClass()); //$NON-NLS-1$
	}

	@Override
	public Class<?> getUnsupportedClass()
	{
		return node.getClass();
	}

	@Override
	public IASTNode getUnsupportedObject()
	{
		return node;
	}

}
