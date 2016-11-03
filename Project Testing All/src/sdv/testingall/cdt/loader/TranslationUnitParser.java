/**
 * Parse a C/C++ translation unit
 * @file TranslationUnitParser.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.util.Stack;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.cdt.node.NamespaceNode;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.INode;

/**
 * Parse a C/C++ translation unit
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public class TranslationUnitParser extends ASTVisitor {

	private Stack<INode>	stackNode;
	private ILogger			logger;

	/**
	 * Parse a translation unit to get a full-tree of node
	 * 
	 * @param rootNode
	 *            a file node to be a root of full-tree
	 * @param config
	 *            load configuration
	 */
	public TranslationUnitParser(CppFileNode rootNode, CppLoaderConfig config)
	{
		super(true);
		this.logger = config.getLogger();

		stackNode = new Stack<>();
		stackNode.push(rootNode);
	}

	@Override
	public int visit(IASTDeclaration declaration)
	{
		// TODO Auto-generated method stub
		return PROCESS_CONTINUE;
	}

	@Override
	public int leave(IASTDeclaration declaration)
	{
		// TODO Auto-generated method stub
		return PROCESS_CONTINUE;
	}

	/**
	 * Log all syntax-problem to the logger
	 */
	@Override
	public int visit(IASTProblem problem)
	{
		logger.log(ILogger.ERROR, problem.getMessageWithLocation());
		return PROCESS_SKIP;
	}

	/**
	 * Visit a namespace, add it to stack
	 */
	@Override
	public int visit(ICPPASTNamespaceDefinition namespaceDefinition)
	{
		INode ns = new NamespaceNode(namespaceDefinition.getName().toString());
		stackNode.peek().add(ns);
		stackNode.push(ns);
		return PROCESS_CONTINUE;
	}

	/**
	 * Leave a namespace, pop back from stack
	 */
	@Override
	public int leave(ICPPASTNamespaceDefinition namespaceDefinition)
	{
		stackNode.pop();
		return PROCESS_CONTINUE;
	}

}
