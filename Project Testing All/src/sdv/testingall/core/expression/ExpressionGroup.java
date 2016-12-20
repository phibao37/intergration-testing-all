/**
 * Base implement for expression group
 * @file ExpressionGroup.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

/**
 * Base implement for expression group
 * 
 * @author VuSD
 *
 * @date 2016-12-19 VuSD created
 */
public abstract class ExpressionGroup extends Expression implements IExpressionGroup {

	protected IExpression[] childs;

	/**
	 * Create new expression group
	 * 
	 * @param childs
	 *            array of child expression inside this root
	 */
	protected ExpressionGroup(IExpression... childs)
	{
		this.childs = childs;
	}

	@Override
	public IExpression[] getChilds()
	{
		return childs;
	}

	@Override
	public boolean replaceChild(IExpression find, IExpression replace)
	{
		boolean success = false;

		for (int i = 0; i < childs.length; i++) {
			IExpression child = childs[i];

			// Skip replace on non-replaceable child
			if (child == null || !child.isReplaceable()) {
				continue;
			}

			// Found a child match to replace
			if (child.getSource() == find.getSource()) {
				childs[i] = replace;
				success = true;
			}

			// Recursive replace in child group
			else if (child instanceof IExpressionGroup) {
				success = ((IExpressionGroup) child).replaceChild(find, replace);
			}
		}

		if (success) {
			invalidateChildContent();
		}

		return success;
	}

	@Override
	public ExpressionGroup clone()
	{
		ExpressionGroup cloned = (ExpressionGroup) super.clone();

		// Deep clone child expression
		for (int i = 0; i < childs.length; i++) {
			if (childs[i] != null) {
				cloned.childs[i] = childs[i].clone();
			}
		}

		return cloned;
	}

	@Override
	public void invalidateChildContent()
	{
		content = null;
	}

	@Override
	public String toString()
	{
		if (content == null) {
			content = computeContent();
		}
		return content;
	}

}
