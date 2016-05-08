package core.models;

import java.util.List;
import java.util.Map;

import api.models.ITestpath;
import api.models.IFunctionTestResult;

public class FunctionTestResult implements IFunctionTestResult {

	private Map<Integer, List<ITestpath>> result;
	private int[] percents;
	
	public FunctionTestResult(Map<Integer, List<ITestpath>> mapResult) {
		result = mapResult;
		percents = new int[4];
	}
	
	@Override
	public Map<Integer, List<ITestpath>> getMapPathResult() {
		return result;
	}

	@Override
	public void setPercent(int coverage, int percent) {
		percents[coverage] = percent;
	}

	@Override
	public int getPercent(int coverage) {
		return percents[coverage];
	}

}
