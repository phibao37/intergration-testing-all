/**
 * Load a C/C++ source file
 * @file CppFileLoader.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.io.File;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.ASTCommenter;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.INode;
import sdv.testingall.util.SDVUtils;

/**
 * Load a C/C++ source file
 * 
 * @author VuSD
 *
 * @date 2016-10-27 VuSD created
 */
@NonNullByDefault
public class CppFileLoader {

	private final File source;

	/**
	 * Create a C/C++ source file loader
	 * 
	 * @param source
	 *            C/C++ source code file
	 */
	public CppFileLoader(File source)
	{
		this.source = source;
	}

	/**
	 * Load a source file with specified configuration
	 * 
	 * @param config
	 *            configuration to load file
	 * @return a file node contains the full-tree loaded component inside
	 */
	public @Nullable INode loadFile(@Nullable CppLoaderConfig config)
	{
		if (config == null) {
			return null;
		}

		boolean isC = false, isCpp = false;
		String lowerName = source.getName().toLowerCase(Locale.ENGLISH);

		for (String cppExt : config.getListCppExt()) {
			if (lowerName.endsWith(cppExt)) {
				isCpp = true;
				break;
			}
		}

		if (!isCpp) {
			for (String cExt : config.getListCExt()) {
				if (lowerName.endsWith(cExt)) {
					isC = true;
					break;
				}
			}
		}

		return isCpp || isC ? loadSourceCode(config, isCpp) : null;
	}

	/**
	 * Get the translation unit from source code
	 * 
	 * @param source
	 *            source code object
	 * @param config
	 *            loader configuration
	 * @param isCpp
	 *            is this a C++ source file
	 * @return translation unit node
	 * @throws Exception
	 *             error during load source code
	 */
	public static IASTTranslationUnit getTranslationUnit(File source, CppLoaderConfig config, boolean isCpp)
			throws Exception
	{
		AbstractLanguage lang = isCpp ? GPPLanguage.getDefault() : GCCLanguage.getDefault();
		char[] chars = FileUtils.readFileToString(source, config.getFileCharset()).toCharArray();
		FileContent content = FileContent.create(source.getAbsolutePath(), chars);
		IScannerInfo scanInfo = new ScannerInfo(config.getMarcoMap(), config.getIncludeDirs());
		IncludeFileContentProvider fileCreator = IncludeFileContentProvider.getSavedFilesProvider();
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();

		return lang.getASTTranslationUnit(content, scanInfo, fileCreator, null, options, log);
	}

	@Nullable
	private INode loadSourceCode(CppLoaderConfig config, boolean isCpp)
	{
		try {
			CppFileNode fileNode = new CppFileNode(source.getName(), isCpp);
			IASTTranslationUnit u = getTranslationUnit(source, config, isCpp);
			NodeCommentMap commentMap = ASTCommenter.getCommentedNodeMap(u);

			u.accept(new TranslationUnitParser(fileNode, config, commentMap));
			return fileNode;
		} catch (Exception e) {
			config.getLogger().log(ILogger.ERROR, "Error loading file %s: %s\n", source.getPath(),
					SDVUtils.gxceptionMsg(e));
			return null;
		}

	}
}
