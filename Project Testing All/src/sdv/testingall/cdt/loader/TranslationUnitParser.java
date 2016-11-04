/**
 * Parse a C/C++ translation unit
 * @file TranslationUnitParser.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.util.Stack;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

import sdv.testingall.cdt.node.CppFileNode;
import sdv.testingall.cdt.node.NamespaceNode;
import sdv.testingall.cdt.type.CppNamedType;
import sdv.testingall.cdt.type.CppTypeModifier;
import sdv.testingall.core.logger.ILogger;
import sdv.testingall.core.node.INode;
import sdv.testingall.core.type.IType;
import sdv.testingall.core.type.ITypeModifier;

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
		if (declaration instanceof IASTFunctionDefinition) {
			//
		}

		else if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration smpDec = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier decType = smpDec.getDeclSpecifier();
			ITypeModifier mdf = parseBaseModifier(decType);
			IType type = parseInlineType(decType, mdf);

			if (type == null) {
				// Enum or composite type, parse to bind a node
			}

			// SimpleType
			// NamedType
			// Elaborate
			// Enumeration
			// Composite
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
	static IType parseInlineType(IASTDeclSpecifier decType, ITypeModifier mdf)
	{
		if (decType instanceof IASTSimpleDeclSpecifier) {
			// Build-int type, map to node
		} else if (decType instanceof IASTNamedTypeSpecifier) {
			return new CppNamedType(((IASTNamedTypeSpecifier) decType).getName(), mdf);
		} else if (decType instanceof IASTElaboratedTypeSpecifier) {
			IASTElaboratedTypeSpecifier elaType = (IASTElaboratedTypeSpecifier) decType;
			CppNamedType type = new CppNamedType(elaType.getName(), mdf);
			int fixedType;

			switch (elaType.getKind()) {
			case IASTElaboratedTypeSpecifier.k_struct:
				fixedType = CppNamedType.ELA_STRUCT;
				break;
			case IASTElaboratedTypeSpecifier.k_enum:
				fixedType = CppNamedType.ELA_ENUM;
				break;
			case IASTElaboratedTypeSpecifier.k_union:
				fixedType = CppNamedType.ELA_UNION;
				break;
			case ICPPASTElaboratedTypeSpecifier.k_class:
				fixedType = CppNamedType.ELA_CLASS;
				break;
			default:
				fixedType = CppNamedType.ELA_NONE;
			}

			type.setElaborated(fixedType);
			return type;
		}

		return null;
	}

	/**
	 * Parse a modifier attached to the C/C++ type (modifier in the name will be parse later)
	 * 
	 * @param decType
	 *            AST type to parse
	 * @return base modifier
	 */
	static ITypeModifier parseBaseModifier(IASTDeclSpecifier decType)
	{
		CppTypeModifier mdf = new CppTypeModifier();
		mdf.setConst(decType.isConst());
		return mdf;
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
