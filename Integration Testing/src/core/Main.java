package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cdt.CMain;
import api.IMain;
import api.models.IBasisPath;
import api.models.IFunction;
import api.parser.UnitParser;

public abstract class Main implements IMain {
	
	private List<IFunction> listFunction;

	public Main(File... sources){
		listFunction = new ArrayList<>();
		for (File source: sources)
			loadFile(source);
	}
	
	protected void loadFile(File source){
		UnitParser u = newUnitParser(source);
		
		listFunction.addAll(u.getParsedFunctions());
	}

	@Override
	public List<IFunction> getFunctions() {
		return listFunction;
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
