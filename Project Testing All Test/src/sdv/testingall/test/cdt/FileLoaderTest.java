/**
 * Test for loading each C/C++ source code file
 * @file FileLoaderTest.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test.cdt;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import sdv.testingall.cdt.loader.CppFileLoader;
import sdv.testingall.cdt.loader.CppLoaderConfig;
import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.cdt.node.NamespaceNode;
import sdv.testingall.core.logger.ConsoleLogger;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.logger.StringLogger;

/**
 * Test for loading each C/C++ source code file
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public class FileLoaderTest {

	static final CppLoaderConfig CONFIG = new CppLoaderConfig();

	/**
	 * @CHECKPOINT Load an error syntax file
	 * 
	 * @SUCCESS no element node as a child of file node, the logger log an error message
	 */
	@Test
	public void testInvalidFile()
	{
		ILogger logStr = new StringLogger();
		logStr.plug(new ConsoleLogger());
		CONFIG.setLogger(logStr);

		File source = new File("data-test/cdt/ProjectLoader2/Error.cpp");
		CppFileLoader loader = new CppFileLoader(source);
		CppFileNode root = (CppFileNode) loader.loadFile(CONFIG);

		Assert.assertNotNull(root);
		Assert.assertEquals(0, root.size());
		Assert.assertTrue(logStr.toString().length() > 0);
	}

	/**
	 * @CHECKPOINT Load a file with namespace inside
	 * 
	 * @SUCCESS correct tree structure returned
	 */
	@Test
	public void testLoadNamespace()
	{
		CONFIG.setLogger(new ConsoleLogger());

		File source = new File("data-test/cdt/ProjectLoader2/Namespace.cpp");
		CppFileLoader loader = new CppFileLoader(source);
		CppFileNode root = (CppFileNode) loader.loadFile(CONFIG);

		Assert.assertNotNull(root);
		Assert.assertEquals(3, root.size());

		Assert.assertTrue(root.get(0) instanceof NamespaceNode);
		NamespaceNode ns1 = (NamespaceNode) root.get(0);
		Assert.assertEquals(0, ns1.size());
		Assert.assertEquals("ns1", ns1.toString());
		Assert.assertFalse(ns1.isAnonymous());

		Assert.assertTrue(root.get(1) instanceof NamespaceNode);
		NamespaceNode ns2 = (NamespaceNode) root.get(1);
		Assert.assertEquals(1, ns2.size());
		Assert.assertEquals("ns2", ns2.toString());
		Assert.assertFalse(ns2.isAnonymous());

		Assert.assertTrue(ns2.get(0) instanceof NamespaceNode);
		NamespaceNode ns21 = (NamespaceNode) ns2.get(0);
		Assert.assertEquals(0, ns21.size());
		Assert.assertEquals("ns21", ns21.toString());
		Assert.assertFalse(ns21.isAnonymous());

		Assert.assertTrue(root.get(2) instanceof NamespaceNode);
		NamespaceNode ns3 = (NamespaceNode) root.get(2);
		Assert.assertEquals(0, ns3.size());
		Assert.assertEquals("<anonymous>", ns3.toString());
		Assert.assertTrue(ns3.isAnonymous());
	}
}
