package test.foo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

public class TestJDT {
	public int sort(int n, int[] a) {
		int i = 2;
		int j, tmp;
		
		while (i <= n) {
			j = i - 1;
			while ((j >= 1) && (a[j] >= a[j + 1])) {
				tmp = a[i];
				a[i] = a[j];
				a[j] = tmp;
				j--;
			}
			i++;
		}
		return 0;
	}
	
	
	public static void main(String[] args) throws IOException {
		//String path = "C://Users//duonganh2812//Documents//Java function//sum.java";
		//String path = "C://Users//duonganh2812//Documents//Java function//JDTMain.java";
		String path = "D:\\Documents\\java\\TestUnit1.java";
		char[] source = getContentFile(new File(path)).toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source);
		parser.setResolveBindings(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			@SuppressWarnings("unused")
			private int level = 0;

			@Override
			public boolean visit(MethodDeclaration node) {
				int mdf = node.getModifiers();
				
				if (!Modifier.isStatic(mdf) && Modifier.isPublic(mdf)) {
					System.out.printf("Method declare: %s\n", node.getName());

					return true;
				}
				return false;
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean visit(ArrayCreation node) {
				ArrayCreation exp = (ArrayCreation)node;
				System.out.println(exp + ": " + exp.getClass());
				ArrayInitializer arrIn = (ArrayInitializer)exp.getInitializer();
				List<Expression> list = arrIn.expressions();
				for(Expression i: list){
					System.out.println(i.toString());
				}
				return true;
			}
			

			@Override
			public boolean visit(BooleanLiteral node) {
				System.out.println("BooleanLiteral: " + node);
				return true;
			}

			@Override
			public boolean visit(CharacterLiteral node) {
				System.out.println("CharacterLiteral: " + node);
				return true;
			}

			@Override
			public boolean visit(InfixExpression node) {
				InfixExpression exp = (InfixExpression)node;
				System.out.println("InfixExpression: " + node);
				System.out.println(exp.getLeftOperand());
				return true;
			}
			

			@Override
			public boolean visit(NullLiteral node) {
				System.out.println("NullLiteral: " + node);
				return true;
			}

			@Override
			public boolean visit(ArrayAccess node) {
				ArrayAccess exp = (ArrayAccess)node;
				System.out.println("ArrayAccess: "+node);
				System.out.println(exp.getArray().toString());
				return true;
			}
			
			

			@Override
			public boolean visit(StringLiteral node) {
				System.out.println("StringLiteral: " + node);
				return true;
			}

			@Override
			public boolean visit(ParenthesizedExpression node) {
				System.out.println("ParenthesizedExpression: " + node);
				return true;
			}

			@Override
			public boolean visit(CreationReference node) {
				System.out.println("CreationReference: " + node);
				return true;
			}

			@Override
			public boolean visit(PrefixExpression node) {
				System.out.println("PrefixExpression: " + node);
				return true;
			}

			
			@Override
			public boolean visit(NumberLiteral node) {
				System.out.println("NumberLiteral: " + node);
				return true;
			}
			

			@Override
			public boolean visit(NameQualifiedType node) {
				System.out.println("NameQualifiedType: " + node);
				return true;
			}
			
			@Override
			public boolean visit(VariableDeclarationExpression node) {
				System.out.println("NameQualifiedType: " + node);
				return true;
			}

			@Override
			public boolean visit(ExpressionMethodReference node) {
				System.out.println("ExpressionMethodReference: " + node);
				return true;
			}

			@Override
			public boolean visit(ExpressionStatement node) {
				System.out.println("ExpressionStatement: " + node);
				return true;
			}

			@Override
			public void preVisit(ASTNode node) {
//				if (node instanceof Statement && !(node instanceof Block))
//					System.out.printf("Node: %s     type: %s, level: %d\n",
//						node, 
//						node.getClass().getSimpleName(),
//						level
//					);
			}
			
		});
		
	}
	
	/**
	 * Lấy nội dung từ một tập tin chỉ định
	 * @param file tập tin nguồn
	 * @return nội dung văn bản bên trong tập tin
	 * @throws IOException tập tin không được tìm thấy
	 */
	public static String getContentFile(File file) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			String line = br.readLine();
			while (line != null) {
				content.append(line);
				content.append("\n");
				line = br.readLine();
			}
		}
		finally {
			br.close();
		}
		return content.toString();
	}
}
