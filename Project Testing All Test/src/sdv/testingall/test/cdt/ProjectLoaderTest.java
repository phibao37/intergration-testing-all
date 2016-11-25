/**
 * Test for loading C/C++ project
 * @file ProjectLoaderTest.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test.cdt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Comparator;

import org.junit.Test;

import sdv.testingall.cdt.loader.DefaultCppLoaderConfig;
import sdv.testingall.cdt.loader.CppProjectLoader;
import sdv.testingall.cdt.loader.ICppLoaderConfig;
import sdv.testingall.core.logger.ConsoleLogger;
import sdv.testingall.core.node.FileNode;
import sdv.testingall.core.node.FolderNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.ProjectNode;

/**
 * Test for loading C/C++ project
 * 
 * @author VuSD
 *
 * @date 2016-10-31 VuSD created
 */
public class ProjectLoaderTest {

	static final ICppLoaderConfig	CONFIG			= new DefaultCppLoaderConfig();
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
		assertNotNull(node);
		assertEquals(0, node.size());
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
		assertNotNull(node);
		assertEquals(2, node.size());

		node.sort(NODE_COMPARE);
		FolderNode node1 = (FolderNode) node.get(0);
		FileNode node2 = (FileNode) node.get(1);

		assertEquals("B", node1.toString());
		assertEquals("A.cpp", node2.toString());

		assertEquals(1, node1.size());
		FileNode node11 = (FileNode) node1.get(0);
		assertEquals("C.cpp", node11.toString());
	}

}
