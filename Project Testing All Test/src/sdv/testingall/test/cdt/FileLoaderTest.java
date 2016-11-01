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
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.test.ConsoleLogger;
import sdv.testingall.test.StringLogger;

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

}
