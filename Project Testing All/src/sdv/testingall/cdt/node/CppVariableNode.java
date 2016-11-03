/**
 * C/C++ variable node
 * @file CppVariableNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.type.IType;

/**
 * C/C++ variable node
 * 
 * @author VuSD
 *
 * @date 2016-11-03 VuSD created
 */
public class CppVariableNode extends VariableNode implements ICppDeclarable {

	private boolean isDeclare;

	/**
	 * Create new C/C++ variable node
	 * 
	 * @param type
	 *            variable type
	 * @param name
	 *            variable name
	 * @param isDeclare
	 *            this is a declaration or not
	 */
	public CppVariableNode(IType type, String name, boolean isDeclare)
	{
		super(type, name);
		this.isDeclare = isDeclare;
	}

	@Override
	public boolean isDeclare()
	{
		return isDeclare;
	}

}
