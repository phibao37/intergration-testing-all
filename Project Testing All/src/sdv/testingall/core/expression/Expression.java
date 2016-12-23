/**
 * Abstract for all expression
 * @file Expression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Abstract for all expression
 * 
 * @author VuSD
 *
 * @date 2016-12-08 VuSD created
 */
public abstract class Expression implements IExpression {

	protected String		content;
	protected IExpression	source;

	private boolean replaceable;

	/**
	 * Create new expression
	 */
	protected Expression()
	{
		source = this;
		replaceable = true;
	}

	@Override
	public void setContent(String content)
	{
		this.content = content;
	}

	@Override
	public String toString()
	{
		return content;
	}

	@Override
	public Expression clone()
	{
		try {
			return (Expression) super.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
	}

	@Override
	public IExpression getSource()
	{
		return source;
	}

	@Override
	public void setReplaceable(boolean replace)
	{
		this.replaceable = replace;
	}

	@Override
	public boolean isReplaceable()
	{
		return replaceable;
	}

	@Override
	public int accept(IExpressionVisitor visitor)
	{
		int state = PROCESS_SKIP;

		if (visitor.preVisit(this)) {
			state = handleVisit(visitor);

			if (state == PROCESS_CONTINUE && this instanceof IExpressionGroup) {
				IExpression[] childs = ((IExpressionGroup) this).getChilds();

				for (IExpression child : childs) {
					if (child == null) {
						continue;
					}
					int child_state = child.accept(visitor);

					if (child_state == PROCESS_ABORT) {
						state = child_state;
						break;
					}
				}
			}
			handleLeave(visitor);
		}

		visitor.postVisit(this);
		return state;
	}

}
