/**
 * C/C++ symbolic execution
 * @file CppSymbolicExecution.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gentestdata;

import sdv.testingall.cdt.expression.CppBinaryExpression;
import sdv.testingall.cdt.expression.CppUnaryExpression;
import sdv.testingall.core.expression.IBinaryExpression;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.IUnaryExpression;
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

	@Override
	public IBinaryExpression createBinary(IExpression left, String operator, IExpression right)
	{
		return new CppBinaryExpression(left, operator, right);
	}

	@Override
	public IUnaryExpression createUnary(IExpression child, String operator, boolean leftSide)
	{
		return new CppUnaryExpression(child, operator, leftSide);
	}

}
