/**
 * Test for loading each C/C++ source code file
 * @file FileLoaderTest.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test.cdt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import sdv.testingall.cdt.loader.CppFileLoader;
import sdv.testingall.cdt.loader.CppLoaderConfig;
import sdv.testingall.cdt.node.ComplexTypeNode;
import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.cdt.node.CppVariableNode;
import sdv.testingall.cdt.node.NamespaceNode;
import sdv.testingall.cdt.type.CppBasicType;
import sdv.testingall.cdt.type.CppTypeModifier;
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

		assertNotNull(root);
		assertEquals(0, root.size());
		assertTrue(logStr.toString().length() > 0);
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

		assertNotNull(root);
		assertEquals(3, root.size());

		assertTrue(root.get(0) instanceof NamespaceNode);
		NamespaceNode ns1 = (NamespaceNode) root.get(0);
		assertEquals(0, ns1.size());
		assertEquals("ns1", ns1.toString());
		assertFalse(ns1.isAnonymous());

		assertTrue(root.get(1) instanceof NamespaceNode);
		NamespaceNode ns2 = (NamespaceNode) root.get(1);
		assertEquals(1, ns2.size());
		assertEquals("ns2", ns2.toString());
		assertFalse(ns2.isAnonymous());

		assertTrue(ns2.get(0) instanceof NamespaceNode);
		NamespaceNode ns21 = (NamespaceNode) ns2.get(0);
		assertEquals(0, ns21.size());
		assertEquals("ns21", ns21.toString());
		assertFalse(ns21.isAnonymous());

		assertTrue(root.get(2) instanceof NamespaceNode);
		NamespaceNode ns3 = (NamespaceNode) root.get(2);
		assertEquals(0, ns3.size());
		assertEquals("<anonymous>", ns3.toString());
		assertTrue(ns3.isAnonymous());
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

		assertNotNull(root);
		assertEquals(7, root.size());

		{
			assertTrue(root.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(0);
			assertEquals(0, cp.size());
			assertEquals("MyStruct", cp.toString());
			assertEquals(ComplexTypeNode.STRUCT, cp.getType());
		}

		{
			assertTrue(root.get(1) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(1);
			assertEquals(0, cp.size());
			assertEquals("MyUnion", cp.toString());
			assertEquals(ComplexTypeNode.UNION, cp.getType());
		}

		{
			assertTrue(root.get(2) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(2);
			assertEquals(0, cp.size());
			assertEquals("MyClass", cp.toString());
			assertEquals(ComplexTypeNode.CLASS, cp.getType());
		}

		assertTrue(root.get(3) instanceof NamespaceNode);
		NamespaceNode ns1 = (NamespaceNode) root.get(3);
		assertEquals(3, ns1.size());
		assertEquals("ns1", ns1.toString());
		assertFalse(ns1.isAnonymous());

		{
			assertTrue(ns1.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) ns1.get(0);
			assertEquals(0, cp.size());
			assertEquals("MyStruct2", cp.toString());
			assertEquals(ComplexTypeNode.STRUCT, cp.getType());
		}

		{
			assertTrue(ns1.get(1) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) ns1.get(1);
			assertEquals(0, cp.size());
			assertEquals("MyUnion2", cp.toString());
			assertEquals(ComplexTypeNode.UNION, cp.getType());
		}

		{
			assertTrue(ns1.get(2) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) ns1.get(2);
			assertEquals(0, cp.size());
			assertEquals("MyClass2", cp.toString());
			assertEquals(ComplexTypeNode.CLASS, cp.getType());
		}

		{
			assertTrue(root.get(4) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(4);
			assertEquals(1, cp.size());
			assertEquals("MyStruct3", cp.toString());
			assertEquals(ComplexTypeNode.STRUCT, cp.getType());

			assertTrue(cp.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp1 = (ComplexTypeNode) cp.get(0);
			assertEquals(0, cp1.size());
			assertEquals("MyStruct4", cp1.toString());
			assertEquals(ComplexTypeNode.STRUCT, cp1.getType());
		}

		{
			assertTrue(root.get(5) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(5);
			assertEquals(1, cp.size());
			assertEquals("MyUnion3", cp.toString());
			assertEquals(ComplexTypeNode.UNION, cp.getType());

			assertTrue(cp.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp1 = (ComplexTypeNode) cp.get(0);
			assertEquals(0, cp1.size());
			assertEquals("MyUnion4", cp1.toString());
			assertEquals(ComplexTypeNode.UNION, cp1.getType());
		}

		{
			assertTrue(root.get(6) instanceof ComplexTypeNode);
			ComplexTypeNode cp = (ComplexTypeNode) root.get(6);
			assertEquals(1, cp.size());
			assertEquals("MyClass3", cp.toString());
			assertEquals(ComplexTypeNode.CLASS, cp.getType());

			assertTrue(cp.get(0) instanceof ComplexTypeNode);
			ComplexTypeNode cp1 = (ComplexTypeNode) cp.get(0);
			assertEquals(0, cp1.size());
			assertEquals("MyClass4", cp1.toString());
			assertEquals(ComplexTypeNode.CLASS, cp1.getType());
		}

	}

	/**
	 * @CHECKPOINT Load a file with variable declare/define
	 * 
	 * @SUCCESS correct tree structure returned
	 */
	@Test
	public void testLoadSimpleVariable()
	{
		CONFIG.setLogger(new ConsoleLogger());
		final int ROOT_SIZE = 6;

		File source = new File("data-test/cdt/ProjectLoader2/Variable.cpp");
		CppFileLoader loader = new CppFileLoader(source);
		CppFileNode root = (CppFileNode) loader.loadFile(CONFIG);

		assertNotNull(root);
		assertEquals(ROOT_SIZE, root.size());

		// Global variable 1
		{
			assertTrue(root.get(0) instanceof CppVariableNode);
			CppVariableNode var = (CppVariableNode) root.get(0);
			assertEquals(0, var.size());
			assertEquals("a", var.getName()); // OPT *
			assertFalse(var.isDeclare()); // OPT 1

			assertTrue(var.getType() instanceof CppBasicType);
			CppBasicType type = (CppBasicType) var.getType();
			assertFalse(type.hasBind());
			assertEquals("int", type.getName()); // OPT 2
			assertEquals(CppBasicType.INT, type.getType()); // OPT 2
			assertFalse(type.isSigned()); // OPT 3
			assertFalse(type.isUnsigned()); // OPT 3
			assertFalse(type.isShort()); // OPT 3
			assertFalse(type.isLong()); // OPT 3
			assertFalse(type.isLongLong()); // OPT 3

			assertTrue(type.getTypeModifier() instanceof CppTypeModifier);
			CppTypeModifier mdf = (CppTypeModifier) type.getTypeModifier();
			assertEquals(0, mdf.getPointerLevel()); // OPT 4
			assertFalse(mdf.isConst()); // OPT 5
			assertFalse(mdf.isStatic()); // OPT 6
			assertFalse(mdf.isReference()); // OPT 7
		}

		// Global variable 2, OPT 4
		{
			assertTrue(root.get(1) instanceof CppVariableNode);
			CppVariableNode var = (CppVariableNode) root.get(1);
			assertEquals(0, var.size());
			assertEquals("b", var.getName());
			assertFalse(var.isDeclare());

			assertTrue(var.getType() instanceof CppBasicType);
			CppBasicType type = (CppBasicType) var.getType();
			assertFalse(type.hasBind());
			assertEquals("int", type.getName());
			assertEquals(CppBasicType.INT, type.getType());
			assertFalse(type.isSigned());
			assertFalse(type.isUnsigned());
			assertFalse(type.isShort());
			assertFalse(type.isLong());
			assertFalse(type.isLongLong());

			assertTrue(type.getTypeModifier() instanceof CppTypeModifier);
			CppTypeModifier mdf = (CppTypeModifier) type.getTypeModifier();
			assertEquals(1, mdf.getPointerLevel());
			assertFalse(mdf.isConst());
			assertFalse(mdf.isStatic());
			assertFalse(mdf.isReference());
		}

		// Global variable 3, OPT 7
		{
			assertTrue(root.get(2) instanceof CppVariableNode);
			CppVariableNode var = (CppVariableNode) root.get(2);
			assertEquals(0, var.size());
			assertEquals("c", var.getName());
			assertFalse(var.isDeclare());

			assertTrue(var.getType() instanceof CppBasicType);
			CppBasicType type = (CppBasicType) var.getType();
			assertFalse(type.hasBind());
			assertEquals("int", type.getName());
			assertEquals(CppBasicType.INT, type.getType());
			assertFalse(type.isSigned());
			assertFalse(type.isUnsigned());
			assertFalse(type.isShort());
			assertFalse(type.isLong());
			assertFalse(type.isLongLong());

			assertTrue(type.getTypeModifier() instanceof CppTypeModifier);
			CppTypeModifier mdf = (CppTypeModifier) type.getTypeModifier();
			assertEquals(0, mdf.getPointerLevel());
			assertFalse(mdf.isConst());
			assertFalse(mdf.isStatic());
			assertTrue(mdf.isReference());
		}

		// Global variable 4, OPT 1-2-3
		{
			assertTrue(root.get(3) instanceof CppVariableNode);
			CppVariableNode var = (CppVariableNode) root.get(3);
			assertEquals(0, var.size());
			assertEquals("cc", var.getName()); // OPT *
			assertTrue(var.isDeclare()); // OPT 1

			assertTrue(var.getType() instanceof CppBasicType);
			CppBasicType type = (CppBasicType) var.getType();
			assertFalse(type.hasBind());
			assertEquals("extern UCHAR", type.getName()); // OPT 2
			assertEquals(CppBasicType.CHAR, type.getType()); // OPT 2
			assertFalse(type.isSigned()); // OPT 3
			assertTrue(type.isUnsigned()); // OPT 3
			assertFalse(type.isShort()); // OPT 3
			assertFalse(type.isLong()); // OPT 3
			assertFalse(type.isLongLong()); // OPT 3

			assertTrue(type.getTypeModifier() instanceof CppTypeModifier);
			CppTypeModifier mdf = (CppTypeModifier) type.getTypeModifier();
			assertEquals(0, mdf.getPointerLevel()); // OPT 4
			assertFalse(mdf.isConst()); // OPT 5
			assertFalse(mdf.isStatic()); // OPT 6
			assertFalse(mdf.isReference()); // OPT 7
		}

		// Global variable 5, OPT 2-3-5
		{
			assertTrue(root.get(4) instanceof CppVariableNode);
			CppVariableNode var = (CppVariableNode) root.get(4);
			assertEquals(0, var.size());
			assertEquals("s", var.getName()); // OPT *
			assertFalse(var.isDeclare()); // OPT 1

			assertTrue(var.getType() instanceof CppBasicType);
			CppBasicType type = (CppBasicType) var.getType();
			assertFalse(type.hasBind());
			assertEquals("const signed", type.getName()); // OPT 2
			assertEquals(CppBasicType.INT, type.getType()); // OPT 2
			assertTrue(type.isSigned()); // OPT 3
			assertFalse(type.isUnsigned()); // OPT 3
			assertFalse(type.isShort()); // OPT 3
			assertFalse(type.isLong()); // OPT 3
			assertFalse(type.isLongLong()); // OPT 3

			assertTrue(type.getTypeModifier() instanceof CppTypeModifier);
			CppTypeModifier mdf = (CppTypeModifier) type.getTypeModifier();
			assertEquals(0, mdf.getPointerLevel()); // OPT 4
			assertTrue(mdf.isConst()); // OPT 5
			assertFalse(mdf.isStatic()); // OPT 6
			assertFalse(mdf.isReference()); // OPT 7
		}

		// Global variable 6, OPT 3-6
		{
			assertTrue(root.get(5) instanceof CppVariableNode);
			CppVariableNode var = (CppVariableNode) root.get(5);
			assertEquals(0, var.size());
			assertEquals("ll", var.getName()); // OPT *
			assertFalse(var.isDeclare()); // OPT 1

			assertTrue(var.getType() instanceof CppBasicType);
			CppBasicType type = (CppBasicType) var.getType();
			assertFalse(type.hasBind());
			assertEquals("static long long", type.getName()); // OPT 2
			assertEquals(CppBasicType.INT, type.getType()); // OPT 2
			assertFalse(type.isSigned()); // OPT 3
			assertFalse(type.isUnsigned()); // OPT 3
			assertFalse(type.isShort()); // OPT 3
			assertFalse(type.isLong()); // OPT 3
			assertTrue(type.isLongLong()); // OPT 3

			assertTrue(type.getTypeModifier() instanceof CppTypeModifier);
			CppTypeModifier mdf = (CppTypeModifier) type.getTypeModifier();
			assertEquals(0, mdf.getPointerLevel()); // OPT 4
			assertFalse(mdf.isConst()); // OPT 5
			assertTrue(mdf.isStatic()); // OPT 6
			assertFalse(mdf.isReference()); // OPT 7
		}

	}

}
