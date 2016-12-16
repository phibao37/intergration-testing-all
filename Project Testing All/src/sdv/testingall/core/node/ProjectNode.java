/**
 * A node to represent a root of project
 * @file ProjectNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.io.File;

import org.eclipse.jdt.annotation.Nullable;

import javafx.scene.image.Image;
import sdv.testingall.core.type.IType;

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
	protected ProjectNode getLogicalRoot()
	{
		if (logicalRoot == null) {
			// TODO Begin merge logical data node
			logicalRoot = this;
			logicalRoot.logicalRoot = logicalRoot;
		}
		return logicalRoot;
	}

	/**
	 * Resolve type to data node
	 * 
	 * @param type
	 *            type to find target binding
	 * @param loc
	 *            location that this type occur, use to find with relative location
	 * @return corresponding data node or <code>null</code> if this type does not bind to a data node, or data node not
	 *         found
	 */
	public @Nullable INode resolve(IType type, INode loc)
	{
		return null;
	}

}
