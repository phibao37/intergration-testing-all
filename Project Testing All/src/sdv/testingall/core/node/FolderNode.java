/**
 * A node to represent a folder
 * @file FolderNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import javafx.scene.image.Image;

/**
 * A node to represent a folder
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 * @date 2016-11-16 VuSD implement IFileNode
 */
public class FolderNode extends BaseNode implements IFileNode {

	private static final Image ICON = new Image(BaseNode.class.getResourceAsStream("/node/folder.png"));

	private File mFile;

	/**
	 * Create new folder node
	 * 
	 * @param file
	 *            folder object
	 */
	public FolderNode(File file)
	{
		super(file.getName());
		this.mFile = file;
	}

	@Override
	public Image getIcon()
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

	@Override
	public File getFile()
	{
		return mFile;
	}

}
