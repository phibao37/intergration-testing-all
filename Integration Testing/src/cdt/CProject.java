package cdt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import api.parser.BodyParser;
import api.parser.UnitParser;
import core.AbstractProject;
import core.models.type.BasicType;

public class CProject extends AbstractProject {

	private Map<String, String> mapMarco;
	
	public CProject(File... source){
		super(source);
		mapMarco = new HashMap<>();
		
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

}
