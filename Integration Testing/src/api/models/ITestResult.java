package api.models;

import java.util.List;
import java.util.Map;

/**
 * Kết quả của việc kiểm thử một hàm
 */
public interface ITestResult {
	
	final Integer
		STATEMENT = 1,
		BRANCH = 2,
		SUBCONDITION = 3,
		ALLPATH = 4,
		ERROR = 5;
	
	Map<Integer, List<IBasisPath>> getMapPathResult();
}