package graph.swing;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Trình duyệt tập tin theo dạng cây
 */
public class FileExplorer extends JTree 
		implements Comparator<FileExplorer.TreeNode>, FileFilter{
	
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_HOSTNAME = "Computer";
	
	static class TreeNode extends DefaultMutableTreeNode{
		
		private static final long serialVersionUID = 1L;
		private static final ImageIcon ICON_FOLDER = 
				new ImageIcon(TreeNode.class.getResource("/image/folder.png"));
		private static final ImageIcon ICON_FOLDER_OPEN = 
				new ImageIcon(TreeNode.class.getResource("/image/folder-open.png"));
		private static final ImageIcon ICON_COMPUTER = 
				new ImageIcon(TreeNode.class.getResource("/image/computer.png"));
		private static final ImageIcon ICON_TEXT = 
				new ImageIcon(TreeNode.class.getResource("/image/text-file.png"));
		
		private File mFile;
		private ImageIcon mExpand, mCollapse;
		
		TreeNode() {}
		
		public TreeNode(String label){
			super(label);
			mExpand = mCollapse = ICON_COMPUTER;
		}
		
		public TreeNode(File file){
			mFile = file;
			String name = file.getName();
			if (name.isEmpty())
				name = file.getAbsolutePath();
			setUserObject(name);
			
			if (mFile.isDirectory()){
				add(new TreeNode());
				mExpand = ICON_FOLDER_OPEN;
				mCollapse = ICON_FOLDER;
			}
			else {
				mExpand = mCollapse = ICON_TEXT;
			}
		}
		
		public boolean hasFile(){
			return mFile != null;
		}
		
		public File getFile(){
			return mFile;
		}
		
		public ImageIcon getExpandedIcon(){
			return mExpand;
		}
		
		public ImageIcon getCollapsedIcon(){
			return mCollapse;
		}
		
		public boolean isDirectory(){
			return mFile != null && mFile.isDirectory();
		}
		
		public boolean isNotLoadedDirectory(){
			if (getChildCount() == 1){
				TreeNode first = getChildAt(0);
				return first.getUserObject() == null && first.getFile() == null;
			} else
				return false;
		}
		
		@Override
		public TreeNode getChildAt(int index) {
			return (TreeNode) super.getChildAt(index);
		}

		@SuppressWarnings("unchecked")
		public void sortChild(Comparator<TreeNode> c){
			if (children != null)
				children.sort(c);
		}
		
	}
	
	static class TreeCellRender extends DefaultTreeCellRenderer{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component c = super.getTreeCellRendererComponent(tree, value, 
					sel, expanded, leaf, row, hasFocus);
			TreeNode node = (TreeNode) value;
			
			setIcon(expanded ? node.getExpandedIcon() : node.getCollapsedIcon());
			return c;
		}
	}
	
	public static interface MenuHandle extends Consumer<JPopupMenu>{
		public void accept(File...files);
	}
	
	class PopupMenu extends JPopupMenu{
		private static final long serialVersionUID = 1L;

		@Override
		public void show(Component invoker, int x, int y) {
			TreePath select = getPathForLocation(x, y);
			
			if (select == null){
				if (mMenuHandle != null)
					mMenuHandle.accept();
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
					File[] files = new File[selects.length];
					
					for (int i = 0; i < files.length; i++){
						TreeNode node = (TreeNode) selects[i].getLastPathComponent();
						files[i] = node.getFile();
					}
					mMenuHandle.accept(files);
				}
			}
			super.show(invoker, x, y);
		}
		
	}
	
	private DefaultTreeModel mTreeModel;
	private TreeNode mRoot;
	private Config mCf;
	
	private PopupMenu mMenu;
	private MenuHandle mMenuHandle;
	
	public FileExplorer(File path){
		this();
		setSelectedPath(path);
	}
	
	public FileExplorer(String path){
		this(new File(path));
	}
	
	public FileExplorer(){
		setConfig(Config.DEFAULT);
		
		String hostName = DEFAULT_HOSTNAME;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (Exception x) {}
		mRoot = new TreeNode(hostName);
		setModel(mTreeModel = new DefaultTreeModel(mRoot));
		setCellRenderer(new TreeCellRender());
		loadFiles(File.listRoots(), mRoot);
		expandRow(0);
		
		addTreeExpansionListener(new TreeExpansionListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				TreeNode node = (TreeNode) event.getPath().getLastPathComponent();
				
				if (node.isDirectory() && node.isNotLoadedDirectory()){
					loadFiles(node.getFile().listFiles(FileExplorer.this), node);
				}
			}
			
			public void treeCollapsed(TreeExpansionEvent event) {}
		});
		addKeyListener(new KeyAdapter(){
			
			@Override
			public void keyPressed(KeyEvent e) {
				boolean isCtrk = e.isControlDown();
				
				switch (e.getKeyCode()){
				case KeyEvent.VK_F5:
					refresh();
					break;
				case KeyEvent.VK_C:
					if (isCtrk);
				}
			}
			
		});
		setComponentPopupMenu(mMenu = new PopupMenu());
	}
	
	/**
	 * Trả về tập tin đầu tiên đang được chọn, hoặc null nếu không có mục được chọn
	 */
	public File getSelectedFile(){
		if (getSelectionCount() == 0)
			return null;
		TreeNode node = (TreeNode) getSelectionPath().getLastPathComponent();
		return node.getFile();
	}
	
	/**
	 * Trả về danh sách tập tin đang được chọn, có thể là tập rỗng
	 */
	public File[] getSelectedFiles(){
		if (getSelectionCount() == 0)
			return new File[0];
		TreePath[] paths = getSelectionPaths();
		File[] files = new File[paths.length];
		
		for (int i = 0; i < paths.length; i++){
			TreeNode node = (TreeNode) paths[i].getLastPathComponent();
			files[i] = node.getFile();
		}
		return files;
	}
	
	/**
	 * Đặt điều khiển menu chuột phải
	 */
	public void setMenuHandle(MenuHandle handle){
		mMenuHandle = handle;
		mMenu.removeAll();
		handle.accept(mMenu);
	}
	
	/**
	 * Làm mới trình duyệt tập tin
	 */
	public void refresh(){
		ArrayList<File> listExpanded = new ArrayList<>();
		ArrayList<File> selected = new ArrayList<>();
		TreePath[] listSelected = getSelectionPaths();
		TreePath rootPath = new TreePath(mRoot);
		Enumeration<TreePath> iter = getExpandedDescendants(rootPath);
		boolean rootSelect = isPathSelected(rootPath);
		
		for (int i = 0; i < listSelected.length; i++){
			TreeNode node = (TreeNode) listSelected[i].getLastPathComponent();
			if (node.hasFile())
				selected.add(node.getFile());
		}
		
		if (iter != null)
		while (iter.hasMoreElements()){
			TreeNode node = (TreeNode) iter.nextElement().getLastPathComponent();
			if (node.hasFile())
				listExpanded.add(node.getFile());
		}
		
		loadFiles(File.listRoots(), mRoot);
		
		for (File path: listExpanded)
			if (path.exists())
				expandToPath(path);
		
		if (rootSelect)
			setSelectionPath(rootPath);
		else
			setSelectedPath(selected.toArray(new File[selected.size()]));
	}
	
	public void setSelectedPath(File path) throws NullPointerException{
		setSelectionPath(expandToPath(path));
	}
	
	public void setSelectedPath(File[] paths){
		ArrayList<TreePath> selectPath = new ArrayList<>();
		for (File path: paths){
			if (path.exists())
				selectPath.add(expandToPath(path));
		}
		setSelectionPaths(selectPath.toArray(new TreePath[selectPath.size()]));
	}

	private void loadFiles(File[] files, TreeNode node){
		node.removeAllChildren();
		for (File file: files){
			TreeNode child = new TreeNode(file);
			node.add(child);
		}
		node.sortChild(this);
		mTreeModel.reload(node);
	}
	
	private TreePath expandToPath(File path) throws NullPointerException {
		LinkedList<File> fileHirachy = new LinkedList<>();
		File traceParent = path;
		
		while (traceParent != null){
			fileHirachy.push(traceParent);
			traceParent = traceParent.getParentFile();
		}
		
		TreeNode trace, find, pathArr[] = new TreeNode[fileHirachy.size() + 1];
		int index = 1;
		
		pathArr[0] = trace = mRoot;
		for (File file: fileHirachy){
			find = null;
			
			for (int i = 0; i < trace.getChildCount(); i++){
				TreeNode child = trace.getChildAt(i);
				if (file.equals(child.getFile())){
					find = child;
					break;
				}
			}
			
			pathArr[index++] = trace = find;
			if (trace.isNotLoadedDirectory())
				loadFiles(file.listFiles(this), trace);
		}
		
		TreePath tPath = new TreePath(pathArr);
		expandPath(tPath);
		return tPath;
	}
	
	public void setConfig(Config cf){
		mCf = cf;
		setRootVisible(cf.showRoot);
	}

	@Override
	public int compare(TreeNode o1, TreeNode o2) {
		
		//Sort by folder first
		if (mCf.showFolderFirst){
			boolean o1d = o1.isDirectory(),
					o2d = o2.isDirectory();
			return o1d ^ o2d ? (o1d ? -1 : 1) : 0;
		}
		
		return 0;
	}
	
	@Override
	public boolean accept(File file) {
		
		//Ignore hidden file
		if (!mCf.showHiddenFile && file.isHidden())
			return false;
		
		return true;
	}
	
	public static class Config{	
		static final Config DEFAULT = new Config();
		
		public boolean showHiddenFile = false;
		public boolean showFolderFirst = true;
		public boolean showRoot = true;
	}
	
}
