package api.graph;

import java.io.File;
import java.util.List;

import javax.swing.Icon;

import api.models.IFunction;

public interface IProjectNode extends Comparable<IProjectNode>{

	List<IProjectNode> childrens();
	
	Icon getIcon();
	
	int getType();
	
	Object getValue();
	
	default boolean hasFile(){
		return getValue() instanceof File;
	}
	
	default File getFile() throws ClassCastException{
		return (File) getValue();
	}
	
	default IFunction getFunction() throws ClassCastException {
		return (IFunction) getValue();
	}
	
	@Override
	default int compareTo(IProjectNode other) {
		IProjectNode o = (IProjectNode) other;
		if (hasFile() && o.hasFile()){
			boolean d1 = getFile().isDirectory(), d2 = o.getFile().isDirectory();
			return d1 ^ d2 ? (d1 ? -1 : 1) : 0;
		}
		return 0;
	}
	
	final int TYPE_PROJECT = 1,
			TYPE_FOLDER = 2,
			TYPE_FILE = 3,
			TYPE_NAMESPACE = 4,
			TYPE_CLASS = 5,
			TYPE_STRUCT = 6,
			TYPE_FUNCTION = 7;
}
