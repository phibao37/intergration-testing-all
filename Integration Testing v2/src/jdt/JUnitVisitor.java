package jdt;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;

import core.Utils;
import core.models.Function;
import core.models.Variable;
import core.visitor.UnitVisitor;

public class JUnitVisitor implements UnitVisitor {
	
	public int test(int a, int b, float c){
		
		return 0;
	}
	
	private ArrayList<Function> mFunctions = new ArrayList<Function>();
	
	public static void main(String[] args){
		try{
		String path = "E:\\Projects\\Working Copy\\intergration-testing-all\\Integration Testing v2\\src\\jdt\\JUnitVisitor.java";
		File f = new File(path);
		String source = Utils.getContentFile(f);
		
		new JUnitVisitor().parseSource(source, f);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public UnitVisitor parseSource(String source, File file, Object... args) {
		mFunctions.clear();
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {
			
			int level = 0;
			
			@Override
			public void preVisit(ASTNode node) {
				String margin = "";
				for (int i = 0; i < level; i++)
					margin += "  ";
				System.out.printf("%s%s : %s\n", margin, node.toString(), node.getClass().getSimpleName());
			}

			@Override
			public boolean visit(Block node) {
				level++;
				return true;
			}

			@Override
			public void endVisit(Block node) {
				level--;
			}

			/*@Override
			public boolean visit(MethodDeclaration node) {
				String s_name = node.getName().getIdentifier();
				Type type = node.getReturnType2();
				
				if (!type.isPrimitiveType()){
					throw new RuntimeException("CHua ho tro");
				}
				String s_type = type.toString();
				
				List<SingleVariableDeclaration> paras = node.parameters();
				
				
				return false;
			}
			*/
			
			
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
