package api.graph;

import java.util.List;

import javax.swing.Icon;

public interface IProjectNode extends Comparable<IProjectNode>{

	List<IProjectNode> childrens();
	
	Icon getIcon();
}
