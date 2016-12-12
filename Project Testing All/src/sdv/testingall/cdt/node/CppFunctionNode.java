/**
 * C/C++ function node
 * @file CppFunctionNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.cdt.gencfg.CFGGeneration;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.statement.ICFG;
import sdv.testingall.core.statement.ICFG.ICFGType;
import sdv.testingall.core.type.IType;
import sdv.testingall.util.SDVUtils;

/**
 * C/C++ function node
 * 
 * @author VuSD
 *
 * @date 2016-11-03 VuSD created
 */
public class CppFunctionNode extends FunctionNode implements ICppDeclarable {

	private final @Nullable IASTStatement	body;
	private final IType						nameType;

	private ICFG	cfgNormal;
	private ICFG	cfgSubCond;

	/**
	 * Create new C/C++ function node
	 * 
	 * @param type
	 *            function return type
	 * @param nameType
	 *            function name (full-qualified as a type)
	 * @param params
	 *            list of function parameter
	 * @param body
	 *            the root statement of function body, can be <code>null</code> if this is a declaration
	 */
	public CppFunctionNode(IType type, IType nameType, VariableNode[] params, @Nullable IASTStatement body)
	{
		super(type, nameType.getName(), params);
		this.body = body;
		this.nameType = nameType;
	}

	@Override
	public boolean isDeclare()
	{
		return body == null;
	}

	/**
	 * Get the full-qualified function name (same as a type)
	 * 
	 * @return function name
	 */
	public IType getNameType()
	{
		return nameType;
	}

	/**
	 * Get the body implement of this function
	 * 
	 * @return AST node corresponding to function body
	 */
	public IASTStatement getBody()
	{
		return body;
	}

	@Override
	public void setDescription(String description)
	{
		super.setDescription(SDVUtils.removeCommentFlag(description));
	}

	@Override
	public boolean shouldDisplay()
	{
		return !isDeclare();
	}

	@Override
	public ICFG getCFG(ICFGType type) throws NullPointerException
	{
		if (type.isExpandSubCondition()) {
			if (cfgNormal == null) {
				cfgNormal = new CFGGeneration(this, type).getCFG();
			}
			return cfgNormal;
		} else {
			if (cfgSubCond == null) {
				cfgSubCond = new CFGGeneration(this, type).getCFG();
			}
			return cfgSubCond;
		}
	}

}
