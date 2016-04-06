package api.models;

import java.util.List;
import java.util.Map;

/**
 * Kết quả của việc kiểm thử một hàm
 */
public interface IFunctionTestResult {
	
	final Integer
		STATEMENT = 1,
		BRANCH = 2,
		SUBCONDITION = 3,
		ALLPATH = 4,
		ERROR = 5,
		LOOP = 6;
	
	Map<Integer, List<ITestpath>> getMapPathResult();
}
