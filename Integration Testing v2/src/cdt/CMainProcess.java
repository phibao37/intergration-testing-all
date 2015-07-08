package cdt;

import java.io.File;

import cdt.visitor.CBodyVisitor;
import cdt.visitor.CUnitVisitor;
import core.MainProcess;

public class CMainProcess extends MainProcess {
	
	public CMainProcess() {
		setUnitVisitor(new CUnitVisitor());
		setBodyVisitor(new CBodyVisitor());
	}
	
	@Override
	public boolean accept(File dir, String name) {
		name = name.toLowerCase();
		return name.endsWith(".c") || name.endsWith(".cpp");
	}
	
}
