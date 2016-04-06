package graph.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Trình duyệt theo dạng cây
 */
@SuppressWarnings("unchecked")
public abstract class TreeExplorer<E> extends JTree 
		implements Comparator<E> {
	
	class TreeNode extends DefaultMutableTreeNode{
		
		private static final long serialVersionUID = 1L;
		
		private E item;
		
		private TreeNode() {}
		
		public TreeNode(E item){
			this.item = item;
			setUserObject(item);
			
			if (hasItemChild(item)){
				add(new TreeNode());
			}
		}
		
		public boolean hasItem(){
			return item != null;
		}
		
		public E getItem(){
			return item;
		}
		
		@Override
		public TreeNode getChildAt(int index) {
			return (TreeNode) super.getChildAt(index);
		}

		public void sortChild(Comparator<TreeNode> c){
			if (children != null)
				children.sort(c);
		}
		
		public boolean isPlaceHolderNode(){
			return getChildCount() == 1 && !((TreeNode)getChildAt(0)).hasItem();
		}
		
	}
	
	class TreeCellRender extends DefaultTreeCellRenderer{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component c = super.getTreeCellRendererComponent(tree, value, 
					sel, expanded, leaf, row, hasFocus);
			TreeNode node = (TreeNode) value;
			E item = node.getItem();
			
			if (node.hasItem()){
				setIcon(renderIcon(item, sel, expanded, leaf, row, hasFocus));
				setText(renderText(item, sel, expanded, leaf, row, hasFocus));
				
				if (mSetTooltips)
					setToolTipText(getText());
			}
			return c;
		}
	}
	
	protected Icon renderIcon(E item, boolean selected, boolean expanded, 
			boolean leaf, int row, boolean hasFocus){
		return null;
	}
	
	protected String renderText(E item, boolean selected, boolean expanded, 
			boolean leaf, int row, boolean hasFocus) {
		return super.convertValueToText(item, selected, expanded, 
				leaf, row, hasFocus);
	}

	public interface MenuHandle<T> extends Consumer<JPopupMenu>{
		public void acceptList(List<T> items);
	}
	
	class PopupMenu extends JPopupMenu{
		private static final long serialVersionUID = 1L;

		@Override
		public void show(Component invoker, int x, int y) {
			TreePath select = getPathForLocation(x, y);
			
			if (select == null){
				if (mMenuHandle != null)
					mMenuHandle.acceptList(Collections.EMPTY_LIST);
				setSelectionPaths(null);
			} else {
				boolean findPath = false;
				TreePath[] selects = getSelectionPaths();
				
				if (getSelectionCount() > 0)
					for (TreePath p: selects)
						if (p.equals(select)){
							findPath = true;
							break;
						}
				if (!findPath){
					setSelectionPath(select);
					selects = new TreePath[]{select};
				}
				
				if (mMenuHandle != null){
					List<E> items = new ArrayList<E>(selects.length);
					
					for (int i = 0; i < selects.length; i++){
						TreeNode node = (TreeNode) selects[i]
								.getLastPathComponent();
						items.add(node.getItem());
					}
					mMenuHandle.acceptList(items);
				}
			}
			super.show(invoker, x, y);
		}
		
	}
	
	private DefaultTreeModel mTreeModel;
	private TreeNode mRoot;
	
	private PopupMenu mMenu;
	private MenuHandle<E> mMenuHandle;
	private boolean mSetTooltips;
	
	protected abstract boolean hasItemChild(E item);
	
	public TreeExplorer(){
		setModel(null);
		addTreeExpansionListener(new TreeExpansionListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				TreeNode node = (TreeNode) event.getPath()
						.getLastPathComponent();
				
				if (node.isPlaceHolderNode()){
					loadItems(node.getItem(), node);
				}
			}
			
			public void treeCollapsed(TreeExpansionEvent event) {}
		});
		
