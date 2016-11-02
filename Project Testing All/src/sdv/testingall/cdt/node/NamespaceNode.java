/**
 * C++ namespace node
 * @file NamespaceNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import sdv.testingall.core.node.BaseNode;
import sdv.testingall.core.node.INode;

/**
 * C++ namespace node
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public class NamespaceNode extends BaseNode {

	private static final ImageIcon ICON = new ImageIcon(ImageIcon.class.getResource("/node/namespace.png"));

	private boolean isAnonymous;

	/**
	 * Create new namespace node
	 * 
	 * @param name
	 *            name of the namespace
	 */
	public NamespaceNode(String name)
	{
		super(name);

		if (name.isEmpty()) {
			setContent("<anonymous>");
			isAnonymous = true;
		}
	}

	/**
	 * Check if this namespace is anonymous (without the name)
	 * 
	 * @return anonymous state
	 */
	public boolean isAnonymous()
	{
		return isAnonymous;
	}

	@Override
	public Icon getIcon()
	{
		return ICON;
	}

	@Override
	public int compareTo(INode o)
	{
		return 0;
	}

}
