/**
 * C/C++ file node
 * @file CppFileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import java.io.File;

import javafx.scene.image.Image;
import sdv.testingall.core.node.FileNode;

/**
 * C/C++ file node
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class CppFileNode extends FileNode {

	private static final Image	ICON_C		= new Image(FileNode.class.getResourceAsStream("/node/c.png"));		//$NON-NLS-1$
	private static final Image	ICON_CPP	= new Image(FileNode.class.getResourceAsStream("/node/cpp.png"));	//$NON-NLS-1$

	private final Image icon;

	/**
	 * Create new C/C++ file node
	 * 
	 * @param file
	 *            file object
	 * @param isCpp
	 *            this is C++ source file (rather than C source file)
	 */
	public CppFileNode(File file, boolean isCpp)
	{
		super(file);
		this.icon = isCpp ? ICON_CPP : ICON_C;
	}

	@Override
	public Image getIcon()
	{
		return icon;
	}

}
