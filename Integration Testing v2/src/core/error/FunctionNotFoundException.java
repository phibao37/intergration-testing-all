package core.error;

import core.models.expression.FunctionCallExpression;

/**
 * Ngoại lệ khi một lời gọi hàm không thể tìm được khai báo hàm khớp với nó
 */
public class FunctionNotFoundException extends CoreException {
	private static final long serialVersionUID = 1L;

	private FunctionCallExpression mCall;
	
	/**
	 * Tạo một ngoại lệ không tìm được khai báo từ một lời gọi hàm
	 */
	public FunctionNotFoundException(FunctionCallExpression call) {
		super("The call %s does not match any declared function", call);
		mCall = call;
	}
	
	/**
	 * Trả về biểu thức gọi hàm dẫn tới ngoại lệ
	 */
	public FunctionCallExpression getCallExpression(){
		return mCall;
	}

}
