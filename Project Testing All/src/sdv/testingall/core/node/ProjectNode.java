/**
 * A node to represent a root of project
 * @file ProjectNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A node to represent a root of project
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
public class ProjectNode extends INode {

	private static final ImageIcon ICON = new ImageIcon(ImageIcon.class.getResource("/node/project.png"));

	/**
	 * Create new project node (root of project)
	 * 
	 * @param projectName
	 *            project name
	 */
	public ProjectNode(String projectName)
	{
		super(projectName);
	}

	@Override
	public Icon getIcon()
	{
		return ICON;
	}

}
