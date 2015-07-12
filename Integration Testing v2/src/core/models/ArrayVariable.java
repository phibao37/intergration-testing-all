package core.models;

import java.util.ArrayList;
import java.util.HashMap;

import core.Utils;
import core.models.expression.ArrayExpression;
import core.models.expression.IDExpression;
import core.models.type.ArrayType;

/**
 * Mô tả kiểu biến mảng
 * @author ducvu
 *
 */
public class ArrayVariable extends Variable {
	
	private ArrayExpression mData;
	private HashMap<ArrayList<Expression>, Expression> mMapData = new HashMap<>();
	
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
	 * Thiết đặt giá trị mới cho phần tử dựa vào dãy các biểu thức chỉ số.<br/>
	 * Nếu dãy chỉ số này không hoàn toàn là số nguyên mà là tham số, nó sẽ được lưu
	 * vào một bảng ánh xạ riêng. 
	 * @param newValue giá trị mới cho phần tử
	 * @param indexes danh sách các biểu thức xác định vị trí của phần tử mảng
	 */
	public void setValueAt(Expression newValue, Expression... indexes){
		try{
			setValueAt(newValue, convertIndexes(indexes));
		} catch (IllegalArgumentException e){
			mMapData.put(Utils.toList(indexes), newValue);
		}
	}
	
	/**
	 * Chuyển dãy các biểu thức số nguyên sang dãy số nguyên
	 * @throws IllegalArgumentException có một chỉ số còn ở dạng tham số
	 */
	private int[] convertIndexes(Expression... indexes){
		int[] indexs = new int[indexes.length];
		
		try {
			for (int i = 0; i < indexes.length; i++)
				indexs[i] = (int) ((IDExpression)indexes[i]).getJavaValue();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return indexs;
	}
	
	/**
	 * Lấy phần tử mảng tại vị trí xác định
	 * @param indexes danh sách biểu thức xác định vị trí phần tử cần lấy
	 * @return giá trị của phần tử tại vị trí đã cho, hoặc null nếu không tồn tại
	 */
	public Expression getValueAt(Expression... indexes){
		try{
			return getValueAt(convertIndexes(indexes));
		} catch (IllegalArgumentException e){
			return mMapData.get(Utils.toList(indexes));
		}
	}
	
	/**
	 * Kiểm tra mảng đã có giá trị ở một vị trí nhất định
	 */
	public boolean isValueSet(Expression... indexes){
		try{
			return getValueAt(indexes) != null;
		} catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Thiết đặt giá trị mới cho phần tử ở vị trí xác định
	 * @param index chỉ số cần thay đổi
	 * @param newValue giá trị biểu thức mới
	 * @throws ArrayIndexOutOfBoundsException vị trí vượt khoảng của mảng
	 */
	public void setValueAt(Expression newValue, int... indexes){
		for (int i: indexes)
			if (i < 0)
				throw new ArrayIndexOutOfBoundsException(i);
		
		Expression old = null;
		try{
			old = getValueAt(indexes);
		} catch (ArrayIndexOutOfBoundsException e){}
		
		if (old == null)
			old = initUpToIndex(indexes);
		
		replace(old, newValue);
	}
	
	/**
	 * Lấy giá trị của phần tử tại vị trí chỉ định
	 * @param indexes danh sách chỉ số xác định vị trí của phần tử
	 * @return giá trị của phần tử chỉ định, hoặc null nếu chưa tồn tại
	 * @throws ArrayIndexOutOfBoundsException Vị trí vượt khoảng của mảng 
	 */
	public Expression getValueAt(int... indexes){
		try {
			ArrayExpression group = getValue();
			Expression value = group.getElement(indexes[0]);

			for (int i = 1; i < indexes.length; i++) {
				group = (ArrayExpression) value;
				value = group.getElement(indexes[i]);
			}
			return value;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Khởi tạo lại giá trị mảng tới một tập các chỉ số, nếu chưa có
	 * @return đối tượng tại vị trí đã khởi tạo
	 * @throws ArrayIndexOutOfBoundsException vượt quá giới hạn kích thước mảng, hoặc
	 * số lượng chỉ số vượt quá kiểu
	 */
	private Expression initUpToIndex(int... indexes){
		initValueIfNotSet();
		ArrayExpression group = getValue();
		
		Type type = getType();
		Expression value = null;
		
		for (int i: indexes){
			 if(!(type instanceof ArrayType))
				throw new ArrayIndexOutOfBoundsException(i);

			int capacity = ((ArrayType)type).getCapacity();
			Type subType = ((ArrayType)type).getSubType();
			
			if (i < group.length()){
				value = group.getElement(i);
				if (value == null){
					value = subType.getDefaultValue();
					group.setElement(i, value);
					replace(value, value);
				}
			}
			
			else if (capacity > 0 && i >= capacity)
				throw new ArrayIndexOutOfBoundsException(i);
			
			else {
				Expression[] arr = new Expression[i+1];
				
				for (int j = 0; j < group.length(); j++)
					arr[j] = group.getElement(j);
					arr[i] = subType.getDefaultValue();
				value = new ArrayExpression(arr);
				replace(group, value);
				value = ((ArrayExpression)value).getElement(i);
			}
			
			if (value instanceof ArrayExpression){
				group = (ArrayExpression) value;
				type = subType;
			} else {
				group = null;
				type = null;
			}
		}
		return value;
	}

	@Override
	public ArrayExpression getValue() {
		return mData;
	}
	
	

	@Override
	public ArrayType getType() {
		return (ArrayType) super.getType();
	}

	@Override
	public void setValue(Expression value) {
		if (value != null && !(value instanceof ArrayExpression))
			throw new RuntimeException("The value must be an array expression");

		mData = (ArrayExpression) value;
		super.setValue(value);
	}

	@Override
	public boolean replace(Expression find, Expression replace) {
		boolean replaced = super.replace(find, replace);
		setValue(g[0]);
		return replaced;
	}
	
}
