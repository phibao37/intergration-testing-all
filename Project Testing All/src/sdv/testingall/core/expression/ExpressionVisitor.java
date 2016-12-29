/**
 * Default implement for expression visitor
 * @file ExpressionVisitor.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import sdv.testingall.core.expression.IDeclareExpression.IDeclarator;
import sdv.testingall.core.expression.IExpressionVisitor.IVisitorState;

/**
 * Default implement for expression visitor
 * 
 * @author VuSD
 *
 * @date 2016-12-20 VuSD created
 */
public class ExpressionVisitor implements IExpressionVisitor, IVisitorState {

	@Override
	public boolean preVisit(IExpression expression)
	{
		return true;
	}

	@Override
	public void postVisit(IExpression expression)
	{
		// do nothing
	}

	@Override
	public int visit(INumberExpression number)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(INumberExpression number)
	{
		// do nothing
	}

	@Override
	public int visit(INameExpression name)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(INameExpression name)
	{
		// do nothing
	}

	@Override
	public int visit(IBinaryExpression bin)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IBinaryExpression bin)
	{
		// do nothing
	}

	@Override
	public int visit(IUnaryExpression unary)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IUnaryExpression unary)
	{
		// do nothing
	}

	@Override
	public int visit(IReturnExpression returnEx)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IReturnExpression returnEx)
	{
		// do nothing
	}

	@Override
	public int visit(IUnsupportedTypeException type)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IUnsupportedTypeException type)
	{
		// do nothing
	}

	@Override
	public int visit(IDeclareExpression declare)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IDeclareExpression declare)
	{
		// do nothing
	}

	@Override
	public int visit(IDeclarator declarator)
	{
		return PROCESS_CONTINUE;
	}

	@Override
	public void leave(IDeclarator declarator)
	{
		// do nothing
	}

}
