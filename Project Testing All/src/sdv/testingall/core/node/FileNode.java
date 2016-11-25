/**
 * A node to represent a file
 * @file FileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import javafx.scene.image.Image;

/**
 * A node to represent a file
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 * @date 2016-11-16 VuSD implement IFileNode
 */
public class FileNode extends BaseNode implements IFileNode {

	private static final Image ICON = new Image(BaseNode.class.getResourceAsStream("/node/text-file.png")); //$NON-NLS-1$

	private File mFile;

	/**
	 * Create new file node
	 * 
	 * @param file
	 *            file object
	 */
	public FileNode(File file)
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

	@Override
	public File getFile()
	{
		return mFile;
	}

}
