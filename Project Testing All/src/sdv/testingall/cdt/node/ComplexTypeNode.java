/**
 * Represent composite data type
 * @file ComplexTypeNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

import sdv.testingall.core.node.BaseNode;
import sdv.testingall.core.node.INode;
import sdv.testingall.util.SDVUtils;

/**
 * Represent composite data type: struct, union, class
 * 
 * @author VuSD
 *
 * @date 2016-11-07 VuSD created
 */
public class ComplexTypeNode extends BaseNode {

	private static final ImageIcon ICON_STRUCT = new ImageIcon(ImageIcon.class.getResource("/node/struct.png")),
			ICON_UNION = new ImageIcon(ImageIcon.class.getResource("/node/union.png")),
			ICON_CLASS = new ImageIcon(ImageIcon.class.getResource("/node/class.png"));

	private int		type;
	private Icon	icon;

	/**
	 * Create new complex type node
	 * 
	 * @param comType
	 *            AST type node
	 */
	public ComplexTypeNode(IASTCompositeTypeSpecifier comType)
	{
		super(comType.getName().toString());
		type = comType.getKey();

		switch (type) {
		case CLASS:
			icon = ICON_CLASS;
			break;
		case STRUCT:
			icon = ICON_STRUCT;
			break;
		case UNION:
			icon = ICON_UNION;
			break;
		}

		// Anonymouse type
		if (comType.getName().toCharArray().length == 0) {
			switch (type) {
			case CLASS:
				setContent("<class>");
				break;
			case STRUCT:
				setContent("<struct>");
				break;
			case UNION:
				setContent("<union>");
				break;
			}
		}
	}

	/**
	 * Get the composite data type
	 * 
	 * @return struct/union/class
	 */
	public int getType()
	{
		return type;
	}

	@Override
	public Icon getIcon()
	{
		return icon;
	}

	@Override
	public int compareTo(INode o)
	{
		return 0;
	}

	/**
	 * Composite type: <code>struct</code>
	 */
	public static final int STRUCT = IASTCompositeTypeSpecifier.k_struct;

	/**
	 * Composite type: <code>union</code>
	 */
	public static final int UNION = IASTCompositeTypeSpecifier.k_union;

	/**
	 * Composite type: <code>class</code>
	 */
	public static final int CLASS = ICPPASTCompositeTypeSpecifier.k_class;

	@Override
	public void setDescription(String description)
	{
		super.setDescription(SDVUtils.removeCommentFlag(description));
	}

}
