package cdt.visitor;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;

import core.models.Expression;
import core.models.expression.ArrayExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.IDExpression;
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
		int a = 5;
		switch (a){
		case 0:
			System.out.println("Case 0");
		case 4:
		default:
		case 5:
			System.out.println("Case default");
		case 2:
			System.out.println("Case 2");
			break;
		case 3:
			System.out.println("Case 3");
		
		}
	}
	
	/**
	 * Chuyển một chuỗi biểu thức sang nút biểu thức 
	 */
	public static IASTExpression getExpression(String expression){
		return SimpleVisitor.DEFAULT.getExpression(expression);
	}
	
	/**
	 * Phân tích nội dung một nút AST và lấy về biểu thức gốc chứa trong nút
	 * @param node nút AST, thường là một {@link IASTExpression}
	 * @return biểu thức tại gốc của nút
	 */
	public static Expression parseNode(IASTNode node){
		
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
			return new IDExpression(node.getRawSignature());
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
		
		if (node instanceof IASTExpressionStatement){
			return parseNode(((IASTExpressionStatement) node).getExpression());
		}
		
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
			if (!source.endsWith(";"))
				source += ";";
			source = String.format("void main(){%s}", source);
			IASTTranslationUnit unit = CUnitVisitor.getIASTTranslationUnit("", 
					source.toCharArray());
			unit.accept(this);
			return mExpression;
		}

		@Override
		public int visit(IASTExpression expression) {
			mExpression = expression; 
			return PROCESS_ABORT;
		}
		
		public static final SimpleVisitor DEFAULT = new SimpleVisitor();
		
	}
}
