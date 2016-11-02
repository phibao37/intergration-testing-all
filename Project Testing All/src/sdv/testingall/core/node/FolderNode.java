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
public class FolderNode extends BaseNode {

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

	@Override
	public int compareTo(INode o)
	{
		// If o is a file, then this folder go first
		if (o instanceof FileNode) {
			return -1;
		}

		// If o is a folder, compare with its name
		else if (o instanceof FolderNode) {
			return toString().compareTo(o.toString());
		}

		return 0;
	}

}
