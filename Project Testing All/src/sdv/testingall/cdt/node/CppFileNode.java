/**
 * C/C++ file node
 * @file CppFileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import sdv.testingall.core.node.FileNode;

/**
 * C/C++ file node
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class CppFileNode extends FileNode {

	private static final ImageIcon ICON_C = new ImageIcon(ImageIcon.class.getResource("/node/c.png")),
			ICON_CPP = new ImageIcon(ImageIcon.class.getResource("/node/cpp.png"));

	private ImageIcon icon;

	/**
	 * Create new C/C++ file node
	 * 
	 * @param name
	 *            file name
	 * @param isCpp
	 *            this is C++ source file (rather than C source file)
	 */
	public CppFileNode(String name, boolean isCpp)
	{
		super(name);
		this.icon = isCpp ? ICON_CPP : ICON_C;
	}

	@Override
	public Icon getIcon()
	{
		return icon;
	}

}
