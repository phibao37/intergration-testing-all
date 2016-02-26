package cdt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;

import api.IProject;
import api.expression.IExpression;
import api.models.IType;
import core.expression.ArrayExpression;
import core.expression.ArrayIndexExpression;
import core.expression.BinaryExpression;
import core.expression.DeclareExpression;
import core.expression.FunctionCallExpression;
import core.expression.NumberExpression;
import core.expression.NameExpression;
import core.expression.ReturnExpression;
import core.expression.StringExpression;
import core.expression.UnaryExpression;

/**
 * Chuyển đổi các cấu trúc AST sang các biểu thức
 * @author ducvu
 *
 */
public class ExpressionUtils {
	
	public static void main(String[] args){
		System.out.println(ExpressionUtils.getExpression("test(maaa)"));
		
	}
	
	/**
	 * Chuyển một chuỗi biểu thức sang nút biểu thức 
	 */
	public static IASTExpression getExpression(String expression){
		return SimpleVisitor.DEFAULT.getExpression(expression);
	}
	
	private IProject mProject;
	
	public ExpressionUtils(IProject project){
		mProject = project;
	}
	
	/**
	 * Phân tích nội dung một nút AST và lấy về biểu thức gốc chứa trong nút
	 * @param node nút AST, thường là một {@link IASTExpression}
	 * @return biểu thức tại gốc của nút
	 */
	public IExpression parseNode(IASTNode node){
		
		if (node instanceof IASTBinaryExpression){
			IASTBinaryExpression astBin = (IASTBinaryExpression) node;
			return new BinaryExpression(
					parseNode(astBin.getOperand1()), 
					String.valueOf(ASTStringUtil.getBinaryOperatorString(astBin)), 
					parseNode(astBin.getOperand2()));
		}
		
		if (node instanceof IASTUnaryExpression){
			IASTUnaryExpression unary = (IASTUnaryExpression) node;
			IExpression child = parseNode(unary.getOperand());
			int op = unary.getOperator();
			String opStr = String.valueOf(ASTStringUtil.getUnaryOperatorString(unary));
			
			//Dấu ngoặc tròn, chỉ lấy cái trong nó
			if (op == IASTUnaryExpression.op_bracketedPrimary)
				return child;
			
			//Phép toán i++ hoặc i-- (postfix)
			else if (op == IASTUnaryExpression.op_postFixDecr 
					|| op == IASTUnaryExpression.op_postFixIncr)
				return new UnaryExpression(child, opStr);
			
			//Các phép toán còn lại thường là prefix
			else
				return new UnaryExpression(opStr, child);
		}
		
		if (node instanceof IASTArraySubscriptExpression){
			ArrayList<IASTInitializerClause> listIndexes = new ArrayList<>();
			
			while (node instanceof IASTArraySubscriptExpression){
				IASTArraySubscriptExpression sub = (IASTArraySubscriptExpression) node;
				listIndexes.add(sub.getArgument());
				node = sub.getArrayExpression();
			}
			
			Collections.reverse(listIndexes);
			IExpression[] indexes = new IExpression[listIndexes.size()];
			
			for (int i = 0; i < indexes.length; i++)
				indexes[i] = parseNode(listIndexes.get(i));
			
			return new ArrayIndexExpression(parseNode(node), indexes);
		}
		
		if (node instanceof IASTFunctionCallExpression){
			IASTFunctionCallExpression call = (IASTFunctionCallExpression) node;
			IASTInitializerClause[] args = call.getArguments();
			IExpression[] argEps = new IExpression[args.length];
			
			for (int i = 0; i < args.length; i++)
				argEps[i] = parseNode(args[i]);
			return new FunctionCallExpression(
					parseNode(call.getFunctionNameExpression()), argEps);
		}
		
		if (node instanceof IASTDeclarationStatement){
			IASTSimpleDeclaration declare = (IASTSimpleDeclaration) 
					((IASTDeclarationStatement) node).getDeclaration();
			IType type = mProject.findType(
					declare.getDeclSpecifier().getRawSignature());
			IASTDeclarator[] drs = declare.getDeclarators();
			IExpression[] decEps = new IExpression[drs.length];
			
			for (int i = 0; i < drs.length; i++){
				IASTDeclarator dr = drs[i];
				String name = dr.getName().getRawSignature();
				IASTInitializer init = dr.getInitializer();
				
				if (dr instanceof IASTArrayDeclarator){
					IASTArrayModifier[] mdfs = ((IASTArrayDeclarator) dr)
							.getArrayModifiers();
					IExpression[] indexes = new IExpression[mdfs.length];
					
					for (int j = 0; j < mdfs.length; j++)
						indexes[j] = parseNode(mdfs[j].getConstantExpression());
					decEps[i] = new ArrayIndexExpression(
							new NameExpression(name), indexes).setDeclare();
				} else {
					decEps[i] = new NameExpression(name);
				}
				
				if (init instanceof IASTEqualsInitializer){
					IExpression right = parseNode(((IASTEqualsInitializer) init)
							.getInitializerClause());
					decEps[i] = new BinaryExpression(
							decEps[i], 
							BinaryExpression.ASSIGN, 
							right);
				} 
			}
			return new DeclareExpression(type, decEps);
		}
	
		
		//Đang thăm 1 danh sách khởi tạo {1, 2, a, b+c}
		if (node instanceof IASTInitializerList){
			IASTInitializerClause[] clauses = ((IASTInitializerList) node).getClauses();
			IExpression[] elements = new IExpression[clauses.length];
			
			for (int i = 0; i < clauses.length; i++)
				elements[i] = parseNode(clauses[i]);
			
			return new ArrayExpression(elements);
		}
		
		//Đang thăm 1 cấu trúc hằng
		if (node instanceof IASTLiteralExpression){
			int liter = ((IASTLiteralExpression) node).getKind();
			
			if (liter == IASTLiteralExpression.lk_string_literal)
				return new StringExpression(node.toString());
			
			int flag = 0;
			
			if (liter == IASTLiteralExpression.lk_integer_constant)
				flag = NumberExpression.INTEGER | NumberExpression.LONG;
			else if (liter == IASTLiteralExpression.lk_float_constant)
				flag = NumberExpression.FLOAT | NumberExpression.DOUBLE;
			else if (liter == IASTLiteralExpression.lk_char_constant)
				flag = NumberExpression.CHARACTER;
			else if (liter == IASTLiteralExpression.lk_true
					|| liter == IASTLiteralExpression.lk_false)
				flag = NumberExpression.BOOLEAN;

			return new NumberExpression(node.toString(), flag);
		}
		
		//Đang thăm 1 tham chiếu tên biến
		if (node instanceof IASTIdExpression || node instanceof IASTName){
			return new NameExpression(node.getRawSignature());
		}
		
		//Đang thăm 1 câu lệnh RETURN
		if (node instanceof IASTReturnStatement){
			IExpression rt = parseNode(((IASTReturnStatement) node).getReturnValue());
			return new ReturnExpression(rt);
		}
		
//		if (node instanceof IASTFieldReference){
//			IASTFieldReference field = (IASTFieldReference) node;
//			IExpression object = parseNode(field.getFieldOwner());
//			return new MemberAccessExpression(object, 
//					field.getFieldName().getRawSignature(), !field.isPointerDereference());
//		}
		
		if (node instanceof IASTExpressionStatement){
			return parseNode(((IASTExpressionStatement) node).getExpression());
		}
		
		if (node != null)
		System.out.printf("Un-support: %s - %s\n",node.getRawSignature(), 
				node.getClass().getSimpleName());
		return null;
	}
	
	static class SimpleVisitor extends ASTVisitor{
		{
			shouldVisitExpressions = true;
			
		}
		
		IASTExpression mExpression;
		
		public IASTExpression getExpression(String source){
			mExpression = null;
			if (!source.endsWith(";"))
				source += ";";
			source = String.format("void main(){%s}", source);
			try {
				CUnitParser.getIASTTranslationUnit(source).accept(this);
				return mExpression;
			} catch (IOException e) {
				return null;
			}
			
		}

		@Override
		public int visit(IASTExpression expression) {
			mExpression = expression; 
			return PROCESS_ABORT;
		}
		
		public static final SimpleVisitor DEFAULT = new SimpleVisitor();
		
	}
}
