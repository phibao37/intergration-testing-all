package cdt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import api.parser.IProjectParser;
import core.BaseProject;
import core.models.type.BasicType;

public class CProject extends BaseProject {

	private Map<String, String> mapMarco;
	
	public CProject(File root){
		super(root);
	}

	@Override
	protected void loadProject() {
		mapMarco = new HashMap<>();

		for (BasicType type: BasicType.LIST_BASIC_TYPE)
			addLoadedType(type);
		
		super.loadProject();
	}



	public void addMarco(String key, String value){
		mapMarco.put(key, value);
	}
	
	public Map<String, String> getMarcoMap(){
		return mapMarco;
	}

	@Override
	public IProjectParser getProjectParser() {
		return new CProjectParser();
	}

	@Override
	public boolean accept(File pathname) {
		String name = pathname.getName().toLowerCase();
		return name.endsWith(".c") || name.endsWith(".cpp");
	}

}
