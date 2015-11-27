package cdt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cdt.visitor.CBodyVisitor;
import cdt.visitor.CUnitVisitor;
import core.MainProcess;

public class CMainProcess extends MainProcess {
	
	private Map<String, String> listMarco = new HashMap<>();
	
	public CMainProcess() {
		setUnitVisitor(new CUnitVisitor());
		setBodyVisitor(new CBodyVisitor());
	}
	
	@Override
	public boolean accept(File dir, String name) {
		name = name.toLowerCase();
		return name.endsWith(".c") || name.endsWith(".cpp");
	}
	
	public void addMarcoDefine(String key, String expand){
		listMarco.put(key, expand);
	}
	
	public Map<String, String> getListMarco(){
		return listMarco;
	}
	
}
