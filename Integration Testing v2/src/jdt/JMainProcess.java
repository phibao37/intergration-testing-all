package jdt;

import java.io.File;

import core.MainProcess;

public class JMainProcess extends MainProcess {
	
	public JMainProcess() {
		setUnitVisitor(new JUnitVisitor());
		setBodyVisitor(new JBodyVisitor());
	}
	
	@Override
	public boolean accept(File dir, String name) {
		name = name.toLowerCase();
		return name.endsWith(".java");
	}

}
