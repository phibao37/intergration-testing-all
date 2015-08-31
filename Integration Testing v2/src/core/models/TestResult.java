package core.models;

import core.solver.Solver.Result;

/**
 * Kết quả so sánh giữa đầu ra tính toán được so với đầu ra mong muốn ứng với một
 * đầu vào cụ thể
 */
public class TestResult {

	private Function mFunc;
	private Testcase mTestcase;
	private Result mResult;
	
	private boolean isMatch;
	
	/**
	 * Tạo kết quả test mới
	 * @param func hàm đang được test
	 * @param test testcase dùng để chứng thực kết quả
	 * @param result kết quả tính toán được
	 * @param match kết quả của việc test, có khớp hay không
	 */
	TestResult(Function func, Testcase test, Result result, boolean match){
		mFunc = func;
		mTestcase = test;
		mResult = result;
		isMatch = match;
	}
	
	/**
	 * Trả về kết quả của việc test
	 */
	public boolean isMatch(){
		return isMatch;
	}
	
	/**
	 * Trả về hàm đang được test
	 */
	public Function getFunction(){
		return mFunc;
	}
	
	/**
	 * Trả về testcase được dùng để chứng thực kết quả
	 */
	public Testcase getTestcase(){
		return mTestcase;
	}
	
	/**
	 * Kiểm tra xem có testcase được dùng để chứng thực kết quả hay không
	 */
	public boolean isTestcaseFound(){
		return getTestcase() != null;
	}
	
	/**
	 * Trả về kết quả tính toán được
	 */
	public Result getResult(){
		return mResult;
	}
}
