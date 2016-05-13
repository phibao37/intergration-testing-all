package jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import api.IProject;
import api.expression.IExpression;
import core.expression.ArrayExpression;
import core.expression.ArrayIndexExpression;
import core.expression.BinaryExpression;
import core.expression.DeclareExpression;
import core.expression.MemberAccessExpression;
import core.expression.NameExpression;
import core.expression.NumberExpression;

public class ExpressionConverter {

	private IProject project;
	
	public ExpressionConverter(IProject prj) {
		this.project = prj;
	}
	
	@SuppressWarnings("unchecked")
	public IExpression parseNode(ASTNode node){
		if(node instanceof ArrayInitializer){
			ArrayInitializer arr = (ArrayInitializer)node;
			List<Expression> list = arr.expressions();
			IExpression[] temp = new IExpression[list.size()];
			for(int i=0; i<list.size(); i++){
				temp[i] = parseNode(list.get(i));
			}
			return new ArrayExpression(temp);
		}
//		else if(node instanceof ArrayAccess){
//			ArrayAccess exp = (ArrayAccess)node;
//			String name = exp.getArray().toString();
//			Expression list = exp.getIndex();
//			IExpression index = parseNode(list);
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
			
			IExpression[] index = new IExpression[list.size()];
			for (int i = 0; i < index.length; i++)
				index[i] = parseNode(list.get(i));
			
			return new ArrayIndexExpression(parseNode(node), index);
		}
		
		else if(node instanceof Assignment){
			Assignment exp = (Assignment)node;
			String operator = exp.getOperator().toString();
			IExpression leftElement = parseNode(exp.getLeftHandSide());
			IExpression rightElement = parseNode(exp.getRightHandSide());
			return new BinaryExpression(leftElement, operator, rightElement);
		}
		else if(node instanceof InfixExpression){
			InfixExpression exp = (InfixExpression)node;
			String operator = exp.getOperator().toString();
			IExpression leftElement = parseNode(exp.getLeftOperand());
			IExpression rightElement = parseNode(exp.getRightOperand());
			return new BinaryExpression(leftElement, operator, rightElement);
		}
		else if (node instanceof QualifiedName){
			QualifiedName x = (QualifiedName) node;
			return new MemberAccessExpression(
					new NameExpression(x.getQualifier().toString()), 
					x.getName().getIdentifier(), 
					true);
		}
		else if(node instanceof Name){
			return new NameExpression(node.toString());
		}
//		else if(node instanceof BooleanLiteral || node instanceof NullLiteral || node instanceof NumberLiteral){
//			return new IDExpression(node.toString());
//		}
		
		else if (node instanceof BooleanLiteral){
			return new NumberExpression(((BooleanLiteral) node).booleanValue());
		}
		
		else if (node instanceof NumberLiteral){
			return new NumberExpression(
					((NumberLiteral) node).getToken(), 
					NumberExpression.NUMBER);
		}
		
		else if (node instanceof CharacterLiteral){
			return new NumberExpression(((CharacterLiteral) node).charValue());
		}
		
		else if(node instanceof PostfixExpression){
			PostfixExpression exp = (PostfixExpression)node;
			IExpression element = parseNode(exp.getOperand());
			String operator = exp.getOperator().toString();
			return new core.expression.UnaryExpression(element, operator);
		}
		else if(node instanceof PrefixExpression){
			PrefixExpression exp = (PrefixExpression)node;
			IExpression element = parseNode(exp.getOperand());
			String operator = exp.getOperator().toString();
			return new core.expression.UnaryExpression(operator, element);
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
			
			
			IExpression[] vars = new IExpression[list.size()];
			IExpression[] indexs = null;
			
			
			if (type instanceof ArrayType){
				ArrayType arrType = (ArrayType) type;
				List<Dimension> dimen = arrType.dimensions();
				
				indexs = new IExpression[dimen.size()];
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
			return new DeclareExpression(project.findType(type.toString()), vars);
		}
		
		else if(node instanceof ReturnStatement){
			ReturnStatement x = (ReturnStatement) node;
			return new core.expression.ReturnExpression(
					parseNode(x.getExpression())
			);
		}
		
		else if (node instanceof FieldAccess){
			FieldAccess x = (FieldAccess) node;
			return new MemberAccessExpression(
					parseNode(x.getExpression()), 
					x.getName().getIdentifier(), 
					true);
		} 
		
		if (node != null){
			System.out.printf("Chua xu ly den: %s - %s\n", node, node.getClass());
		}
		
		return null;
	}
}
