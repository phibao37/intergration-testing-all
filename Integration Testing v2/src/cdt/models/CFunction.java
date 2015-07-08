package cdt.models;

import org.eclipse.cdt.core.dom.ast.IASTStatement;

import core.models.Function;
import core.models.Type;
import core.models.Variable;

/**
 * Hàm trong C
 */
public class CFunction extends Function {

	/**
	 * Tạo một hàm số mới qua tất cả các thông số của nó
	 * @param name tên của hàm
	 * @param paras danh sách các tham số cần truyền vào hàm
	 * @param body nội dung phần thân hàm
	 * @param returnType kiểu trả về của hàm
	 */
	public CFunction(String name, Variable[] paras, IASTStatement body, Type returnType) {
		super(name, paras, body, returnType);
	}

	@Override
	protected String getBodyString(Object body) {
		return ((IASTStatement)body).getRawSignature();
	}
	
	
}
