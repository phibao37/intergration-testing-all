package jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import core.models.expression.ArrayExpression;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.DeclareExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;

public class JEpUtil {
	public static Expression getExpression(String expression){
		return SimpleVisitor.DEFAULT.getExpression(expression);
	}
	
	/**
	 * Phân tích nội dung một nút AST và lấy về biểu thức gốc chứa trong nút
	 * @param node nút AST, thường là một {@link IASTExpression}
	 * @return biểu thức tại gốc của nút
	 */
	
	static class SimpleVisitor{
		
		Expression mExpression;
		
		public Expression getExpression(String source){
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			
			source = String.format("public class test{ public void test{if(%s);}}", source);
			parser.setSource(source.toCharArray());
			//parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(new ASTVisitor(){
				@Override
				public boolean visit(IfStatement node) {
					mExpression = node.getExpression();
					return true;
				}
			}
			
			);
			return mExpression;
		}

		public static final SimpleVisitor DEFAULT = new SimpleVisitor();
		
	}
	
	@SuppressWarnings("unchecked")
	public static core.models.Expression parseNode(ASTNode node){
		if(node instanceof ArrayInitializer){
			ArrayInitializer arr = (ArrayInitializer)node;
			List<Expression> list = arr.expressions();
			core.models.Expression[] temp = new core.models.Expression[list.size()];
			for(int i=0; i<list.size(); i++){
				temp[i] = parseNode(list.get(i));
			}
			return new ArrayExpression(temp);
		}
//		else if(node instanceof ArrayAccess){
//			ArrayAccess exp = (ArrayAccess)node;
//			String name = exp.getArray().toString();
//			Expression list = exp.getIndex();
//			core.models.Expression index = parseNode(list);
//			return new ArrayIndexExpression(name, index);
//		}
		
		else if(node instanceof ArrayAccess){
			ArrayList<Expression> list = new ArrayList<Expression>();
			
			//a[0][1][2]
			while (node instanceof ArrayAccess){
				ArrayAccess exp = (ArrayAccess)node;
				
				list.add(exp.getIndex());//Thêm từ phải vào: 2, 1, 0
				node = exp.getArray();
			}
			Collections.reverse(list);//Đảo ngược lại
			
			core.models.Expression[] index = new core.models.Expression[list.size()];
			for (int i = 0; i < index.length; i++)
				index[i] = parseNode(list.get(i));
			
			return new ArrayIndexExpression(parseNode(node), index);
		}
		
		else if(node instanceof Assignment){
			Assignment exp = (Assignment)node;
			String operator = exp.getOperator().toString();
			core.models.Expression leftElement = parseNode(exp.getLeftHandSide());
			core.models.Expression rightElement = parseNode(exp.getRightHandSide());
			return new BinaryExpression(leftElement, operator, rightElement);
		}
		else if(node instanceof InfixExpression){
			InfixExpression exp = (InfixExpression)node;
			String operator = exp.getOperator().toString();
			core.models.Expression leftElement = parseNode(exp.getLeftOperand());
			core.models.Expression rightElement = parseNode(exp.getRightOperand());
			return new BinaryExpression(leftElement, operator, rightElement);
		}
		else if(node instanceof Name){
			return new NameExpression(node.toString());
		}
//		else if(node instanceof BooleanLiteral || node instanceof NullLiteral || node instanceof NumberLiteral){
//			return new IDExpression(node.toString());
//		}
		
		else if (node instanceof BooleanLiteral){
			return new IDExpression(((BooleanLiteral) node).booleanValue());
		}
		
		else if (node instanceof NumberLiteral){
			return new IDExpression(
					((NumberLiteral) node).getToken(), 
					IDExpression.NUMBER);
		}
		
		else if (node instanceof CharacterLiteral){
			return new IDExpression(((CharacterLiteral) node).charValue());
		}
		
		else if(node instanceof PostfixExpression){
			PostfixExpression exp = (PostfixExpression)node;
			core.models.Expression element = parseNode(exp.getOperand());
			String operator = exp.getOperator().toString();
			return new core.models.expression.UnaryExpression(element, operator);
		}
		else if(node instanceof PrefixExpression){
			PrefixExpression exp = (PrefixExpression)node;
			core.models.Expression element = parseNode(exp.getOperand());
			String operator = exp.getOperator().toString();
			return new core.models.expression.UnaryExpression(operator, element);
		}
		
		//Cặp dấu ngoặc tròn (...)
		else if (node instanceof ParenthesizedExpression){
			return parseNode(((ParenthesizedExpression) node).getExpression());
		}
		
		else if (node instanceof ExpressionStatement){
			return parseNode(((ExpressionStatement) node).getExpression());
		}
		
		else if (node instanceof VariableDeclarationStatement
				|| node instanceof VariableDeclarationExpression){
			
			Type type = null;
			List<VariableDeclarationFragment> list = null;
			
			if (node instanceof VariableDeclarationStatement){
				VariableDeclarationStatement declare = (VariableDeclarationStatement) node;
				type = declare.getType();
				list = declare.fragments();
			} else {
				VariableDeclarationExpression declare = (VariableDeclarationExpression) node;
				type = declare.getType();
				list = declare.fragments();
			}
			
			
			core.models.Expression[] vars = new core.models.Expression[list.size()];
			core.models.Expression[] indexs = null;
			
			
			if (type instanceof ArrayType){
				ArrayType arrType = (ArrayType) type;
				List<Dimension> dimen = arrType.dimensions();
				
				indexs = new core.models.Expression[dimen.size()];
				//TODO lấy cả chỉ số kích thước: int[2][3] a;
			}
			
			for (int i = 0; i < vars.length; i++){
				VariableDeclarationFragment var = list.get(i);
				String name = var.getName().getIdentifier();
				Expression init = var.getInitializer();
				
				vars[i] = new NameExpression(name);
				if (indexs != null)
					vars[i] = new ArrayIndexExpression(vars[i], indexs);
				//Có khai báo mảng ở sau biến?? int[][] a, b[], làm sau
				
				if (init != null)
					vars[i] = new BinaryExpression(
							vars[i], 
							BinaryExpression.ASSIGN, 
							parseNode(init));
			}
			return new DeclareExpression(JType.parse(type.toString()), vars);
		}
		
		else if(node instanceof ReturnStatement){
			ReturnStatement x = (ReturnStatement) node;
			return new core.models.expression.ReturnExpression(
					parseNode(x.getExpression())
			);
		}
		
		if (node != null){
			System.out.printf("Chua xu ly den: %s - %s\n", node, node.getClass());
		}
		
		return null;
	}
	
	public static void main(String args[]){
		
	}
}
