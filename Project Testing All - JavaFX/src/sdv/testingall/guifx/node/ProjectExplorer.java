/**
 * Project view implement
 * @file ProjectExplorer.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.node;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import sdv.testingall.core.node.INode;

/**
 * Project view class
 * 
 * @author VuSD
 *
 * @date 2016-11-23 VuSD created
 */
public class ProjectExplorer extends TreeExplorer<INode> {

	@Override
	protected boolean canHasChild(INode item)
	{
		return item.size() > 0;
	}

	@Override
	protected Iterable<INode> iterChildItems(INode parentItem)
	{
		return parentItem;
	}

	@Override
	protected Image renderIcon(INode item, boolean isEditing, boolean isFocused, boolean isSelected, boolean isHover)
	{
		return item.getIcon();
	}

	@Override
	protected void processCellFactory(TreeCell<INode> cell, INode item)
	{
		// Process and display tool-tip
		String des = item.getDescription();
		if (des == null || des.isEmpty()) {
			cell.setTooltip(null);
		} else {
			Tooltip tip = cell.getTooltip();
			if (tip == null) {
				cell.setTooltip(tip = new Tooltip());
			}
			tip.setText(item.getDescription());
		}

	}

}
