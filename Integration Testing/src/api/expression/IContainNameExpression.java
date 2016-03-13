package api.expression;

public interface IContainNameExpression extends IExpression {
	
	/**
	 * Trả về biểu thức tương ứng với thuộc tính tên
	 */
	IExpression getName();
	
	/**
	 * Trả về chuỗi hiển thị của thuộc tính tên
	 */
	default String getNameString(){
		return getName().getContent();
	}
}
