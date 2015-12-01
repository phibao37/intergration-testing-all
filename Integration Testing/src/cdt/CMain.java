package cdt;

import java.io.File;

import api.parser.UnitParser;
import core.Main;

public class CMain extends Main {

	public CMain(File... source){
		super(source);
	}
	
	@Override
	public UnitParser newUnitParser(File source) {
		return new CUnitParser(source, this);
	}

}
