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

	private static final Image ICON = new Image(BaseNode.class.getResourceAsStream("/node/project.png")); //$NON-NLS-1$

	private File		mFile;
	private ProjectNode	logicalRoot;

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

	/**
	 * Get the logical data node from the root. <br/>
	 * All physical node such as file, folder will be skipped and all data node inside will be merged together
	 * 
	 * @return root node of logical data
	 */
	public ProjectNode getLogicalRoot()
	{
		if (logicalRoot == null) {
			// TODO Begin merge logical data node
			logicalRoot = this;
			logicalRoot.logicalRoot = logicalRoot;
		}
		return logicalRoot;
	}

}
