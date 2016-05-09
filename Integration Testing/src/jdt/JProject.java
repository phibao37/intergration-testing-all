package jdt;

import java.io.File;
import api.parser.IProjectParser;
import core.BaseProject;
import core.models.type.BasicType;

public class JProject extends BaseProject {
	
	public JProject(File root){
		super(root);
	}

	@Override
	protected void loadProject() {

		for (BasicType type: BasicType.LIST_BASIC_TYPE)
			addLoadedType(type);
		addLoadedType(new BasicType("boolean", false, BasicType.BOOL_SIZE));
		
		super.loadProject();
	}

	@Override
	public IProjectParser getProjectParser() {
		return new JProjectParser();
	}

	@Override
	public boolean accept(File pathname) {
		String name = pathname.getName().toLowerCase();
		return name.endsWith(".java");
	}
}
