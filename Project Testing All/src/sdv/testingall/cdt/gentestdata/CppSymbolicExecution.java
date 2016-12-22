/**
 * C/C++ symbolic execution
 * @file CppSymbolicExecution.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gentestdata;

import sdv.testingall.core.gentestdata.IGenTestConfig;
import sdv.testingall.core.gentestdata.symbolicexec.SymbolicExecution;
import sdv.testingall.core.node.FunctionNode;
import sdv.testingall.core.node.ProjectNode;
import sdv.testingall.core.statement.ITestPath;

/**
 * C/C++ symbolic execution
 * 
 * @author VuSD
 *
 * @date 2016-12-22 VuSD created
 */
public class CppSymbolicExecution extends SymbolicExecution {

	/**
	 * Create new symbolic execution controller for C/C++
	 * 
	 * @param project
	 *            root of project, use to resolve binding, find reference, ...
	 * @param function
	 *            function node need to generate
	 * @param config
	 *            configuration during test
	 * @param testpath
	 *            test path to analyze
	 */
	public CppSymbolicExecution(ProjectNode project, FunctionNode function, IGenTestConfig config, ITestPath testpath)
	{
		super(project, function, config, testpath);
	}

}
