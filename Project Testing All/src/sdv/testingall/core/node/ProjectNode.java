/**
 * A node to represent a root of project
 * @file ProjectNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A node to represent a root of project
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 * @date 2016-11-16 VuSD implement IFileNode
 */
public class ProjectNode extends BaseNode implements IFileNode {

	private static final ImageIcon ICON = new ImageIcon(ImageIcon.class.getResource("/node/project.png"));

	private File mFile;

	/**
	 * Create new project node (root of project)
	 * 
	 * @param file
	 *            project root file
	 */
	public ProjectNode(File file)
	{
		super(file.getName());
		this.mFile = file;
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

	@Override
	public File getFile()
	{
		return mFile;
	}

}
