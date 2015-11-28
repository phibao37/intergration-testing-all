package graph.swing;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.net.InetAddress;
import java.util.Comparator;
import java.util.LinkedList;
import javax.swing.ImageIcon;
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
				TreeNode first = (TreeNode) getChildAt(0);
				return first.getUserObject() == null && first.getFile() == null;
			} else
				return false;
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
	
	private TreeNode mRoot;
	private Config mCf;
	
	public FileExplorer(){
		setConfig(Config.DEFAULT);
		
		String hostName = DEFAULT_HOSTNAME;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (Exception x) {}
		mRoot = new TreeNode(hostName);
		setModel(new DefaultTreeModel(mRoot));
		setCellRenderer(new TreeCellRender());
		loadFiles(File.listRoots(), mRoot);
		expandRow(0);
		
		addTreeExpansionListener(new TreeExpansionListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				TreeNode node = (TreeNode) event.getPath().getLastPathComponent();
				DefaultTreeModel model = (DefaultTreeModel) treeModel;
				
				if (node.isDirectory() && node.isNotLoadedDirectory()){
					loadFiles(node.getFile().listFiles(FileExplorer.this), node);
					model.reload(node);
				}
			}
			
			public void treeCollapsed(TreeExpansionEvent event) {}
		});
	}
	
	public FileExplorer(File path){
		this();
		setSelectedPath(path);
	}
	
	public FileExplorer(String path){
		this(new File(path));
	}
	
	public void setSelectedPath(File path) throws NullPointerException{
		selectionModel.setSelectionPath(expandToPath(path));
	}

	private void loadFiles(File[] files, TreeNode node){
		node.removeAllChildren();
		for (File file: files){
			TreeNode child = new TreeNode(file);
			node.add(child);
		}
		node.sortChild(this);
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
				TreeNode child = (TreeNode) trace.getChildAt(i);
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