//		addKeyListener(new KeyAdapter(){
//			
//			@Override
//			public void keyPressed(KeyEvent e) {
//				switch (e.getKeyCode()){
//				case KeyEvent.VK_F5:
//					refresh();
//					break;
//				}
//			}
//			
//		});
		setComponentPopupMenu(mMenu = new PopupMenu());
	}
	
	public TreeExplorer(E root){
		this();
		setRoot(root);
	}
	
	public void setRoot(E root){
		mRoot = new TreeNode(root);
		setModel(mTreeModel = new DefaultTreeModel(mRoot));
		loadItems(root, mRoot);
		expandRow(0);
		setCellRenderer(new TreeCellRender());
	}
	
	/**
	 * Trả về mục đầu tiên đang được chọn, hoặc null nếu không có mục được chọn
	 */
	public E getSelectedItem(){
		if (getSelectionCount() == 0)
			return null;
		TreeNode node = (TreeNode) getSelectionPath().getLastPathComponent();
		return node.getItem();
	}
	
	public void setTooltipsEnable(boolean enable){
		this.mSetTooltips = enable;
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	/**
	 * Trả về danh sách mục đang được chọn, có thể là tập rỗng
	 */
	public List<E> getSelectedItems(){
		int count = getSelectionCount();
		TreePath[] paths = getSelectionPaths();
		List<E> items = new ArrayList<>(count);
		
		for (int i = 0; i < paths.length; i++){
			TreeNode node = (TreeNode) paths[i].getLastPathComponent();
			items.add(node.getItem());
		}
		return items;
	}
	
	/**
	 * Đặt điều khiển menu chuột phải
	 */
	public void setMenuHandle(MenuHandle<E> handle){
		mMenuHandle = handle;
		mMenu.removeAll();
		handle.accept(mMenu);
	}
	
	/**
	 * Làm mới trình duyệt tập tin
	 */
//	public void refresh(){
//		ArrayList<File> listExpanded = new ArrayList<>();
//		ArrayList<File> selected = new ArrayList<>();
//		TreePath[] listSelected = getSelectionPaths();
//		TreePath rootPath = new TreePath(mRoot);
//		Enumeration<TreePath> iter = getExpandedDescendants(rootPath);
//		boolean rootSelect = isPathSelected(rootPath);
//		
//		for (int i = 0; i < listSelected.length; i++){
//			TreeNode<E> node = (TreeNode<E>) listSelected[i].getLastPathComponent();
//			if (node.hasItem())
//				selected.add(node.getFile());
//		}
//		
//		if (iter != null)
//		while (iter.hasMoreElements()){
//			TreeNode node = (TreeNode) iter.nextElement().getLastPathComponent();
//			if (node.hasFile())
//				listExpanded.add(node.getFile());
//		}
//		
//		loadFiles(File.listRoots(), mRoot);
//		
//		for (File path: listExpanded)
//			if (path.exists())
//				expandToPath(path);
//		
//		if (rootSelect)
//			setSelectionPath(rootPath);
//		else
//			setSelectedPath(selected.toArray(new File[selected.size()]));
//	}
	
//	public void setSelectedPath(File path) throws NullPointerException{
//		setSelectionPath(expandToPath(path));
//	}
//	
//	public void setSelectedPath(File[] paths){
//		ArrayList<TreePath> selectPath = new ArrayList<>();
//		for (File path: paths){
//			if (path.exists())
//				selectPath.add(expandToPath(path));
//		}
//		setSelectionPaths(selectPath.toArray(new TreePath[selectPath.size()]));
//	}
	
	protected abstract Iterable<E> iterItemChilds(E parent);

	private void loadItems(E parent, TreeNode node){
		node.removeAllChildren();
		for (E item: iterItemChilds(parent)){
			TreeNode child = new TreeNode(item);
			node.add(child);
		}
		node.sortChild((n1, n2) -> compare(n1.getItem(), n2.getItem()));
		mTreeModel.reload(node);
	}
	
	public void addItemClickListener(ItemClickListener<E> listenr){
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				int row = getRowForLocation(e.getX(), e.getY());
				if (row == -1) return;
				
				TreeNode node = (TreeExplorer<E>.TreeNode) getPathForRow(row)
						.getLastPathComponent();
				listenr.itemClicked(node.getItem(), e.getClickCount());
			}
			
		});
	}
	
//	private TreePath expandToPath(File path) throws NullPointerException {
//		LinkedList<File> fileHirachy = new LinkedList<>();
//		File traceParent = path;
//		
//		while (traceParent != null){
//			fileHirachy.push(traceParent);
//			traceParent = traceParent.getParentFile();
//		}
//		
//		TreeNode trace, find, pathArr[] = new TreeNode[fileHirachy.size() + 1];
//		int index = 1;
//		
//		pathArr[0] = trace = mRoot;
//		for (File file: fileHirachy){
//			find = null;
//			
//			for (int i = 0; i < trace.getChildCount(); i++){
//				TreeNode child = trace.getChildAt(i);
//				if (file.equals(child.getFile())){
//					find = child;
//					break;
//				}
//			}
//			
//			pathArr[index++] = trace = find;
//			if (trace.isNotLoadedDirectory())
//				loadFiles(file.listFiles(this), trace);
//		}
//		
//		TreePath tPath = new TreePath(pathArr);
//		expandPath(tPath);
//		return tPath;
//	}

	@Override
	public int compare(E i1, E i2) {
		return 0;
	}

	public interface ItemClickListener<T>{
		
		void itemClicked(T item, int count);
	}

	private static final long serialVersionUID = 1L;
	
}