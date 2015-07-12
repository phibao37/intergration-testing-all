package core.models.type;

import core.models.Expression;
import core.models.Type;
import core.models.expression.ArrayExpression;

/**
 * Kiểu mảng, bao gồm kiểu của phần tử con và một chỉ số xác định số lượng phần tử mảng
 * @example
 * <ul>
 * 	<li>int[]</li>
 * 	<li>float[2]</li>
 * </ul>
 * @author ducvu
 *
 */
public class ArrayType extends Type {
	
	private Type mSubType;
	private int mCapacity;
	
	/**
	 * Tạo một kiểu mảng với kiểu phần tử con và số lượng phần tử
	 * @param content kiểu của các phần tử con
	 * @param capacity kích thước của kiểu mảng, bằng 0 nếu là kích thước chưa xác định
	 */
	public ArrayType(Type subType, int capacity) {
		super(String.format("%s[%s]", subType, capacity == 0 ? "" : capacity));
		mSubType = subType;
		mCapacity = capacity;
	}
	
	/**
	 * Trả về kiểu của phần tử con
	 */
	public Type getSubType(){
		return mSubType;
	}
	
	/**
	 * Trả về kích thước của kiểu mảng, 0 cho trường hợp chưa xác định
	 */
	public int getCapacity(){
		return mCapacity;
	}

	@Override
	public Expression getDefaultValue() {
		int size = mCapacity == 0 ? 1 : mCapacity;
		Expression[] elements = new Expression[size];
		
		for (int i = 0; i < size; i++)
			elements[i] = null;
		
		return new ArrayExpression(elements);
	}

}
