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

			for (IStatement stm : this) {
				if (stm.sholdDisplayInTestPath()) {
					b.append(stm);
				}
			}
			toString = b.substring(0, b.length() - separator.length());
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
