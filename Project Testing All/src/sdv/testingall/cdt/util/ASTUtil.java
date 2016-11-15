/**
 * Display a tree of CDT AST
 * @file ASTUtil.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.util;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jdt.annotation.NonNullByDefault;

import sdv.testingall.cdt.loader.CppFileLoader;
import sdv.testingall.cdt.loader.CppLoaderConfig;
import sdv.testingall.core.logger.ConsoleLogger;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.util.SDVUtils;

/**
 * Display a tree of CDT AST
 * 
 * @author VuSD
 *
 * @date 2016-11-04 VuSD created
 */
@NonNullByDefault
public class ASTUtil {

	/**
	 * Print the node structure on the console
	 * 
	 * @param node
	 *            target node
	 * @param margin
	 *            margin position to print
	 */
	public static void printTree(IASTNode node, String margin)
	{
		// [SAFE_CHECKED] getRawSignature() //
		String content = node.getRawSignature().replaceAll("\\s", " ");
		String attr = node.getClass().getSimpleName();
		if (content.length() > 40) {
			content = content.substring(0, 37) + "...";
		}

		if (node instanceof IASTDeclSpecifier) {
			switch (((IASTDeclSpecifier) node).getStorageClass()) {
			case IASTDeclSpecifier.sc_typedef:
				attr += "-typedef";
				break;
			case IASTDeclSpecifier.sc_extern:
				attr += "-extern";
				break;
			case IASTDeclSpecifier.sc_static:
				attr += "-static";
				break;
			case IASTDeclSpecifier.sc_auto:
				attr += "-auto";
				break;
			case IASTDeclSpecifier.sc_register:
				attr += "-register";
				break;
			case IASTDeclSpecifier.sc_mutable:
				attr += "-mutable";
				break;
			}
		}

		System.out.printf("%s%s [%s]%n", margin, content, attr);

		for (IASTNode child : node.getChildren()) {
			printTree(child, margin + "   ");
		}
	}

	public static void main(String[] args)
	{
		CppLoaderConfig config = new CppLoaderConfig();
		config.setLogger(new ConsoleLogger());

		try {
			File source = new File("data-test/ASTView.cpp");
			IASTTranslationUnit u = CppFileLoader.getTranslationUnit(source, config, true);
			printTree(u, "-");
		} catch (Exception e) {
			e.printStackTrace();
			config.getLogger().log(ILogger.ERROR, SDVUtils.gxceptionMsg(e));
		}
	}
}
