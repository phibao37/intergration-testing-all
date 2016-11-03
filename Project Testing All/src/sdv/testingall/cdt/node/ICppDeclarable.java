/**
 * Represent a declarable node
 * @file ICppDeclarable.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import sdv.testingall.core.node.INode;

/**
 * This type of node can be either a declaration or a definition
 * 
 * @author VuSD
 *
 * @date 2016-11-03 VuSD created
 */
public interface ICppDeclarable extends INode {

	/**
	 * Check whether this is declare node, instead of a definition
	 * 
	 * @return declare state
	 */
	boolean isDeclare();
}
