/**
 * Implementation for test path
 * @file TestPath.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import java.util.ArrayList;

/**
 * Represent a test path
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public class TestPath extends ArrayList<IStatement> implements ITestPath {

	private String toString;

	@Override
	public String toString()
	{
		if (toString == null) {
			StringBuilder b = new StringBuilder();
			String separator = " -> "; //$NON-NLS-1$

			for (int i = 0; i < size(); i++) {
				IStatement stm = get(i);

				if (stm.sholdDisplayInTestPath()) {
					b.append(separator).append(stm);

					if (stm instanceof IConditionStatement) {
						IStatement trueBranch = ((IConditionStatement) stm).trueBranch();
						b.append(' ').append('(').append(trueBranch == get(i + 1) ? 'T' : 'F').append(')');
					}
				}
			}
			toString = b.substring(separator.length());
		}
		return toString;
	}

	@Override
	public TestPath clone()
	{
		TestPath clone = (TestPath) super.clone();
		clone.toString = null;
		return clone;
	}

	@Override
	public ITestPath cloneAt(int index)
	{
		TestPath clone = clone();
		int last = size() - 1;

		if (index < last) {
			clone.removeRange(index + 1, last);
		}

		return clone;
	}

}
