/**
 * Number constant expression for C/C++
 * @file CppNumberExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType;

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
			doubleValue = longValue = convertCppIntegerLiteral(value);
			boolValue = longValue != 0;
			break;
		case IASTLiteralExpression.lk_float_constant:
			doubleValue = Double.parseDouble(value);
			longValue = (long) doubleValue;
			boolValue = doubleValue != 0.0;
			break;
		case IASTLiteralExpression.lk_true:
			boolValue = true;
			doubleValue = longValue = 1;
			break;
		case IASTLiteralExpression.lk_false:
			// Same as default field value
			break;
		case IASTLiteralExpression.lk_char_constant:
			value = StringEscapeUtils.unescapeJava(value);
			doubleValue = longValue = value.charAt(1); // Get second position: 'x'
			boolValue = longValue != 0;
			break;
		}
	}

	/**
	 * Create new boolean constant expression
	 * 
	 * @param boolValue
	 *            boolean value
	 * @param type
	 *            attached type
	 */
	public CppNumberExpression(boolean boolValue, CppBasicType type)
	{
		setContent(Boolean.toString(boolValue));

		this.boolValue = boolValue;
		doubleValue = longValue = boolValue ? 1 : 0;
	}

	/**
	 * Create new integer constant expression
	 * 
	 * @param intValue
	 *            integer value
	 * @param type
	 *            attached type
	 */
	public CppNumberExpression(long intValue, CppBasicType type)
	{
		setContent(convertString(intValue, type));

		doubleValue = longValue = intValue;
		boolValue = intValue != 0;
	}

	/**
	 * Create new decimal constant expression
	 * 
	 * @param decValue
	 *            decimal value
	 * @param type
	 *            attached type
	 */
	public CppNumberExpression(double decValue, CppBasicType type)
	{
		setContent(Double.toString(decValue));

		doubleValue = decValue;
		longValue = (long) doubleValue;
		boolValue = doubleValue != 0.0;
	}

	/**
	 * Convert an integer literal in C/C++ format to corresponding value
	 * 
	 * @param value
	 *            C/C++ integer token as string
	 *
	 * @return integer value
	 * @see http://en.cppreference.com/w/cpp/language/integer_literal
	 */
	@SuppressWarnings("nls")
	static long convertCppIntegerLiteral(String value)
	{
		int radix = 10;
		int index = 0;
		boolean negative = false;
		Long result;

		// if (value.length() == 0) {
		// throw new NumberFormatException("Zero length string");
		// }
		char firstChar = value.charAt(0);
		// Handle sign, if present
		if (firstChar == '-') {
			negative = true;
			index++;
		} else if (firstChar == '+') {
			index++;
		}

		// Normalize lower-case
		value = value.toLowerCase();
		// C++ 14: 18'446'744 --> 18446774
		// Replace 3u, 3l, 3llu as well
		value = value.replaceAll("['lu]", "");

		// Handle radix specifier, if present
		if (value.startsWith("0x", index)) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("0b", index)) {
			index += 2;
			radix = 2;
		} else if (value.startsWith("0", index) && value.length() > 1 + index) {
			index++;
			radix = 8;
		}

		try {
			result = Long.valueOf(value.substring(index), radix);
			result = negative ? Long.valueOf(-result.longValue()) : result;
		} catch (NumberFormatException e) {
			// If number is Long.MIN_VALUE, we'll end up here. The next line
			// handles this case, and causes any genuine format error to be re-thrown.
			String constant = negative ? ("-" + value.substring(index)) : value.substring(index);
			result = Long.valueOf(constant, radix);
		}
		return result;
	}

	/**
	 * Convert integer value to C/C++ literal string representation
	 * 
	 * @param value
	 *            integer value
	 * @param type
	 *            target literal type
	 * @return C/C++ integer literal
	 */
	static String convertString(long value, CppBasicType type)
	{
		// Escape character: 0 -> '\0', 10 -> '\n'
		if (type.getType() == CppBasicType.CHAR && value >= 0) {
			String str = String.valueOf((char) value);
			str = StringEscapeUtils.escapeJava(str);
			return String.format("'%s'", str); //$NON-NLS-1$
		}

		StringBuilder b = new StringBuilder(Long.toString(value));

		// Check for long/long long, unsigned
		if (type.isLong()) {
			b.append('l');
		} else if (type.isLongLong()) {
			b.append('l').append('l');
		}
		if (type.isUnsigned()) {
			b.append('u');
		}

		return b.toString();
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
