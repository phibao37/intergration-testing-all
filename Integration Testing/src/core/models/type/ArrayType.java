package core.models.type;

import api.expression.IExpression;
import api.models.IType;
import core.expression.ArrayExpression;

public class ArrayType extends Type {

	private IType mSubType;
	private ArrayModifier mArrayMod;
	
	/**
	 * Tạo một kiểu mảng với kiểu phần tử con và số lượng phần tử
	 * @param content kiểu của các phần tử con
	 * @param capacity kích thước của kiểu mảng, bằng 0 nếu là kích thước chưa xác định
	 */
	public ArrayType(IType subType, int capacity) {
		super(String.format("%s[%s]", subType, capacity == 0 ? "" : capacity), 0);
		mSubType = subType;
		addModifier(mArrayMod = new ArrayModifier(capacity));
	}
	
	/**
	 * Trả về kiểu của phần tử con
	 */
	public IType getSubType(){
		return mSubType;
	}
	
	/**
	 * Trả về kích thước của kiểu mảng, 0 cho trường hợp chưa xác định
	 */
	public int getCapacity(){
		return mArrayMod.getCapacity();
	}

	
	public static class ArrayModifier extends TypeModifier{
		private int mCapacity;

		public ArrayModifier(int capacity) {
			super("[" + capacity + "]");
			mCapacity = capacity;
		}
		
		public int getCapacity(){
			return mCapacity;
		}
		
	}


	@Override
	public IExpression getDefaultValue() {
		int size = Math.max(1, getCapacity());
		return new ArrayExpression(new IExpression[size]);
	}

	@Override
	public int compareTo(IType o) {
		return 0;
	}

}
