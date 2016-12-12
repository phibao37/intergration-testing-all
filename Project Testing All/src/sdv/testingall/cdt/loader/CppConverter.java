/**
 * Convert AST expression to core expression
 * @file CppConverter.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import sdv.testingall.core.expression.Expression;
import sdv.testingall.core.expression.IExpression;

/**
 * Convert AST expression to core expression
 * 
 * @author VuSD
 *
 * @date 2016-11-10 VuSD created
 */
public class CppConverter {

	/**
	 * Convert an AST expression to core expression
	 * 
	 * @param expression
	 *            AST expression
	 * @return converted expression
	 */
	public static IExpression convert(IASTNode expression)
	{
		return new StubExpression(expression.getRawSignature());
	}

	private static class StubExpression extends Expression {

		StubExpression(String content)
		{
			setContent(content);
		}
	}
}
