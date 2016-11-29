/**
 * Extended tab pane
 * @file LightTabPane.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.node;

import java.lang.reflect.Constructor;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

/**
 * Extended tab pane to open the tab based on tab data
 * 
 * @author VuSD
 *
 * @date 2016-11-24 VuSD created
 */
public class LightTabPane extends TabPane {

	/**
	 * Create new tab or open current tab based on current data. The opened tab will be selected
	 * 
	 * @param title
	 *            text to show in the tab
	 * @param tip
	 *            the tooltip to show when the user hovers over the tab
	 * @param constructor
	 *            constructor type (Java Reflection) to create new tab if no matching found
	 * @param constructData
	 *            data to be passed to construct new tab content
	 * @return opened tab content
	 */
	@SuppressWarnings("unchecked")
	public <T extends Node> T openTab(String title, String tip, Constructor<T> constructor, Object... constructData)
	{
		Node content = null;
		int selected = -1, index = 0;
		List<Tab> tabs = getTabs();

		for (Tab tab : tabs) {

			// Check if current tab match construct data
			if ((content = tab.getContent()) instanceof EqualsTabConstruct) {
				if (((EqualsTabConstruct) content).equalsConstruct(constructData)) {
					selected = index;
					break;
				}
			}

			index++;
		}

		// No matching tab found, crate new one
		if (selected == -1) {
			selected = tabs.size();

			Tab tab = null;
			try {
				tab = new Tab(title, content = constructor.newInstance(constructData));
				if (tip != null) {
					tab.setTooltip(new Tooltip(tip));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			tabs.add(tab);
		}

		getSelectionModel().select(selected);
		return (T) content;
	}

	/**
	 * Interface for extended node that will be used to compare against opening tab
	 */
	public static interface EqualsTabConstruct {

		/**
		 * Check if given data set match with opening tab component
		 * 
		 * @param constructItem
		 *            list of data to compare
		 * @return matching state
		 */
		public boolean equalsConstruct(Object... constructItem);
	}
}
