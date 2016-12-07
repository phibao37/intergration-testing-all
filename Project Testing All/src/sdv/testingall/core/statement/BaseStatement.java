/**
 * Abstract implementation for a statement
 * @file BaseStatement.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.statement;

import java.io.File;

import org.eclipse.jdt.annotation.Nullable;

import sdv.testingall.core.expression.IExpression;

/**
 * Abstract implementation for a statement
 * 
 * @author VuSD
 *
 * @date 2016-12-07 VuSD created
 */
public abstract class BaseStatement implements IStatement {

	private IExpression	root;
	private String		content;

	private File	sourceFile;
	private int		sourceOffset;
	private int		sourceLength;

	/**
	 * Create new statement with its root expression
	 * 
	 * @param root
	 *            root expression attached to
	 */
	protected BaseStatement(@Nullable IExpression root)
	{
		this.root = root;

		if (root != null) {
			setContent(root.toString());
		}
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
	public IExpression getRoot()
	{
		return root;
	}

	/**
	 * Set the location of this statement inside source code
	 * 
	 * @param file
	 *            file object
	 * @param offset
	 *            starting position
	 * @param len
	 *            content length
	 */
	public void setFileLocation(File file, int offset, int len)
	{
		sourceFile = file;
		sourceOffset = offset;
		sourceLength = len;
	}

	@Override
	public File getFile()
	{
		return sourceFile;
	}

	@Override
	public int fileOffset()
	{
		return sourceOffset;
	}

	@Override
	public int fileLength()
	{
		return sourceLength;
	}

}
