package graph.swing;

import javax.swing.Icon;

import api.graph.IProjectNode;

public class ProjectExplorer extends TreeExplorer<IProjectNode> {
	private static final long serialVersionUID = 1L;

	public ProjectExplorer() {}
	
	public ProjectExplorer(IProjectNode root){
		super(root);
	}
	
	@Override
	protected boolean hasItemChild(IProjectNode item) {
		return item.childrens().size() > 0;
	}

	@Override
	protected Iterable<IProjectNode> iterItemChilds(IProjectNode parent) {
		return parent.childrens();
	}

	@Override
	protected Icon renderIcon(IProjectNode item, boolean selected, 
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		return item.getIcon();
	}

	@Override
	public int compare(IProjectNode i1, IProjectNode i2) {
		return i1.compareTo(i2);
	}

}
