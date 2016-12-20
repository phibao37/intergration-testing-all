/**
 * Number constant expression for C/C++
 * @file CppNumberExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType;

import com.ibm.icu.impl.Utility;

import sdv.testingall.cdt.type.CppBasicType;
import sdv.testingall.cdt.type.CppTypeModifier;
import sdv.testingall.core.expression.Expression;

/**
 * Number constant expression for C/C++
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public class CppNumberExpression extends Expression implements ICppNumberExpression {

	private static final CppTypeModifier DEFAULT_MODIFIER = new CppTypeModifier();

	private CppBasicType type;

	private boolean	boolValue;
	private long	longValue;
	private double	doubleValue;

	/**
	 * Create new number constant expression
	 * 
	 * @param literal
	 *            AST constant expression node
	 */
	public CppNumberExpression(IASTLiteralExpression literal)
	{
		String value = literal.toString();
		setContent(value);
		IBasicType type = (IBasicType) literal.getExpressionType();
		this.type = new CppBasicType(type, DEFAULT_MODIFIER);

		switch (literal.getKind()) {
		case IASTLiteralExpression.lk_integer_constant:
			doubleValue = longValue = Long.parseLong(value);
			boolValue = longValue != 0;
		case IASTLiteralExpression.lk_float_constant:
			doubleValue = Double.parseDouble(value);
			longValue = (long) doubleValue;
			boolValue = doubleValue != 0.0;
		case IASTLiteralExpression.lk_true:
			boolValue = true;
			doubleValue = longValue = 1;
			break;
		case IASTLiteralExpression.lk_false:
			// Same as default field value
			break;
		case IASTLiteralExpression.lk_char_constant:
			value = Utility.unescape(value);
			doubleValue = longValue = value.charAt(1); // Get second position: 'x'
			boolValue = longValue != 0;
		}
	}

	@Override
	public CppBasicType getType()
	{
		return type;
	}

	@Override
	public boolean boolValue()
	{
		return boolValue;
	}

	@Override
	public char charValue()
	{
		return (char) longValue;
	}

	@Override
	public int intValue()
	{
		return (int) longValue;
	}

	@Override
	public long longValue()
	{
		return longValue;
	}

	@Override
	public float floatValue()
	{
		return (float) doubleValue;
	}

	@Override
	public double doubleValue()
	{
		return doubleValue;
	}

	@Override
	public int unsignedShortValue()
	{
		return (int) longValue;
	}

	@Override
	public long unsignedIntValue()
	{
		return longValue;
	}

}
