package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cdt.CMain;
import api.IMain;
import api.models.IBasisPath;
import api.models.IFunction;
import api.models.IVariable;

public abstract class Main implements IMain {
	
	private List<IFunction> listFunction;
	private List<IVariable> listGlobalVar;

	public Main(File... sources){
		listFunction = new ArrayList<>();
		listGlobalVar = new ArrayList<>();
		
		for (File source: sources)
			parseUnit(source);
	}

	@Override
	public List<IFunction> getFunctions() {
		return listFunction;
	}

	@Override
	public void addFunction(IFunction function) {
		listFunction.add(function);
	}
	
	@Override
	public List<IVariable> getGlobalVars() {
		return listGlobalVar;
	}

	@Override
	public void addGlobalVar(IVariable global) {
		listGlobalVar.add(global);
	}

	@Override
	public List<IBasisPath> testFunction(IFunction function) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args){
		File f = new File("D:/Documents/Unit/delta2.cpp");
		Main m = new CMain(f);
		
		System.out.println(m.getFunctions());
	}
}
