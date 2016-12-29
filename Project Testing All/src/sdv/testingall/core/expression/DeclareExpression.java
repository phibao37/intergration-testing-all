/**
 * Implement for declaration expression
 * @file DeclareExpression.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.expression;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.type.IType;

/**
 * Implement for declaration expression
 * 
 * @author VuSD
 *
 * @date 2016-12-29 VuSD created
 */
public class DeclareExpression extends ExpressionGroup implements IDeclareExpression {

	/**
	 * Implement for declarator part inside a declaration
	 */
	public static class Declarator extends ExpressionGroup implements IDeclarator {

		/**
		 * Create new declarator part
		 * 
		 * @param name
		 *            declaring name
		 * @param value
		 *            default declaring value, can be {@code null}
		 */
		public Declarator(INameExpression name, @Nullable IExpression value)
		{
			super(name, value);
		}

		@Override
		public String computeContent()
		{
			return getName() + " = " + getValue(); //$NON-NLS-1$
		}

		@Override
		public IType getType()
		{
			// no type needed
			return null;
		}

		@Override
		public INameExpression getName()
		{
			return (INameExpression) childs[0];
		}

		@Override
		public IExpression getValue()
		{
			return childs[1];
		}

	}

	private IType type;

	/**
	 * Create new declaration expression
	 * 
	 * @param type
	 *            declaring type
	 * @param declarators
	 *            list of declaring variable
	 */
	public DeclareExpression(IType type, IDeclarator... declarators)
	{
		super(declarators);
		this.type = type;
	}

	@Override
	public String computeContent()
	{
		StringBuilder b = new StringBuilder();
		b.append(getType()).append(' ');

		for (IDeclarator dec : getDeclarators()) {
			b.append(dec).append(',').append(' ');
		}
		return b.substring(0, b.length() - 2);
	}

	@Override
	public @NonNull IType getType()
	{
		return type;
	}

	@Override
	public IDeclarator[] getDeclarators()
	{
		return (IDeclarator[]) childs;
	}

}
