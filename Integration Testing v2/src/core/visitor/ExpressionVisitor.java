package core.visitor;

import core.models.Expression;
import core.models.expression.ArrayExpression;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.DeclareExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.models.expression.PlaceHolderExpression;
import core.models.expression.UnaryExpression;

/**
 * Bộ visit dùng để duyệt trong cây biểu thức
 *
 */
public abstract class ExpressionVisitor {
	
	/**
	 * Bỏ qua việc duyệt các biểu thức khác, thoát hoàn toàn
	 */
	public static final int PROCESS_ABORT = -1;
	
	/**
	 * Bỏ qua duyệt các biểu thức con, tiếp tục duyệt các biểu thức sau
	 */
	public static final int PROCESS_SKIP = 0;
	
	/**
	 * Tiếp tục duyệt các biểu thức con, sau đó là các biểu thức đằng sau
	 */
	public static final int PROCESS_CONTINUE = 1;
	
	
	/**
	 * Trước khi duyệt qua một biểu thức nào đó
	 * @param expression biểu thức sẽ được duyệt
	 * @return 
	 * <b>true</b>: duyệt qua biểu thức
	 * <b>false</b>: bỏ qua biểu thức
	 */
	public boolean preVisit(Expression expression){
		return true;
	}
	
	/**
	 * Sau khi duyệt qua một biểu thức nào đó
	 * @param expression biểu thức đã được duyệt
	 */
	public void postVisit(Expression expression) {}
	
	
	public int visit(NameExpression name){
		return PROCESS_CONTINUE;
	}
	
	public int visit(FunctionCallExpression call){
		return PROCESS_CONTINUE;
	}
	
	public int visit(ArrayExpression array){
		return PROCESS_CONTINUE;
	}
	
	public int visit(ArrayIndexExpression array){
		return PROCESS_CONTINUE;
	}
	
	public int visit(BinaryExpression bin){
		return PROCESS_CONTINUE;
	}
	
	public int visit(UnaryExpression unary){
		return PROCESS_CONTINUE;
	}
	
	public int visit(DeclareExpression declare){
		return PROCESS_CONTINUE;
	}
	
	public int visit(IDExpression id){
		return PROCESS_CONTINUE;
	}
	
	public int visit(PlaceHolderExpression place){
		return PROCESS_CONTINUE;
	}
}
