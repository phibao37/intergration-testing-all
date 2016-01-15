package cdt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import core.Main;

public class CMain extends Main {

	private Map<String, String> mapMarco;
	
	public CMain(File... source){
		super(source);
		mapMarco = new HashMap<>();
	}

	@Override
	public void parseUnit(File source) {
		new CUnitParser(source, this);
	}
	
	public void addMarco(String key, String value){
		mapMarco.put(key, value);
	}
	
	public Map<String, String> getMarcoMap(){
		return mapMarco;
	}

}
