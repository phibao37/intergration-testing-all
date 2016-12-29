/**
 * Parse a C/C++ translation unit
 * @file TranslationUnitParser.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.gnu.c.ICASTKnRFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousDeclarator;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import javafx.util.Pair;
import sdv.testingall.cdt.expression.CppConverter;
import sdv.testingall.cdt.expression.CppNameExpression;
import sdv.testingall.cdt.node.ComplexTypeNode;
import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.cdt.node.CppFunctionNode;
import sdv.testingall.cdt.node.CppVariableNode;
import sdv.testingall.cdt.node.NamespaceNode;
import sdv.testingall.cdt.type.CppBasicType;
import sdv.testingall.cdt.type.CppNamedType;
import sdv.testingall.cdt.type.CppTypeModifier;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.IInsideFileNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.node.VariableNode;
import sdv.testingall.core.type.IType;
import sdv.testingall.core.type.ITypeModifier;

/**
 * Parse a C/C++ translation unit.
 * 
 * @note TODO parse as IASTName.resolveBinding() to IType for faster parser
 * 
 * @author VuSD
 *
 * @date 2016-11-01 VuSD created
 */
@NonNullByDefault
public class TranslationUnitParser extends ASTVisitor {

	private final Stack<Pair<INode, @Nullable IASTDeclaration>> stackNode;

	private final ICppLoaderConfig	config;
	private final NodeCommentMap	commentMap;

	/**
	 * Parse a translation unit to get a full-tree of node
	 * 
	 * @param rootNode
	 *            a file node to be a root of full-tree
	 * @param config
	 *            load configuration
	 * @param commentMap
	 *            map from node to list of comment
	 */
	public TranslationUnitParser(CppFileNode rootNode, ICppLoaderConfig config, NodeCommentMap commentMap)
	{
		super(true);
		this.config = config;
		this.commentMap = commentMap;

		stackNode = new Stack<>();
		stackNode.push(new Pair<>(rootNode, null));
	}

	/**
	 * Add a node to last parent node in a stack
	 * 
	 * @param node
	 *            node to be add
	 * @param astNode
	 *            corresponding AST node
	 * @param findComment
	 *            looking up for comment assigned with AST node
	 */
	private void addToCurrentStack(IInsideFileNode node, IASTNode astNode, boolean findComment)
	{
		stackNode.peek().getKey().add(node);
		node.setIsPartOfSource(astNode.isPartOfTranslationUnitFile());
		if (!findComment) {
			return;
		}

		List<IASTComment> list = commentMap.getLeadingCommentsForNode(astNode);
		if (list.isEmpty()) {
			return;
		}

		StringBuilder build = new StringBuilder();
		build.append(list.get(0).getComment());

		for (int i = 1; i < list.size(); i++) {
			build.append('\n').append(list.get(i).getComment());
		}
		node.setDescription(build.toString());
	}

