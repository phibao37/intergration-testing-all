/**
 * Base implementation for node
 * @file BaseNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Base implementation for node
 * 
 * @author VuSD
 *
 * @date 2016-11-02 VuSD created
 */
@NonNullByDefault
public abstract class BaseNode extends ArrayList<INode> implements INode {

	private String				content	= "";
	private @Nullable String	description;

	/**
	 * Create empty new node
	 */
	protected BaseNode()
	{
	}

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

	@Override
	@Nullable
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}

}
