/**
 * Test for loading C/C++ project
 * @file ProjectLoaderTest.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test.cdt;

import java.io.File;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import sdv.testingall.cdt.loader.CppLoaderConfig;
import sdv.testingall.cdt.loader.CppProjectLoader;
import sdv.testingall.core.node.FileNode;
import sdv.testingall.core.node.FolderNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.test.ConsoleLogger;

/**
 * Test for loading C/C++ project
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class ProjectLoaderTest {

	static final CppLoaderConfig	CONFIG			= new CppLoaderConfig();
	static final Comparator<INode>	NODE_COMPARE	= (o1, o2) -> o1.compareTo(o2);

	static {
		CONFIG.setLogger(new ConsoleLogger());
	}

	/**
	 * @CHECKPOINT Load an empty project
	 * 
	 * @SUCCESS no element node as a child of project node
	 */
	@Test
	public void testEmptyProject()
	{
		File root = new File("data-test/cdt/ProjectLoaderEmpty");
		CppProjectLoader loader = new CppProjectLoader(root);
		loader.setLoaderConfig(CONFIG);

		ProjectNode node = loader.loadProject();
		Assert.assertNotNull(node);
		Assert.assertEquals(0, node.size());
	}

	/**
	 * @CHECKPOINT Load a general project
	 * 
	 * @SUCCESS correct structure returned
	 */
	@Test
	public void testLoadProject1()
	{
		File root = new File("data-test/cdt/ProjectLoader1");
		CppProjectLoader loader = new CppProjectLoader(root);
		loader.setLoaderConfig(CONFIG);

		ProjectNode node = loader.loadProject();
		Assert.assertNotNull(node);
		Assert.assertEquals(2, node.size());

		node.sort(NODE_COMPARE);
		FolderNode node1 = (FolderNode) node.get(0);
		FileNode node2 = (FileNode) node.get(1);

		Assert.assertEquals("B", node1.toString());
		Assert.assertEquals("A.cpp", node2.toString());

		Assert.assertEquals(1, node1.size());
		FileNode node11 = (FileNode) node1.get(0);
		Assert.assertEquals("C.cpp", node11.toString());
	}

}