	@Override
	public int visit(@Nullable IASTDeclaration declaration)
	{
		if (declaration instanceof IASTFunctionDefinition) {
			IASTFunctionDefinition fnDef = (IASTFunctionDefinition) declaration;
			IASTDeclSpecifier decType = fnDef.getDeclSpecifier();
			IASTFunctionDeclarator fnDecA = fnDef.getDeclarator();
			IASTName fnName = fnDecA.getName();
			CppTypeModifier mdf = parseBaseModifier(decType);
			IType type = parseInlineType(decType, mdf);
			if (fnDecA instanceof ICASTKnRFunctionDeclarator) {
				config.getLogger().log(ILogger.ERROR, config.resString("loader.kr_support"), fnName); //$NON-NLS-1$
				return PROCESS_SKIP;
			}
			IASTStandardFunctionDeclarator fnDec = (IASTStandardFunctionDeclarator) fnDecA;
			ArrayList<CppVariableNode> listParam = new ArrayList<>();

			parseDeclaratorModifier(mdf, fnDec);
			for (IASTParameterDeclaration paramDec : fnDec.getParameters()) {
				IASTDeclSpecifier pDecType = paramDec.getDeclSpecifier();
				IASTDeclarator pDector = paramDec.getDeclarator();
				CppTypeModifier pMdf = parseBaseModifier(pDecType);

				parseDeclaratorModifier(pMdf, pDector);
				listParam
						.add(new CppVariableNode(parseInlineType(pDecType, pMdf), pDector.getName().toString(), false));
			}

			assert (type != null);
			CppFunctionNode fnNode = new CppFunctionNode(type, new CppNameExpression(fnName),
					listParam.toArray(new VariableNode[listParam.size()]), fnDef.getBody());
			addToCurrentStack(fnNode, fnDef, true);
			applyFileLocation(fnNode, decType.getFileLocation(), fnDec.getFileLocation(), 0);

			return PROCESS_SKIP;
		}

		else if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration smpDec = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier decType = smpDec.getDeclSpecifier();
			ITypeModifier mdf = parseBaseModifier(decType);
			IInsideFileNode complex = null;
			boolean isTypedef = decType.getStorageClass() == IASTDeclSpecifier.sc_typedef;
			boolean isExtern = decType.getStorageClass() == IASTDeclSpecifier.sc_extern;

			IType type = parseInlineType(decType, mdf);
			if (type == null) {
				if (decType instanceof IASTCompositeTypeSpecifier) {
					IASTCompositeTypeSpecifier comType = (IASTCompositeTypeSpecifier) decType;
					type = new CppNamedType(comType.getName(), mdf);
					ComplexTypeNode complexNode = new ComplexTypeNode(comType);
					complex = complexNode;
					type.setBind(complex);
					applyFileLocation(complex, comType.getFileLocation(), comType.getName().getFileLocation(),
							complexNode.getKeyString().length());
				} else if (decType instanceof IASTEnumerationSpecifier) {
					return PROCESS_SKIP;
				} else {
					throw new RuntimeException(
							config.resString("loader.unsupport.type") + decType.getClass().getName()); //$NON-NLS-1$
				}
			}

			for (IASTDeclarator dector : smpDec.getDeclarators()) {
				IType typeClone = type.clone();
				INode decNode;

				parseDeclaratorModifier((CppTypeModifier) typeClone.getTypeModifier(), dector);
				if (dector instanceof IASTFunctionDeclarator) {
					// Parse function declarator
					decNode = null;
				} else if (dector instanceof IASTAmbiguousDeclarator) {
					// Ambiguous declare will be ignored
					decNode = null;
				} else {
					if (isTypedef) {
						// Parse typedef
						decNode = null;
					} else {
						CppVariableNode var = new CppVariableNode(typeClone, dector.getName().toString(), isExtern);
						decNode = var;

						if (dector.getInitializer() != null) {
							var.setValue(parseInitialer(dector.getInitializer()));
						}
					}
				}

				if (decNode != null) {
					stackNode.peek().getKey().add(decNode);
				}
			}

			if (complex != null) {
				addToCurrentStack(complex, smpDec, true);
				stackNode.push(new Pair<>(complex, smpDec));
			}
		}

