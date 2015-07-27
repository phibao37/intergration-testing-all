package core.inte;

import core.error.StatementNoRootException;
import core.models.Expression;
import core.models.Function;
import core.models.expression.FunctionCallExpression;
import core.models.expression.PlaceHolderExpression;
import core.unit.BasisPath;
import core.unit.BasisPathParser;
import core.visitor.ExpressionVisitor;

/**
 * Xử lý các câu lệnh gọi hàm trong các đường đi để phục vụ cho việc tích hợp
 */
public class IntegrationPathParser extends BasisPathParser {
	
	private Function mCalling;
	
	public void parseBasisPath(BasisPath path, Function func, Function calling)
			throws StatementNoRootException {
		mCalling = calling;
		super.parseBasisPath(path, func);
	}

	@Override
	protected void preVisitRoot(Expression root) {
		super.preVisitRoot(root);
		new PlaceHolderExpression(root).accept(new ExpressionVisitor() {

			@Override
			public int visit(FunctionCallExpression call) {
				Function link = call.getFunction();
				
				//Lời gọi hàm này tương ứng với hàm đang được gọi kiểm thử
				if (link == mCalling){
					
					
				}
				
				//Hàm được gọi không là hàm đang xét kiểm thử
				else {
					
				}
				
				return PROCESS_CONTINUE;
			}
			
		});
	}

	@Override
	protected Expression handleFunctionCall(FunctionCallExpression call) {
		
		return super.handleFunctionCall(call);
	}

	/**
	 * Bộ phân tích mặc định
	 */
	public static final IntegrationPathParser DEFAULT = new IntegrationPathParser();
}
