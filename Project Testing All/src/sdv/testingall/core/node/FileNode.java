/**
 * A node to represent a file
 * @file FileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A node to represent a file
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class FileNode extends INode {

	private static final ImageIcon ICON = new ImageIcon(ImageIcon.class.getResource("/node/text-file.png"));

	/**
	 * Create new file node
	 * 
	 * @param name
	 *            file name
	 */
	public FileNode(String name)
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
		// If o is a folder, then that folder go first
		if (o instanceof FolderNode) {
			return 1;
		}

		// If o is a file, compare with its name
		else if (o instanceof FileNode) {
			return toString().compareTo(o.toString());
		}

		return 0;
	}

}
