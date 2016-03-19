package core.solver;

import java.util.List;
import java.util.Map;

import api.models.IBasisPath;
import api.models.ITestResult;

public class TestResult implements ITestResult {

	private Map<Integer, List<IBasisPath>> result;
	
	public TestResult(Map<Integer, List<IBasisPath>> mapResult) {
		result = mapResult;
	}
	
	@Override
	public Map<Integer, List<IBasisPath>> getMapPathResult() {
		return result;
	}

}
