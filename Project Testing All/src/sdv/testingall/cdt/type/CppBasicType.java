/**
 * Built-in C/C++ data type
 * @file CppBasicType.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.type;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;

import sdv.testingall.core.type.BaseType;
import sdv.testingall.core.type.ITypeModifier;

/**
 * Built-in C/C++ data type
 * 
 * @author VuSD
 *
 * @date 2016-11-07 VuSD created
 */
public class CppBasicType extends BaseType {

	private final boolean	isSigned, isUnsigned, isShort, isLong, isLongLong;
	private final int		typeFlag;

	/**
	 * Create new basic data type
	 * 
	 * @param simpleType
	 *            AST type node
	 * @param mdf
	 *            extra type modifier
	 */
	public CppBasicType(IASTSimpleDeclSpecifier simpleType, ITypeModifier mdf)
	{
		// [SAFE_CHECKED] getRawSignature() //
		super(simpleType.getRawSignature(), null, mdf);

		isSigned = simpleType.isSigned();
		isUnsigned = simpleType.isUnsigned();
		isShort = simpleType.isShort();
		isLong = simpleType.isLong();
		isLongLong = simpleType.isLongLong();
		int typeFlag = simpleType.getType();

		// Re-sync type "int" if omitted
		if (typeFlag == UNSPECIFIED && (isSigned || isUnsigned || isShort || isLong || isLongLong)) {
			typeFlag = INT;
		}

		this.typeFlag = typeFlag;
	}

	/**
	 * Create new basic data type from AST binding
	 * 
	 * @param simpleType
	 *            AST type binding
	 * @param mdf
	 *            extra type modifier
	 */
	public CppBasicType(IBasicType simpleType, ITypeModifier mdf)
	{
		super(null, null, mdf);

		isSigned = simpleType.isSigned();
		isUnsigned = simpleType.isUnsigned();
		isShort = simpleType.isShort();
		isLong = simpleType.isLong();
		isLongLong = simpleType.isLongLong();
		int typeFlag = convertKind(simpleType.getKind());

		// Re-sync type "int" if omitted
		if (typeFlag == UNSPECIFIED && (isSigned || isUnsigned || isShort || isLong || isLongLong)) {
			typeFlag = INT;
		}

		this.typeFlag = typeFlag;
	}

	static int convertKind(Kind kind)
	{
		switch (kind) {
		case eBoolean:
			return BOOL;
		case eChar:
		case eWChar:
		case eChar16:
		case eChar32:
			return CHAR;
		case eDouble:
			return DOUBLE;
		case eFloat:
		case eFloat128:
			return FLOAT;
		case eInt:
			return INT;
		case eVoid:
			return VOID;
		default:
			return UNSPECIFIED;
		}
	}

	/**
	 * Get the basic type flag that differ each basic type to each other
	 * 
	 * @return basic type flag
	 */
	public int getType()
	{
		return typeFlag;
	}

	/**
	 * <code>signed char c;</code>
	 * 
	 * @return signed state
	 */
	public boolean isSigned()
	{
		return isSigned;
	}

	/**
	 * <code>unsigned int u;</code>
	 * 
	 * @return unsigned state
	 */
	public boolean isUnsigned()
	{
		return isUnsigned;
	}

	/**
	 * <code>short int s;</code>
	 * 
	 * @return short state
	 */
	public boolean isShort()
	{
		return isShort;
	}

	/**
	 * <code>long int l;</code>
	 * 
	 * @return long state
	 */
	public boolean isLong()
	{
		return isLong;
	}

	/**
	 * <code>long long int l;</code>
	 * 
	 * @return long long state
	 * 
	 */
	public boolean isLongLong()
	{
		return isLongLong;
	}

	/**
	 * Used for declaration of constructors, destructors
	 */
	public static final int UNSPECIFIED = IASTSimpleDeclSpecifier.t_unspecified;

	/**
	 * <code>void x();</code>
	 */
	public static final int VOID = IASTSimpleDeclSpecifier.t_void;

	/**
	 * <code>char c;</code>
	 */
	public static final int CHAR = IASTSimpleDeclSpecifier.t_char;

	/**
	 * <code>int i;</code>
	 */
	public static final int INT = IASTSimpleDeclSpecifier.t_int;

	/**
	 * <code>float f;</code>
	 */
	public static final int FLOAT = IASTSimpleDeclSpecifier.t_float;

	/**
	 * <code>double d;</code>
	 */
	public static final int DOUBLE = IASTSimpleDeclSpecifier.t_double;

	/**
	 * Represents a boolean type (bool in c++, _Bool in c)
	 */
	public static final int BOOL = IASTSimpleDeclSpecifier.t_bool;

	/**
	 * <code>auto c = expression;</code>
	 */
	public static final int AUTO = IASTSimpleDeclSpecifier.t_auto;
}
