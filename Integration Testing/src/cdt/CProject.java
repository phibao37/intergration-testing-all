package cdt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import api.graph.IProjectNode;
import api.parser.BodyParser;
import api.parser.UnitParser;
import core.BaseProject;
import core.models.type.BasicType;

public class CProject extends BaseProject {

	private Map<String, String> mapMarco;
	private Map<File, IProjectNode> mapProjectNode;
	
	public CProject(File root){
		super(root);
		mapMarco = new HashMap<>();
		mapProjectNode = new HashMap<>();
		
		for (BasicType type: BasicType.LIST_BASIC_TYPE)
			addLoadedType(type);
	}

	
	public void addMarco(String key, String value){
		mapMarco.put(key, value);
	}
	
	public Map<String, String> getMarcoMap(){
		return mapMarco;
	}

	@Override
	public UnitParser getUnitParser() {
		return new CUnitParser();
	}


	@Override
	public BodyParser getBodyParser() {
		return new CBodyParser();
	}

	public void putMapProjectStruct(File source, IProjectNode node){
		mapProjectNode.put(source, node);
	}

	@Override
	public Map<File, IProjectNode> getMapProjectStruct() {
		return mapProjectNode;
	}


	@Override
	public boolean accept(File pathname) {
		String name = pathname.getName().toLowerCase();
		return name.endsWith(".c") || name.endsWith(".cpp");
	}

}
