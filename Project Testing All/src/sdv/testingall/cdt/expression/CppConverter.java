/**
 * Convert AST expression to core expression
 * @file CppConverter.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.expression;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;

import sdv.testingall.cdt.loader.TranslationUnitParser;
import sdv.testingall.cdt.type.CppTypeModifier;
import sdv.testingall.core.expression.DeclareExpression;
import sdv.testingall.core.expression.IDeclareExpression.IDeclarator;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.INameExpression;
import sdv.testingall.core.expression.ReturnExpression;
import sdv.testingall.core.type.IType;

/**
 * Convert AST expression to core expression
 * 
 * @author VuSD
 *
 * @date 2016-11-10 VuSD created
 */
public class CppConverter {

	/**
	 * Convert an AST expression to core expression
	 * 
	 * @param node
	 *            AST expression
	 * @return converted expression
	 */
	public static IExpression convert(IASTNode node)
	{
		// Declaration statement
		if (node instanceof IASTDeclarationStatement) {
			return convert(((IASTDeclarationStatement) node).getDeclaration());
		}

		// Variable declaration
		else if (node instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration astDec = (IASTSimpleDeclaration) node;
			IASTDeclSpecifier decType = astDec.getDeclSpecifier();
			CppTypeModifier mdf = TranslationUnitParser.parseBaseModifier(decType);
			IType type = TranslationUnitParser.parseInlineType(decType, mdf);
			IASTDeclarator[] astDecList = astDec.getDeclarators();
			IDeclarator[] decList = new IDeclarator[astDecList.length];
			int iter = 0;

			for (IASTDeclarator astDector : astDecList) {
				CppTypeModifier mdfClone = mdf.clone();
				TranslationUnitParser.parseDeclaratorModifier(mdfClone, astDector);
				INameExpression decName = (INameExpression) convert(astDector.getName());
				IExpression decValue = null;

				if (astDector.getInitializer() != null) {
					decValue = convert(astDector.getInitializer());
				}

				decList[iter++] = new CppDeclarator(decName, decValue, mdfClone);
			}

			return new DeclareExpression(type, decList);
		}

		// Convert Id to name
		else if (node instanceof IASTIdExpression) {
			return convert(((IASTIdExpression) node).getName());
		}

		// Converting constant value
		else if (node instanceof IASTLiteralExpression) {
			IASTLiteralExpression astLiteral = (IASTLiteralExpression) node;

			switch (astLiteral.getKind()) {
			case IASTLiteralExpression.lk_string_literal:
				// Create string expression
			case IASTLiteralExpression.lk_this:
			case IASTLiteralExpression.lk_nullptr:
				break;
			default:
				return new CppNumberExpression(astLiteral);
			}
		}

		// Convert name reference to expression
		else if (node instanceof IASTName) {
			return new CppNameExpression((IASTName) node);
		}

		// Convert statement to expression
		else if (node instanceof IASTExpressionStatement) {
			return convert(((IASTExpressionStatement) node).getExpression());
		}

		// Convert binary expression
		else if (node instanceof IASTBinaryExpression) {
			IASTBinaryExpression astBin = (IASTBinaryExpression) node;
			IExpression left = convert(astBin.getOperand1());
			IExpression right = convert(astBin.getOperand2());
			String operator = String.valueOf(ASTStringUtil.getBinaryOperatorString(astBin));

			return new CppBinaryExpression(left, operator, right);
		}

		// Convert unary expression
		else if (node instanceof IASTUnaryExpression) {
			IASTUnaryExpression astUnary = (IASTUnaryExpression) node;
			IExpression child = convert(astUnary.getOperand());
			String operator = String.valueOf(ASTStringUtil.getUnaryOperatorString(astUnary));
			int operatorType = astUnary.getOperator();

			// For (((x))), just skip all and return x
			if (operatorType == IASTUnaryExpression.op_bracketedPrimary) {
				return child;
			}

			boolean isRightSide = operatorType == IASTUnaryExpression.op_postFixIncr
					|| operatorType == IASTUnaryExpression.op_postFixDecr;
			return new CppUnaryExpression(child, operator, !isRightSide);
		}

		// Convert return statement
		else if (node instanceof IASTReturnStatement) {
			IExpression returnValue = convert(((IASTReturnStatement) node).getReturnValue());
			return new ReturnExpression(returnValue);
		}

		// Unsupported type
		if (node != null) {
			return new CppUnsupportedTypeExpression(node);
		} else {
			return null;
		}
	}

}
