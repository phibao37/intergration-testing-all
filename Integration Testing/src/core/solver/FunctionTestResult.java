package core.solver;

import java.util.List;
import java.util.Map;

import api.models.ITestpath;
import api.models.IFunctionTestResult;

public class FunctionTestResult implements IFunctionTestResult {

	private Map<Integer, List<ITestpath>> result;
	
	public FunctionTestResult(Map<Integer, List<ITestpath>> mapResult) {
		result = mapResult;
	}
	
	@Override
	public Map<Integer, List<ITestpath>> getMapPathResult() {
		return result;
	}

}
