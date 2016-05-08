package api.models;

import java.util.List;
import java.util.Map;

/**
 * Kết quả của việc kiểm thử một hàm
 */
public interface IFunctionTestResult {
	
	final Integer
		STATEMENT = 0,
		BRANCH = 1,
		SUBCONDITION = 2,
		ALLPATH = 3,
		ERROR = 4,
		LOOP = 5;
	
	Map<Integer, List<ITestpath>> getMapPathResult();
	
	void setPercent(int coverage, int percent);
	
	int getPercent(int coverage);
}
