package api.models;

import java.util.List;

/**
 * Kết quả của việc kiểm thử một hàm
 */
public interface IFunctionTestResult {
	
	final int
		STATEMENT = 0,
		BRANCH = 1,
		SUBCONDITION = 2,
		ALLPATH = 3,
		ERROR = 4,
		LOOP = 5,
		
		__LAST = 6;
	
	void setTestpaths(int coverage, List<ITestpath> testpaths);
	
	List<ITestpath> getTestpaths(int coverage);
	
	void setPercent(int coverage, int percent);
	
	int getPercent(int coverage);
}
