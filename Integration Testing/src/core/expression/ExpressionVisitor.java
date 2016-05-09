package core.expression;

import api.expression.IArrayExpression;
import api.expression.IArrayIndexExpression;
import api.expression.IBinaryExpression;
import api.expression.IDeclareExpression;
import api.expression.IExpression;
import api.expression.IExpressionVisitor;
import api.expression.IFunctionCallExpression;
import api.expression.IMemberAccessExpression;
import api.expression.INameExpression;
import api.expression.INumberExpression;
import api.expression.IObjectExpression;
import api.expression.IReturnExpression;
import api.expression.IStringExpression;
import api.expression.IUnaryExpression;
import api.models.IStatement;

public class ExpressionVisitor implements IExpressionVisitor {

	@Override
	public int visit(IReturnExpression rt) {
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IReturnExpression rt) {
		
	}

	@Override
	public boolean preVisit(IExpression expression) {
		return true;
	}

	@Override
	public void postVisit(IExpression expression) {}

	@Override
	public int visit(INameExpression name) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IFunctionCallExpression call) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IArrayExpression array) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IArrayIndexExpression array) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IBinaryExpression bin) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IUnaryExpression unary) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IDeclareExpression declare) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(INumberExpression number) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IMemberAccessExpression member) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IStringExpression string) {
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(INameExpression name) {
		
	}

	@Override
	public void leave(IFunctionCallExpression call) {
		
	}

	@Override
	public void leave(IArrayExpression array) {
		
	}

	@Override
	public void leave(IArrayIndexExpression array) {
		
	}

	@Override
	public void leave(IBinaryExpression bin) {
		
	}

	@Override
	public void leave(IUnaryExpression unary) {
		
	}

	@Override
	public void leave(IDeclareExpression declare) {
		
	}

	@Override
	public void leave(INumberExpression number) {
		
	}

	@Override
	public void leave(IMemberAccessExpression member) {
		
	}

	@Override
	public void leave(IStringExpression string) {
		
	}

	@Override
	public int visit(IStatement statement) {
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IObjectExpression obj) {
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IObjectExpression obj) {}

}
