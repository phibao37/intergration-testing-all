/**
 * Implementation for C/C++ name expression
 * @file CppNameExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;

import sdv.testingall.cdt.util.ASTUtil;
import sdv.testingall.core.expression.Expression;
import sdv.testingall.core.type.IType;

/**
 * Implementation for C/C++ name expression
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public class CppNameExpression extends Expression implements ICppNameExpression {

	private String		name;
	private String[]	namePart;
	private boolean		isFullQualified;
	private IType		type;

	/**
	 * Create new name expression
	 * 
	 * @param astName
	 *            AST node for name
	 */
	public CppNameExpression(IASTName astName)
	{
		name = astName.getLastName().toString();
		namePart = ASTUtil.parseNamePart(astName);

		if (astName instanceof ICPPASTQualifiedName) {
			isFullQualified = ((ICPPASTQualifiedName) astName).isFullyQualified();
		}

		setContent(astName.toString());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String[] getNameParts()
	{
		return namePart;
	}

	@Override
	public IType getType()
	{
		return type;
	}

	@Override
	public void setType(IType type)
	{
		this.type = type;
	}

	@Override
	public boolean isFullQualifiedName()
	{
		return isFullQualified;
	}

}
