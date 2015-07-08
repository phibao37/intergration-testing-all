package jdt;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import core.models.Function;
import core.models.Variable;
import core.visitor.UnitVisitor;

public class JUnitVisitor implements UnitVisitor {
	
	private ArrayList<Function> mFunctions = new ArrayList<Function>();
	
	@Override
	public UnitVisitor parseSource(String source, File file, Object... args) {
		mFunctions.clear();
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				String name = node.getName().getIdentifier();
				return false;
			}
			 
			
			
		});
		return this;
	}

	@Override
	public ArrayList<Function> getFunctionList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Variable> getGlobalVariableList() {
		// TODO Lam sau
		return new ArrayList<Variable>();
	}

}
