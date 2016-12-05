/**
 * Tree view implementation
 * @file TreeExplorer.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Tree view that load and show node dynamically
 * 
 * @author VuSD
 *
 * @param <E>
 *            type of tree node
 * @date 2016-11-22 VuSD created
 */
public abstract class TreeExplorer<E> extends TreeView<E> implements Comparator<E> {

	private ContextMenuHandle<E> menuHandle = list -> false;

	/**
	 * A node that live inside a TreeExplorer
	 * 
	 * @date 2016-11-23 VuSD created
	 */
	class TreeNode extends TreeItem<E> {

		/**
		 * Create new node with data
		 * 
		 * @param item
		 *            data value
		 */
		TreeNode(E item)
		{
			super(item);

			// Append a placeholder node if this node can has children
			if (canHasChild(item)) {
				getChildren().add(new TreeNode());

				// Register expanding event for first time to load child content
				expandedProperty().addListener((observable, oldValue, newValue) -> {
					if (isPlaceHolder()) {
						loadChildItems(getValue(), TreeNode.this);
					}
				});
			}
		}

		/**
		 * Create a placeholder node
		 */
		TreeNode()
		{
			super((E) null);
		}

		/**
		 * Check if this node is parent for placeholder
		 * 
		 * @return placeholder state
		 */
		boolean isPlaceHolder()
		{
			ObservableList<TreeItem<E>> childs = getChildren();
			return childs.size() == 1 && childs.get(0).getValue() == null;
		}
	}

	/**
	 * Factory to create a tree item
	 *
	 * @date 2016-11-23 VuSD created
	 */
	protected class TreeCellImpl extends TreeCell<E> {

		@Override
		protected void updateItem(E item, boolean empty)
		{
			super.updateItem(item, empty);

			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(renderText(item, isEditing(), isFocused(), isSelected(), isHover()));
				Image icon = renderIcon(item, isEditing(), isFocused(), isSelected(), isHover());

				if (icon == null) {
					setGraphic(null);
				} else {
					ImageView iconNode = (ImageView) getGraphic();
					if (iconNode == null) {
						iconNode = new ImageView(icon);
						setGraphic(iconNode);
					} else {
						iconNode.setImage(icon);
					}
				}

				processCellFactory(this, item);
			}

		}

	}

	/**
	 * Create a blank default tree
	 */
	public TreeExplorer()
	{
		setCellFactory(tree -> new TreeCellImpl());
		setContextMenu(new ContextMenu() {

			/**
			 * Process the handle
			 * 
			 * @return should display the menu
			 */
			protected boolean processMenuHandle()
			{
				List<TreeItem<E>> selected = getSelectionModel().getSelectedItems();
				ArrayList<E> list = new ArrayList<>(selected.size());

				for (TreeItem<E> treeItem : selected) {
					list.add(treeItem.getValue());
				}
				if (menuHandle.handle(list)) {
					for (MenuItem item : getItems()) {
						if (item.isVisible()) {
							return true;
						}
					}
					return false;
				} else {
					return false;
				}
			}

			@Override
			public void show(Node anchor, double screenX, double screenY)
			{
				if (processMenuHandle()) {
					super.show(anchor, screenX, screenY);
				}
			}

			@Override
			public void show(Node anchor, Side side, double dx, double dy)
			{
				if (processMenuHandle()) {
					super.show(anchor, side, dx, dy);
				}
			}

		});
	}

	/**
	 * Create new tree explorer with root element
	 * 
	 * @param root
	 *            root element
	 */
	public TreeExplorer(E root)
	{
		this();
		setRoot(root);
	}

	/**
	 * Set the root element for tree
	 * 
	 * @param root
	 *            root element
	 */
	public void setRoot(E root)
	{
		TreeNode rootNode = new TreeNode(root);
		setRoot(rootNode);
		loadChildItems(root, rootNode);
		rootNode.setExpanded(true);
	}

	/**
	 * Load all children inside a parent item and add corresponding child node to parent node
	 * 
	 * @param parentItem
	 *            item to look children inside
	 * @param parentNode
	 *            node to add child node to
	 */
	protected void loadChildItems(E parentItem, TreeNode parentNode)
	{
		ObservableList<TreeItem<E>> childs = parentNode.getChildren();

		// Clear old child
		childs.clear();

		// Load new child
		for (E childItem : iterChildItems(parentItem)) {
			if (shouldDisplayInTree(childItem)) {
				childs.add(new TreeNode(childItem));
			}
		}

		// Sort the child
		childs.sort((item1, item2) -> compare(item1.getValue(), item2.getValue()));
	}

	/**
	 * Check whether this item can has children inside. Note that if a item <b>can</b> has child, it may still has no
	 * children
	 * 
	 * @param item
	 *            item to check if can has children
	 * @return has children state
	 */
	protected abstract boolean canHasChild(E item);

	/**
	 * Get the list of children item from given parent item
	 * 
	 * @param parentItem
	 * @return
	 */
	protected abstract Iterable<E> iterChildItems(E parentItem);

	/**
	 * Check if an item should be display in tree
	 * 
	 * @param item
	 *            item to check
	 * @return display status
	 */
	protected boolean shouldDisplayInTree(E item)
	{
		return true;
	}

	/**
	 * Get the rendered text for an item in tree
	 * 
	 * @param item
	 *            item to render text
	 * @param isEditing
	 *            this item is in editing mode
	 * @param isFocused
	 *            this item is focused
	 * @param isSelected
	 *            this item is selected
	 * @param isHover
	 *            this item is hovered
	 * @return
	 */
	protected String renderText(E item, boolean isEditing, boolean isFocused, boolean isSelected, boolean isHover)
	{
		return item.toString();
	}

	/**
	 * Get the rendered icon for an item in tree
	 * 
	 * @param item
	 *            item to render icon
	 * @param isEditing
	 *            this item is in editing mode
	 * @param isFocused
	 *            this item is focused
	 * @param isSelected
	 *            this item is selected
	 * @param isHover
	 *            this item is hovered
	 * @return
	 */
	protected Image renderIcon(E item, boolean isEditing, boolean isFocused, boolean isSelected, boolean isHover)
	{
		return null;
	}

	/**
	 * Process custom work when a cell is being constructed
	 * 
	 * @param cell
	 *            cell object
	 * @param item
	 *            cell item data
	 */
	protected void processCellFactory(TreeCell<E> cell, E item)
	{
		// default do nothing
	}

	@Override
	public int compare(E o1, E o2)
	{
		return 0;
	}

	/**
	 * Set the handle for context menu
	 * 
	 * @param menuHandle
	 *            the handle object
	 */
	public void setMenuHandle(@NonNull ContextMenuHandle<E> menuHandle)
	{
		this.menuHandle = menuHandle;
	}

	/**
	 * Interface to handle with TreeExplorer context menu
	 *
	 * @param <T>
	 *            item type in tree explorer
	 * @date 2016-12-05 VuSD created
	 */
	public static interface ContextMenuHandle<T> {

		/**
		 * Called when context menu has been requested
		 * 
		 * @param item
		 *            list of selected item
		 * @return should display context menu
		 */
		boolean handle(List<T> item);
	}

}
