/**
 * C/C++ function node
 * @file CppFunctionNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import org.eclipse.cdt.core.dom.ast.IASTStatement;

import com.sun.istack.internal.Nullable;

import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.type.IType;

/**
 * C/C++ function node
 * 
 * @author VuSD
 *
 * @date 2016-11-03 VuSD created
 */
public class CppFunctionNode extends FunctionNode implements ICppDeclarable {

	private IASTStatement body;

	/**
	 * Create new C/C++ function node
	 * 
	 * @param type
	 *            function return type
	 * @param name
	 *            function name
	 * @param params
	 *            list of function parameter
	 * @param body
	 *            the root statement of function body, can be <code>null</code> if this is a declaration
	 */
	public CppFunctionNode(IType type, String name, VariableNode[] params, @Nullable IASTStatement body)
	{
		super(type, name, params);
		this.body = body;
	}

	@Override
	public boolean isDeclare()
	{
		return body == null;
	}

}
