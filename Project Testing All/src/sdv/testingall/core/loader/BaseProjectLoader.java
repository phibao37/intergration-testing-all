/**
 * Base project loader task
 * @file BaseProjectLoader.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.loader;

import java.io.File;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.node.FolderNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.ProjectNode;

/**
 * Base project loader task that parse project tree from root, folder, sub-folder and file
 * 
 * @author VuSD
 *
 * @date 2016-10-25 VuSD created
 */
@NonNullByDefault
public abstract class BaseProjectLoader {

	private final File				root;
	private @Nullable ILoaderConfig	config;

	/**
	 * Create new project loader from specified root
	 * 
	 * @param rootFile
	 *            a folder (in case load all file inside folder) or a project setting file
	 */
	public BaseProjectLoader(File rootFile)
	{
		root = rootFile;
	}

	/**
	 * Load the project with the configuration specified
	 * 
	 * @return the root node (project node) after load complete
	 */
	public ProjectNode loadProject()
	{
		ProjectNode rootNode = new ProjectNode(root);
		recurseLoadProject(root, rootNode);
		return rootNode;
	}

	/**
	 * Load the project component from top root to bottom
	 * 
	 * @param parent
	 *            the folder/file to be load
	 * @param parentNode
	 *            parent node to be add component
	 */
	protected void recurseLoadProject(File parent, INode parentNode)
	{
		for (File child : parent.listFiles()) {
			INode childNode;

			if (child.isFile()) {
				childNode = loadFileItem(child);
			} else {
				childNode = loadDirItem(child);
				recurseLoadProject(child, childNode);
			}

			if (childNode != null) {
				parentNode.add(childNode);
			}
		}
	}

	/**
	 * Load the file
	 * 
	 * @param file
	 *            file path to be load
	 * @return the node object after load the file or null if this file is ignored
	 */
	protected abstract @Nullable INode loadFileItem(File file);

	/**
	 * Get the folder node
	 * 
	 * @param folder
	 *            folder folder path to be load
	 * @return the node object corresponding to the folder or null if this folder is ignored
	 */
	protected INode loadDirItem(File folder)
	{
		return new FolderNode(folder);
	}

	/**
	 * Return the root folder or file (example Visual Studio / Eclipse project file)
	 * 
	 * @return the root file/folder
	 */
	public File getRoot()
	{
		return root;
	}

	/**
	 * @return the loader configuration
	 */
	@Nullable
	public ILoaderConfig getLoaderConfig()
	{
		return config;
	}

	/**
	 * Set the loader configuration
	 * 
	 * @param config
	 *            the configuration used during load task
	 */
	public void setLoaderConfig(ILoaderConfig config)
	{
		this.config = config;
	}

}
