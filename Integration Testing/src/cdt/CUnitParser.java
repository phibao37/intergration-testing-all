package cdt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import api.IMain;
import api.models.IFunction;
import api.parser.UnitParser;

public class CUnitParser extends UnitParser {

	private List<IFunction> listFunction;
	
	public CUnitParser(File file, IMain main) {
		super(file, main);
		listFunction = new ArrayList<>();
	}

	@Override
	public List<IFunction> getParsedFunctions() {
		return listFunction;
	}

}
