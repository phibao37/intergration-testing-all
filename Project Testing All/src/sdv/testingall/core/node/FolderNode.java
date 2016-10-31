/**
 * A node to represent a folder
 * @file FolderNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A node to represent a folder
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
public class FolderNode extends INode {

	private static final ImageIcon ICON = new ImageIcon(ImageIcon.class.getResource("/node/folder.png"));

	/**
	 * Create new folder node
	 * 
	 * @param name
	 *            folder name
	 */
	public FolderNode(String name)
	{
		super(name);
	}

	@Override
	public Icon getIcon()
	{
		return ICON;
	}

}
