package core.models;

/**
 * Stub của một hàm là một giá trị đầu ra đã được định sẵn, không phụ thuộc vào nội dung
 * của hàm số
 */
public class FunctionStub {

	private Expression mValue;
	private Function mFunction;
	
	/**
	 * 
	 * @param function
	 * @param value
	 */
	public FunctionStub(Function function, Expression value){
		mFunction = function;
		mValue = value;
	}
	
	public Function getFunction(){
		return mFunction;
	}
	
	public Expression getValue(){
		return mValue;
	}
	
}
