/**
 * Base implementation for node
 * @file BaseNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.node;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;
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

	private String				content	= "";	//$NON-NLS-1$
	private @Nullable String	description;
	private @Nullable INode		parent;

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
	protected BaseNode(String content)
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
	public @Nullable INode getParent()
	{
		return parent;
	}

	@Override
	public void setParent(@Nullable INode parent)
	{
		this.parent = parent;
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

	/*--------------------------------- ARRAY LISTENER ---------------------------------*/

	@Override
	public boolean add(INode e)
	{
		e.setParent(this);
		return super.add(e);
	}

	@Override
	public void add(int index, @NonNull INode element)
	{
		super.add(index, element);
		element.setParent(this);
	}

	@Override
	public INode remove(int index)
	{
		INode removed = super.remove(index);
		removed.setParent(null);
		return removed;
	}

	@Override
	public boolean remove(@Nullable Object o)
	{
		boolean removed = super.remove(o);
		if (removed) {
			assert o != null;
			((INode) o).setParent(null);
		}
		return removed;
	}

	@Override
	public void clear()
	{
		for (INode child : this) {
			child.setParent(null);
		}
		super.clear();
	}

}
