/**
 * Represent a scope (virtual)
 * @file IScopeStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

/**
 * Represent a scope (virtual), include opening and closing scope
 * 
 * @author VuSD
 *
 * @date 2016-12-12 VuSD created
 */
public interface IScopeStatement extends INormalStatement {

	/**
	 * Check this is opening scope statement
	 * 
	 * @return opening state
	 */
	boolean isOpening();

	/**
	 * Check this is closing scope statement
	 * 
	 * @return closing state
	 */
	boolean isClosing();

	/**
	 * Create new scope statement object
	 * 
	 * @param isOpening
	 *            this is opening scope
	 * @return scope statement
	 */
	static IScopeStatement newScopeStatement(boolean isOpening)
	{
		return new ScopeStatement(isOpening);
	}
}

class ScopeStatement extends NormalStatement implements IScopeStatement {

	private boolean isOpening;

	public ScopeStatement(boolean isOpening)
	{
		this.isOpening = isOpening;
	}

	@Override
	public boolean isOpening()
	{
		return isOpening;
	}

	@Override
	public boolean isClosing()
	{
		return !isOpening;
	}

}