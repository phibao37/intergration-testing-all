package core.models;

import core.models.expression.ArrayExpression;
import core.models.type.ArrayType;

/**
 * Mô tả kiểu biến mảng
 * @author ducvu
 *
 */
public class ArrayVariable extends Variable {
	
	private ArrayExpression mData;
	
	/**
	 * Tạo một biến mảng với tên và kiểu biến
	 * @param name tên biến
	 * @param type kiểu mảng
	 */
	public ArrayVariable(String name, ArrayType type) {
		super(name, type);
	}
	
	/**
	 * Tạo một biến mảng với tên, kiểu và giá trị khởi đầu
	 * @param name tên biến mảng
	 * @param type kiểu mảng
	 * @param value giá trị khởi đầu
	 */
	public ArrayVariable(String name, ArrayType type, ArrayExpression value){
		super(name, type, value);
	}
	
	/**
	 * Thiết đặt giá trị mới cho phần tử ở vị trí xác định
	 * @param index chỉ số cần thay đổi
	 * @param newValue giá trị biểu thức mới
	 */
	public void setValueAt(int index, Expression newValue){
		replace(mData.getElement(index), newValue);
	}

	@Override
	public ArrayExpression getValue() {
		return mData;
	}

	@Override
	public void setValue(Expression value) {
		if (value != null && !(value instanceof ArrayExpression))
			throw new RuntimeException("The value must be an array expression");
		super.setValue(value);
		mData = (ArrayExpression) value;
	}

	@Override
	public boolean replace(Object find, Expression replace) {
		boolean replaced = super.replace(find, replace);
		setValue(g[0]);
		return replaced;
	}
	
}
