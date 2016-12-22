/**
 * Static test generation for C/C++
 * @file CppStaticTestDataGeneration.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gentestdata;

import sdv.testingall.core.gentestdata.IGenTestConfig;
import sdv.testingall.core.gentestdata.StaticTestDataGeneration;
import sdv.testingall.core.gentestdata.symbolicexec.ISymbolicExecution;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.statement.ITestPath;

/**
 * Static test generation for C/C++
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class CppStaticTestDataGeneration extends StaticTestDataGeneration {

	/**
	 * Create new static test generation object for C/C++
	 * 
	 * @param project
	 *            root of project, use to resolve binding, find reference, ...
	 * @param function
	 *            function node need to generate
	 * @param config
	 *            configuration during test
	 */
	public CppStaticTestDataGeneration(ProjectNode project, FunctionNode function, IGenTestConfig config)
	{
		super(project, function, config);
	}

	@Override
	public ISymbolicExecution createSymbolicExecution(ITestPath testpath)
	{
		return new CppSymbolicExecution(getRootProject(), getFunction(), getConfig(), testpath);
	}

}
