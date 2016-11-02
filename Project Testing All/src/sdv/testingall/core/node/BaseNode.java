/**
 * Base implementation for node
 * @file BaseNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.util.ArrayList;

/**
 * Base implementation for node
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
public abstract class BaseNode extends ArrayList<INode> implements INode {

	private String content;

	/**
	 * Create new node
	 * 
	 * @param content
	 *            node content
	 */
	public BaseNode(String content)
	{
		setContent(content);
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
	public BaseNode clone()
	{
		return (BaseNode) super.clone();
	}

}
