package api.expression;

import api.models.IType;

public interface IContainNameExpression extends IExpression {
	
	/**
	 * Trả về biểu thức tương ứng với thuộc tính tên
	 */
	IExpression getNameExpression();
	
	/**
	 * Trả về chuỗi hiển thị của thuộc tính tên
	 */
	default String getName(){
		return getNameExpression().getContent();
	}
	

	void setType(IType type);
}
