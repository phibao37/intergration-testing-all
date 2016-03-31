package api.solver;

import api.expression.IExpression;
import api.models.IVariable;

public interface ISolveResult {

	/**
	 * Trả về nghiệm ứng với danh sách đầu vào hàm
	 */
	IVariable[] getSolution();
	
	/**
	 * Trả về cờ hiệu về quá trình giải hệ
	 */
	int getCode();
	
	/**
	 * Tóm tắt kết quả
	 */
	String getMessage();
	
	/**
	 * Trả về giá trị được trả về bởi hàm
	 */
	IExpression getReturnValue();
	
	/**
	 * Trả về bộ giải đã giải ra nghiệm
	 */
	ISolver getSolver();
	
	final int 
			UNKNOWN = 0,
			ERROR = -1,
			UNSATISFIED = 1,
			SATISFY = 2;
}
