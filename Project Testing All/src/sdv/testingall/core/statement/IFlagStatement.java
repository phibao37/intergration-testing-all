/**
 * Special statement to be mark in CFG
 * @file IFlagStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

/**
 * Special statement to be mark in CFG<br/>
 * Example: beginning/ending of CFG
 * 
 * @author VuSD
 *
 * @date 2016-12-09 VuSD created
 */
public interface IFlagStatement extends INormalStatement {

	/**
	 * Get the flag associated with this statement
	 * 
	 * @return statement flag
	 */
	int getFlag();

	/** Beginning of graph */
	int FLAG_BEGIN = 1;

	/** Ending of graph */
	int FLAG_END = 2;

	/**
	 * Create new BEGINING statement
	 * 
	 * @return flag statement
	 */
	static IFlagStatement newBegin()
	{
		return new FlagStatement(FLAG_BEGIN);
	}

	/**
	 * Create new ENDING statement
	 * 
	 * @return flag statement
	 */
	static IFlagStatement newEnd()
	{
		return new FlagStatement(FLAG_END);
	}

	@Override
	default boolean sholdDisplayInTestPath()
	{
		return false;
	}

}

class FlagStatement extends NormalStatement implements IFlagStatement {

	private int flag;

	public FlagStatement(int flag)
	{
		super(null);
		this.flag = flag;
	}

	@Override
	public int getFlag()
	{
		return flag;
	}

}