/**
 * Load a C/C++ project folder
 * @file CppProjectLoader.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.io.File;

import sdv.testingall.core.loader.BaseProjectLoader;
import sdv.testingall.core.node.INode;

/**
 * Load a C/C++ project folder
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class CppProjectLoader extends BaseProjectLoader {

	/**
	 * Load a C/C++ project from a directory
	 * 
	 * @param root
	 *            directory contains C/C++ project
	 */
	public CppProjectLoader(File root)
	{
		super(root);
	}

	@Override
	protected INode loadFileItem(File file)
	{
		return new CppFileLoader(file).loadFile(getLoaderConfig());
	}

	@Override
	public CppLoaderConfig getLoaderConfig()
	{
		return (CppLoaderConfig) super.getLoaderConfig();
	}
}
