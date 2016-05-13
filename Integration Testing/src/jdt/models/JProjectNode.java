package jdt.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import api.IProject;
import api.graph.IProjectNode;
import api.models.IFunction;
import core.models.type.ObjectType;

public class JProjectNode implements IProjectNode {

	public static final int TYPE_PROJECT = 1;
	public static final int TYPE_FOLDER = 2;
	public static final int TYPE_FILE = 3;
	public static final int TYPE_CLASS = 5;
	public static final int TYPE_FUNCTION = 7;
	
	private static final ImageIcon 
		ICON_FOLDER = 
			new ImageIcon(JProjectNode.class.getResource("/image/folder.png")),
		ICON_TEXT = 
			new ImageIcon(JProjectNode.class.getResource("/image/text-file.png")),
		ICON_PROJECT = 
			new ImageIcon(JProjectNode.class.getResource("/image/project.png")),
		ICON_PUNCTION = 
			new ImageIcon(JProjectNode.class.getResource("/image/function.png")),
		ICON_CLASS = 
			new ImageIcon(JProjectNode.class.getResource("/image/class.png"));
	
	private Object value;
	private List<IProjectNode> childs;
	private int type;
	private Icon icon;
	private IProject project;
	private String render;
	
	public JProjectNode() {}
	
	public JProjectNode(File file, int type, IProject project){
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
	
	public JProjectNode(ObjectType clsType){
		value = clsType;
		this.type = TYPE_CLASS;
		render = clsType.getContent();
		icon = ICON_CLASS;
	}
	
	public JProjectNode(IFunction fn){
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
					childs.add(new JProjectNode(files[i], 
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
