package cdt.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

import api.IProject;
import api.graph.IProjectNode;
import api.models.IFunction;

public class CProjectNode implements IProjectNode {
	
	private static final ImageIcon 
		ICON_FOLDER = 
			new ImageIcon(CProjectNode.class.getResource("/image/folder.png")),
		ICON_TEXT = 
			new ImageIcon(CProjectNode.class.getResource("/image/text-file.png")),
		ICON_PROJECT = 
			new ImageIcon(CProjectNode.class.getResource("/image/project.png")),
		ICON_NS = 
			new ImageIcon(CProjectNode.class.getResource("/image/namespace.png")),
		ICON_PUNCTION = 
			new ImageIcon(CProjectNode.class.getResource("/image/function.png")),
		ICON_STRUCT = 
			new ImageIcon(CProjectNode.class.getResource("/image/struct.png")),
		ICON_CLASS = 
			new ImageIcon(CProjectNode.class.getResource("/image/class.png"));
	
	private Object value;
	private List<IProjectNode> childs;
	private int type;
	private Icon icon;
	private IProject project;
	private String render;
	
	public CProjectNode() {}
	
	public CProjectNode(File file, int type, IProject project){
		value = file;
		this.type = type;
		this.project = project;
		
		String s = file.getName();
		render = s.isEmpty() ? file.getAbsolutePath() : s;
		
		switch (type){
		case TYPE_PROJECT:
			icon = ICON_PROJECT;
			break;
		case TYPE_FOLDER:
			icon = ICON_FOLDER;
			break;
		case TYPE_FILE:
			icon = ICON_TEXT;
			break;
		}
	}
	
	public CProjectNode(ICPPASTNamespaceDefinition ns){
		value = ns;
		type = TYPE_NAMESPACE;
		icon = ICON_NS;
		String nsName = ns.getName().toString();
		render = nsName.isEmpty() ? "[namespace]" : nsName;
	}
	
	public CProjectNode(IASTSimpleDeclaration dec, int type, String name){
		value = dec;
		this.type = type;
		render = name;
		icon = type == TYPE_CLASS ? ICON_CLASS : ICON_STRUCT;
	}
	
	public CProjectNode(IFunction fn){
		value = fn;
		type = TYPE_FUNCTION;
		icon = ICON_PUNCTION;
		render = fn.getContent();
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	public List<IProjectNode> childrens() {
		if (childs == null){
			
			if (type == TYPE_PROJECT || type == TYPE_FOLDER){
				File[] files = getFile().listFiles(file -> {
					if (file.isHidden())
						return false;
					if (file.isFile() && !project.accept(file))
						return false;
					return true;
				});
				childs = new ArrayList<>(files.length);
				
				for (int i = 0; i < files.length; i++)
					childs.add(new CProjectNode(files[i], 
							files[i].isDirectory() ? TYPE_FOLDER : TYPE_FILE,
									project));
			}
			
			else if (type == TYPE_FILE){
				IProjectNode node = project.getMapProjectStruct().get(getValue());
				
				if (node != null)
					childs = node.childrens();
			}
			
			if (childs == null)
				childs = new ArrayList<>();
			
		}
		return childs;
	}
	
	@Override
	public int getType(){
		return type;
	}
	
	public void addChild(IProjectNode node){
		childrens().add(node);
	}

	@Override
	public Icon getIcon() {
		return icon;
	}
	
	@Override
	public String toString() {
		return render;
	}

}