		return PROCESS_CONTINUE;
	}

	/**
	 * Parse a inline-able type
	 * <ul>
	 * <li>simple type: <code>int, float</code></li>
	 * <li>named type: <code>SinhVien, List</code></li>
	 * <li>elaborated type: <code>struct SinhViem, class List</code></li>
	 * </ul>
	 * 
	 * @param decType
	 *            AST type to parse
	 * @param mdf
	 *            base modifier attached to the type
	 * @return parsed type, or <code>null</code> if this is a enum/composite type
	 */
	public static @Nullable IType parseInlineType(IASTDeclSpecifier decType, ITypeModifier mdf)
	{
		if (decType instanceof IASTSimpleDeclSpecifier) {
			return new CppBasicType((IASTSimpleDeclSpecifier) decType, mdf);
		} else if (decType instanceof IASTNamedTypeSpecifier) {
			return new CppNamedType(((IASTNamedTypeSpecifier) decType).getName(), mdf);
		} else if (decType instanceof IASTElaboratedTypeSpecifier) {
			IASTElaboratedTypeSpecifier elaType = (IASTElaboratedTypeSpecifier) decType;
			return new CppNamedType(elaType.getName(), mdf, elaType.getKind());
		}

		return null;
	}

	/**
	 * Set the location information to the node
	 * 
	 * @param node
	 *            node that is inside the file
	 * @param locBegin
	 *            location to get the first position
	 * @param locEnd
	 *            location to get the last position
	 * @param fallbackLen
	 *            the content length if the <code>locEnd</code> is null
	 */
	static void applyFileLocation(IInsideFileNode node, IASTFileLocation locBegin, @Nullable IASTFileLocation locEnd,
			int fallbackLen)
	{
		int offset = locBegin.getNodeOffset();
		int offsetEnd;
		if (locEnd != null) {
			offsetEnd = locEnd.getNodeOffset() + locEnd.getNodeLength();
		} else {
			offsetEnd = offset + fallbackLen;
		}
		node.setFileLocation(offset, offsetEnd - offset);
	}

	/**
	 * Parse a modifier attached to the C/C++ type (modifier in the name will be parse later)
	 * 
	 * @param decType
	 *            AST type to parse
	 * @return base modifier
	 */
	public static CppTypeModifier parseBaseModifier(IASTDeclSpecifier decType)
	{
		CppTypeModifier mdf = new CppTypeModifier();
		mdf.setConst(decType.isConst());
		mdf.setStatic(decType.getStorageClass() == IASTDeclSpecifier.sc_static);
		return mdf;
	}

	/**
	 * Parse a modifier attached to the declare name
	 * 
	 * @param mdf
	 *            modifier to edit
	 * @param dec
	 *            the declarator node
	 * @return the modifier (same as <code>mdf</code>)
	 */
	public static CppTypeModifier parseDeclaratorModifier(CppTypeModifier mdf, IASTDeclarator dec)
	{
		int pointerLevel = 0;
		for (IASTPointerOperator pointer : dec.getPointerOperators()) {
			if (pointer instanceof IASTPointer) {
				pointerLevel++;
			} else if (pointer instanceof ICPPASTReferenceOperator) {
				mdf.setReference(true);
			}
		}
		mdf.setPointerLevel(pointerLevel);

		// Parse array [] to modifier
		// Parse bit field to modifier

		return mdf;
	}

	/**
	 * Parse the initialer and convert to the expression
	 * 
	 * @param init
	 *            the initialer
	 * @return expression
	 */
	static IExpression parseInitialer(IASTInitializer init)
	{
		return CppConverter.convert(init);
	}

	@Override
	public int leave(@Nullable IASTDeclaration declaration)
	{
		if (stackNode.peek().getValue() == declaration) {
			stackNode.pop();
		}
		return PROCESS_CONTINUE;
	}

	/**
	 * Log all syntax-problem to the logger
	 * 
	 * @param problem
	 *            AST problem node
	 * @return travel flag
	 */
	@Override
	public int visit(@Nullable IASTProblem problem)
	{
		assert (problem != null);

		config.getLogger().log(ILogger.ERROR, problem.getMessageWithLocation());
		return PROCESS_SKIP;
	}

	/**
	 * Visit a namespace, add it to stack
	 * 
	 * @param namespaceDefinition
	 *            AST namespace node
	 * @return travel flag
	 */
	@Override
	public int visit(@Nullable ICPPASTNamespaceDefinition namespaceDefinition)
	{
		assert (namespaceDefinition != null);

		NamespaceNode ns = new NamespaceNode(namespaceDefinition.getName().toString());
		addToCurrentStack(ns, namespaceDefinition, false);
		stackNode.push(new Pair<>(ns, namespaceDefinition));
		applyFileLocation(ns, namespaceDefinition.getFileLocation(), namespaceDefinition.getName().getFileLocation(),
				9);
		return PROCESS_CONTINUE;
	}

	/**
	 * Leave a namespace, pop back from stack
	 * 
	 * @param namespaceDefinition
	 *            AST namespace node
	 * @return travel flag
	 */
	@Override
	public int leave(@Nullable ICPPASTNamespaceDefinition namespaceDefinition)
	{
		stackNode.pop();
		return PROCESS_CONTINUE;
	}

}
