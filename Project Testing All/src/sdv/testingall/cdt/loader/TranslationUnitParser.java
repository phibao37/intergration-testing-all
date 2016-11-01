/**
 * Parse a C/C++ translation unit
 * @file TranslationUnitParser.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTProblem;

import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.core.logger.ILogger;

/**
 * Parse a C/C++ translation unit
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
public class TranslationUnitParser extends ASTVisitor {

	private CppFileNode	rootNode;
	private ILogger		logger;

	public TranslationUnitParser(CppFileNode rootNode, CppLoaderConfig config)
	{
		super(true);
		this.rootNode = rootNode;
		this.logger = config.getLogger();
	}

	@Override
	public int visit(IASTProblem problem)
	{
		logger.log(ILogger.ERROR, problem.getMessageWithLocation());
		return PROCESS_SKIP;
	}

}
