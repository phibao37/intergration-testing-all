/**
 * Simple test for C/C++ test data generation
 * @file SimpleTestDataGenerationTest.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.test.cdt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import sdv.testingall.cdt.gentestdata.CppStaticTestDataGeneration;
import sdv.testingall.cdt.gentestdata.solver.CppZ3SolverFactory;
import sdv.testingall.cdt.loader.CppProjectLoader;
import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.cdt.node.CppFunctionNode;
import sdv.testingall.cdt.util.DefaultCppConfig;
import sdv.testingall.core.gentestdata.GenerationController;
import sdv.testingall.core.logger.ConsoleLogger;
import sdv.testingall.core.node.ProjectNode;

/**
 * Simple test for C/C++ test data generation use static strategy
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class SimpleTestDataGenerationTest {

	static final DefaultCppConfig CONFIG = new DefaultCppConfig();
	static {
		CONFIG.setLogger(new ConsoleLogger());
	}

	@Test
	public void testSimple()
	{
		File root = new File("data-test/cdt/CppTestGen1");
		CppProjectLoader loader = new CppProjectLoader(root);
		loader.setLoaderConfig(CONFIG);

		ProjectNode node = loader.loadProject();
		assertNotNull(node);
		assertEquals(1, node.size());
		CppFileNode file = (CppFileNode) node.get(0);
		assertEquals(1, file.size());

		CppFunctionNode fn = (CppFunctionNode) file.get(0);
		GenerationController testgen = new GenerationController(node, fn, CONFIG);

		testgen.addSolver(new CppZ3SolverFactory());
		testgen.addStraitgy(new CppStaticTestDataGeneration(node, fn, CONFIG));

		testgen.generateData();
	}

}
