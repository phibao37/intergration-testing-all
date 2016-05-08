package core.models;

import java.util.ArrayList;
import java.util.List;
import api.models.ITestpath;
import api.models.IFunctionTestResult;

public class FunctionTestResult implements IFunctionTestResult {

	private List<List<ITestpath>> testpaths;
	private int[] percents;
	
	public FunctionTestResult() {
		testpaths = new ArrayList<>(__LAST);
		percents = new int[__LAST];
	}

	@Override
	public void setTestpaths(int coverage, List<ITestpath> testpaths) {
		this.testpaths.add(coverage, testpaths);
	}

	@Override
	public List<ITestpath> getTestpaths(int coverage) {
		return testpaths.get(coverage);
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
