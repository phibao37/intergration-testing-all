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
import sdv.testingall.cdt.node.ComplexTypeNode;
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

	/**
	 * @CHECKPOINT Load a file with composite type inside
	 * 
	 * @SUCCESS correct tree structure returned
	 */
	@Test
	public void testLoadComplex()
	{
		CONFIG.setLogger(new ConsoleLogger());

		File source = new File("data-test/cdt/ProjectLoader2/Composite.cpp");
		CppFileLoader loader = new CppFileLoader(source);
		CppFileNode root = (CppFileNode) loader.loadFile(CONFIG);

		Assert.assertNotNull(root);
		Assert.assertEquals(7, root.size());

		{
			Assert.assertTrue(root.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(0);
			Assert.assertEquals(0, cp.size());
			Assert.assertEquals("MyStruct", cp.toString());
			Assert.assertEquals(ComplexTypeNode.STRUCT, cp.getType());
		}

		{
			Assert.assertTrue(root.get(1) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(1);
			Assert.assertEquals(0, cp.size());
			Assert.assertEquals("MyUnion", cp.toString());
			Assert.assertEquals(ComplexTypeNode.UNION, cp.getType());
		}

		{
			Assert.assertTrue(root.get(2) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(2);
			Assert.assertEquals(0, cp.size());
			Assert.assertEquals("MyClass", cp.toString());
			Assert.assertEquals(ComplexTypeNode.CLASS, cp.getType());
		}

		Assert.assertTrue(root.get(3) instanceof NamespaceNode);
		NamespaceNode ns1 = (NamespaceNode) root.get(3);
		Assert.assertEquals(3, ns1.size());
		Assert.assertEquals("ns1", ns1.toString());
		Assert.assertFalse(ns1.isAnonymous());

		{
			Assert.assertTrue(ns1.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) ns1.get(0);
			Assert.assertEquals(0, cp.size());
			Assert.assertEquals("MyStruct2", cp.toString());
			Assert.assertEquals(ComplexTypeNode.STRUCT, cp.getType());
		}

		{
			Assert.assertTrue(ns1.get(1) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) ns1.get(1);
			Assert.assertEquals(0, cp.size());
			Assert.assertEquals("MyUnion2", cp.toString());
			Assert.assertEquals(ComplexTypeNode.UNION, cp.getType());
		}

		{
			Assert.assertTrue(ns1.get(2) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) ns1.get(2);
			Assert.assertEquals(0, cp.size());
			Assert.assertEquals("MyClass2", cp.toString());
			Assert.assertEquals(ComplexTypeNode.CLASS, cp.getType());
		}

		{
			Assert.assertTrue(root.get(4) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(4);
			Assert.assertEquals(1, cp.size());
			Assert.assertEquals("MyStruct3", cp.toString());
			Assert.assertEquals(ComplexTypeNode.STRUCT, cp.getType());

			Assert.assertTrue(cp.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp1 = (ComplexTypeNode) cp.get(0);
			Assert.assertEquals(0, cp1.size());
			Assert.assertEquals("MyStruct4", cp1.toString());
			Assert.assertEquals(ComplexTypeNode.STRUCT, cp1.getType());
		}

		{
			Assert.assertTrue(root.get(5) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(5);
			Assert.assertEquals(1, cp.size());
			Assert.assertEquals("MyUnion3", cp.toString());
			Assert.assertEquals(ComplexTypeNode.UNION, cp.getType());

			Assert.assertTrue(cp.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp1 = (ComplexTypeNode) cp.get(0);
			Assert.assertEquals(0, cp1.size());
			Assert.assertEquals("MyUnion4", cp1.toString());
			Assert.assertEquals(ComplexTypeNode.UNION, cp1.getType());
		}

		{
			Assert.assertTrue(root.get(6) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(6);
			Assert.assertEquals(1, cp.size());
			Assert.assertEquals("MyClass3", cp.toString());
			Assert.assertEquals(ComplexTypeNode.CLASS, cp.getType());

			Assert.assertTrue(cp.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp1 = (ComplexTypeNode) cp.get(0);
			Assert.assertEquals(0, cp1.size());
			Assert.assertEquals("MyClass4", cp1.toString());
			Assert.assertEquals(ComplexTypeNode.CLASS, cp1.getType());
		}

	}
}
