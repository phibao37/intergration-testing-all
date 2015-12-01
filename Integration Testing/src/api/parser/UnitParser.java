package api.parser;

import java.io.File;
import java.util.List;

import api.IMain;
import api.models.IFunction;

public abstract class UnitParser {
	
	protected File mFile;
	protected IMain mMain;
	
	protected UnitParser(File file, IMain main){
		mFile = file;
		mMain = main;
	}
	
	/**
	 * Lấy danh sách các hàm số đã phân tích được từ mã nguồn
	 */
	public abstract List<IFunction> getParsedFunctions();
}
