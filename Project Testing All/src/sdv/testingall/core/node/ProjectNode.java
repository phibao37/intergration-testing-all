/**
 * A node to represent a root of project
 * @file ProjectNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import javafx.scene.image.Image;

/**
 * A node to represent a root of project
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 * @date 2016-11-16 VuSD implement IFileNode
 */
public class ProjectNode extends BaseNode implements IFileNode {

	private static final Image ICON = new Image(BaseNode.class.getResourceAsStream("/node/project.png"));

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
	public Image getIcon()
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
