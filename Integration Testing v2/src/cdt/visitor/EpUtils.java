package cdt.visitor;

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
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
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

import cdt.models.CType;
import core.ProcessInterface;
import core.models.Expression;
import core.models.Type;
import core.models.expression.ArrayExpression;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.DeclareExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.MemberAccessExpression;
import core.models.expression.NameExpression;
import core.models.expression.ReturnExpression;
import core.models.expression.UnaryExpression;

/**
 * Chuyển đổi các cấu trúc AST sang các biểu thức
 * @author ducvu
 *
 */
public class EpUtils {
	
	public static void main(String[] args){
		System.out.println(EpUtils.getExpression("test(maaa)"));
		
	}
	
	/**
	 * Chuyển một chuỗi biểu thức sang nút biểu thức 
	 */
	public static IASTExpression getExpression(String expression){
		return SimpleVisitor.DEFAULT.getExpression(expression);
	}
	
	private ProcessInterface mProcess;
	
	public EpUtils(ProcessInterface process){
		mProcess = process;
	}
	
	/**
	 * Phân tích nội dung một nút AST và lấy về biểu thức gốc chứa trong nút
	 * @param node nút AST, thường là một {@link IASTExpression}
	 * @return biểu thức tại gốc của nút
	 */
	public Expression parseNode(IASTNode node){
		
		if (node instanceof IASTBinaryExpression){
			IASTBinaryExpression astBin = (IASTBinaryExpression) node;
			return new BinaryExpression(
					parseNode(astBin.getOperand1()), 
					String.valueOf(ASTStringUtil.getBinaryOperatorString(astBin)), 
					parseNode(astBin.getOperand2()));
		}
		
		if (node instanceof IASTUnaryExpression){
			IASTUnaryExpression unary = (IASTUnaryExpression) node;
			Expression child = parseNode(unary.getOperand());
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
			Expression[] indexes = new Expression[listIndexes.size()];
			
			for (int i = 0; i < indexes.length; i++)
				indexes[i] = parseNode(listIndexes.get(i));
			
			return new ArrayIndexExpression(parseNode(node), indexes);
		}
		
		if (node instanceof IASTFunctionCallExpression){
			IASTFunctionCallExpression call = (IASTFunctionCallExpression) node;
			IASTInitializerClause[] args = call.getArguments();
			Expression[] argEps = new Expression[args.length];
			
			for (int i = 0; i < args.length; i++)
				argEps[i] = parseNode(args[i]);
			return new FunctionCallExpression(
					parseNode(call.getFunctionNameExpression()), argEps);
		}
		
		if (node instanceof IASTDeclarationStatement){
			IASTSimpleDeclaration declare = (IASTSimpleDeclaration) 
					((IASTDeclarationStatement) node).getDeclaration();
			Type type = CType.parse(
					declare.getDeclSpecifier().getRawSignature(), mProcess);
			IASTDeclarator[] drs = declare.getDeclarators();
			Expression[] decEps = new Expression[drs.length];
			
			for (int i = 0; i < drs.length; i++){
				IASTDeclarator dr = drs[i];
				String name = dr.getName().getRawSignature();
				IASTInitializer init = dr.getInitializer();
				
				if (dr instanceof IASTArrayDeclarator){
					IASTArrayModifier[] mdfs = ((IASTArrayDeclarator) dr)
							.getArrayModifiers();
					Expression[] indexes = new Expression[mdfs.length];
					
					for (int j = 0; j < mdfs.length; j++)
						indexes[j] = parseNode(mdfs[j].getConstantExpression());
					decEps[i] = new ArrayIndexExpression(
							new NameExpression(name), indexes).setDeclare();
				} else {
					decEps[i] = new NameExpression(name);
				}
				
				if (init instanceof IASTEqualsInitializer){
					Expression right = parseNode(((IASTEqualsInitializer) init)
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
			Expression[] elements = new Expression[clauses.length];
			
			for (int i = 0; i < clauses.length; i++)
				elements[i] = parseNode(clauses[i]);
			
			return new ArrayExpression(elements);
		}
		
		//Đang thăm 1 cấu trúc hằng
		if (node instanceof IASTLiteralExpression){
			int liter = ((IASTLiteralExpression) node).getKind();
			int flag = 0;
			
			if (liter == IASTLiteralExpression.lk_integer_constant)
				flag = IDExpression.INTEGER | IDExpression.LONG;
			else if (liter == IASTLiteralExpression.lk_float_constant)
				flag = IDExpression.FLOAT | IDExpression.DOUBLE;
			else if (liter == IASTLiteralExpression.lk_char_constant)
				flag = IDExpression.CHARACTER;
			else if (liter == IASTLiteralExpression.lk_true
					|| liter == IASTLiteralExpression.lk_false)
				flag = IDExpression.BOOLEAN;

			return new IDExpression(node.toString(), flag);
		}
		
		//Đang thăm 1 tham chiếu tên biến
		if (node instanceof IASTIdExpression || node instanceof IASTName){
			return new NameExpression(node.getRawSignature());
		}
		
		//Đang thăm 1 câu lệnh RETURN
		if (node instanceof IASTReturnStatement){
			Expression rt = parseNode(((IASTReturnStatement) node).getReturnValue());
			return new ReturnExpression(rt);
		}
		
		if (node instanceof IASTFieldReference){
			IASTFieldReference field = (IASTFieldReference) node;
			Expression object = parseNode(field.getFieldOwner());
			return new MemberAccessExpression(object, 
					field.getFieldName().getRawSignature(), !field.isPointerDereference());
		}
		
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
				CUnitVisitor.getIASTranslationUnit(source, null).accept(this);
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
