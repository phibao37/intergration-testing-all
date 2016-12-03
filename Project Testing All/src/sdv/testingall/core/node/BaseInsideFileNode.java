/**
 * Abstract node for all node that is inside a source file
 * @file BaseInsideFileNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

/**
 * Abstract node for all node that is inside a source file
 * 
 * @author VuSD
 *
 * @date 2016-11-29 VuSD created
 */
public abstract class BaseInsideFileNode extends BaseNode implements IInsideFileNode {

	private int		fileOffset;
	private int		fileLength;
	private boolean	isInside;

	/**
	 * Create empty new node
	 */
	protected BaseInsideFileNode()
	{
		super();
	}

	/**
	 * Create new node
	 * 
	 * @param content
	 *            node content
	 */
	protected BaseInsideFileNode(String content)
	{
		super(content);
	}

	@Override
	public int fileOffset()
	{
		return fileOffset;
	}

	@Override
	public int fileLength()
	{
		return fileLength;
	}

	@Override
	public void setFileLocation(int offset, int length)
	{
		fileOffset = offset;
		fileLength = length;
	}

	@Override
	public void setIsPartOfSource(boolean inSource)
	{
		isInside = inSource;
	}

	@Override
	public boolean isPartOfSource()
	{
		return isInside;
	}

}
