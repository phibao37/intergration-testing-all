/**
 * Represent composite data type
 * @file ComplexTypeNode.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.node;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

import javafx.scene.image.Image;
import sdv.testingall.core.node.BaseInsideFileNode;
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
public class ComplexTypeNode extends BaseInsideFileNode {

	private static final Image	ICON_STRUCT	= new Image(BaseNode.class.getResourceAsStream("/node/struct.png"));	//$NON-NLS-1$
	private static final Image	ICON_UNION	= new Image(BaseNode.class.getResourceAsStream("/node/union.png"));		//$NON-NLS-1$
	private static final Image	ICON_CLASS	= new Image(BaseNode.class.getResourceAsStream("/node/class.png"));		//$NON-NLS-1$

	private int		type;
	private Image	icon;
	private String	keyString;

	/**
	 * Create new complex type node
	 * 
	 * @param comType
	 *            AST type node
	 */
	@SuppressWarnings("nls")
	public ComplexTypeNode(IASTCompositeTypeSpecifier comType)
	{
		super(comType.getName().toString());
		type = comType.getKey();

		switch (type) {
		case CLASS:
			icon = ICON_CLASS;
			keyString = "class";
			break;
		case STRUCT:
			icon = ICON_STRUCT;
			keyString = "struct";
			break;
		case UNION:
			icon = ICON_UNION;
			keyString = "union";
			break;
		}

		// Anonymouse type
		if (comType.getName().toCharArray().length == 0) {
			setContent("<" + keyString + ">");
		}
	}

	/**
	 * Get the string representation of complex type
	 * 
	 * @return key in string
	 */
	public String getKeyString()
	{
		return keyString;
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
	public Image getIcon()
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
