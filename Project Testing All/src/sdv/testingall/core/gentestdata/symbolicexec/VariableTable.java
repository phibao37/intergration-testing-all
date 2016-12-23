/**
 * Implement for variable table
 * @file VariableTable.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.symbolicexec;

import java.util.ArrayList;
import java.util.LinkedList;

import sdv.testingall.core.expression.ExpressionGroup;
import sdv.testingall.core.expression.ExpressionVisitor;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.IExpressionVisitor;
import sdv.testingall.core.expression.INameExpression;
import sdv.testingall.core.type.IType;

/**
 * Implement for variable table
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public class VariableTable extends LinkedList<IVariable> implements IVariableTable {

	private int scope;

	@Override
	public void addVariable(IVariable var)
	{
		var.setScope(scope);
		addFirst(var);
	}

	@Override
	public void increaseScope()
	{
		scope++;
	}

	@Override
	public void decreaseScope()
	{
		removeIf(v -> v.getScope() >= scope);
		scope--;
	}

	@Override
	public void updateVariable(String name, IExpression value)
	{
		value = fill(value);
		find(name).setValue(value);
	}

	@Override
	public IExpression fill(IExpression ex)
	{
		GroupWrapper wrap = new GroupWrapper(ex.clone());
		ArrayList<IExpression> blockReplace = new ArrayList<>();

		ex.accept(new ExpressionVisitor() {

			@Override
			public void leave(INameExpression name)
			{
				IVariable find = find(name.getName());
				if (find.isValueSet()) {
					IExpression value = find.getValue().clone();
					value.setReplaceable(false);
					blockReplace.add(value);
					wrap.replaceChild(name, value);
				}
			}

		});

		blockReplace.forEach(e -> e.setReplaceable(true));
		return wrap.getChild();
	}

	@Override
	public IVariable find(String name)
	{
		for (IVariable var : this) {
			if (var.getName().equals(name)) {
				return var;
			}
		}
		return null;
	}

	/**
	 * Helper class for expression filling
	 */
	class GroupWrapper extends ExpressionGroup {

		/**
		 * Create new group wrapper that wrap an expression into group
		 * 
		 * @param child
		 *            expression need to wrapper
		 */
		public GroupWrapper(IExpression child)
		{
			super(child);
		}

		/**
		 * Get underlying expression
		 * 
		 * @return child expression that under wrap
		 */
		public IExpression getChild()
		{
			return childs[0];
		}

		@Override
		public String computeContent()
		{
			// No need
			return null;
		}

		@Override
		public IType getType()
		{
			// No need
			return null;
		}

		@Override
		public int handleVisit(IExpressionVisitor visitor)
		{
			// No need
			return 0;
		}

		@Override
		public void handleLeave(IExpressionVisitor visitor)
		{
			// No need
		}

	}
}
