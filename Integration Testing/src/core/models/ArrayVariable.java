package core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import api.expression.IArrayExpression;
import api.expression.IExpression;
import api.expression.IExpressionGroup;
import api.models.IType;
import core.Utils;
import core.expression.ArrayExpression;
import core.expression.NumberExpression;
import core.models.type.ArrayType;

/**
 * Mô tả kiểu biến mảng
 *
 */
public class ArrayVariable extends Variable {
	
	private HashMap<ArrayList<IExpression>, IExpression> mMapData = new HashMap<>();
	//private ObjectExpression mObject;
	
	/**
	 * Tạo một biến mảng với tên và kiểu biến
	 * @param name tên biến
	 * @param type kiểu mảng
	 */
	public ArrayVariable(String name, ArrayType type) {
		this(name, type, null);
	}
	
	/**
	 * Tạo một biến mảng với tên, kiểu và giá trị khởi đầu
	 * @param name tên biến mảng
	 * @param type kiểu mảng
	 * @param value giá trị khởi đầu
	 */
	public ArrayVariable(String name, ArrayType type, IArrayExpression value){
		super(name, type, value);
		//TODO tạo một mảng các biểu thức để lưu kích thước phần tử
	}
	
	/**
	 * Thiết đặt giá trị mới cho phần tử dựa vào dãy các biểu thức chỉ số.<br/>
	 * Nếu dãy chỉ số này không hoàn toàn là số nguyên mà là tham số, nó sẽ được lưu
	 * vào một bảng ánh xạ riêng. 
	 * @param newValue giá trị mới cho phần tử
	 * @param indexes danh sách các biểu thức xác định vị trí của phần tử mảng
	 */
	public void setValueAt(IExpression newValue, IExpression... indexes){
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
	private int[] convertIndexes(IExpression... indexes){
		int[] indexs = new int[indexes.length];
		
		try {
			for (int i = 0; i < indexes.length; i++)
				indexs[i] = ((NumberExpression)indexes[i]).intValue();
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
	public IExpression getValueAt(IExpression... indexes){
		try{
			return getValueAt(convertIndexes(indexes));
		} catch (IllegalArgumentException e){
			return mMapData.get(Utils.toList(indexes));
		}
	}
	
	/**
	 * Trả về danh sách các cặp chỉ số->giá trị mà giá trị của chỉ số vần còn là một
	 * ẩn số.<br/> Thí dụ: a[1][x][2] = 3;
	 */
	public HashMap<ArrayList<IExpression>, IExpression> getAbstractDatas(){
		return mMapData;
	}
	
	/**
	 * Kiểm tra mảng đã có giá trị ở một vị trí nhất định
	 */
	public boolean isValueSet(IExpression... indexes){
		try{
			return getValueAt(indexes) != null;
		} catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Kiểm tra mảng đã có giá trị ở một vị trí nhất định
	 */
	public boolean isValueSet(int... indexes){
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
	public void setValueAt(IExpression newValue, int... indexes){
		for (int i: indexes)
			if (i < 0)
				throw new ArrayIndexOutOfBoundsException(i);
		
		IExpression old = null;
		try{
			old = getValueAt(indexes);
		} catch (ArrayIndexOutOfBoundsException e){}
		
		if (old == null)
			old = initUpToIndex(indexes);
		
		getValue().group().replaceChild(old, newValue);
		getValue().invalidateChild();
	}
	
	/**
	 * Lấy giá trị của phần tử tại vị trí chỉ định
	 * @param indexes danh sách chỉ số xác định vị trí của phần tử
	 * @return giá trị của phần tử chỉ định, hoặc null nếu chưa tồn tại
	 * @throws ArrayIndexOutOfBoundsException Vị trí vượt khoảng của mảng 
	 */
	public IExpression getValueAt(int... indexes){
		try {
			IArrayExpression group = getValue();
			IExpression value = group.getElement(indexes[0]);

			for (int i = 1; i < indexes.length; i++) {
				group = (IArrayExpression) value;
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
	private IExpression initUpToIndex(int... indexes){
		initValueIfNotSet();
		IArrayExpression group = getValue();
		IExpressionGroup root = group.group();
		
		IType type = getType();
		IExpression value = null;
		
		for (int i: indexes){
			 if(!(type instanceof ArrayType))
				throw new ArrayIndexOutOfBoundsException(i);

			int capacity = ((ArrayType)type).getCapacity();
			IType subType = ((ArrayType)type).getSubType();
			
			if (i < group.length()){
				value = group.getElement(i);
				if (value == null){
					value = subType.getDefaultValue();
					group.setElement(i, value);
					//replace(value, value);
				}
			}
			
			else if (capacity > 0 && i >= capacity)
				throw new ArrayIndexOutOfBoundsException(i);
			
			else {
				IExpression[] arr = new IExpression[i+1];
				
				for (int j = 0; j < group.length(); j++)
					arr[j] = group.getElement(j);
				arr[i] = subType.getDefaultValue();
				value = new ArrayExpression(arr);
				root.replaceChild(group, value);
				value = ((IArrayExpression)value).getElement(i);
			}
			
			if (value instanceof IArrayExpression){
				group = (IArrayExpression) value;
				type = subType;
			} else {
				group = null;
				type = null;
			}
		}
		setValue(root.ungroup());
		getValue().invalidateChild();
		return value;
	}
	
//	private static ObjectType ARRAY_LENGTH_TYPE;
//	
//	/**
//	 * Kích hoạt chế độ đối tượng cho biến mảng, biến sẽ có thêm thuộc tính "length"
//	 * tương ứng với kích thước của mảng
//	 */
//	public void setSupportObject(){
//		if (ARRAY_LENGTH_TYPE == null){
//			LinkedHashMap<String, Type> schema = new LinkedHashMap<>();
//			Type finalInt = (Type) BasicType.INT.clone();
//			
//			finalInt.addModifier(Modifier.FINAL_MODIFIER);
//			schema.put("length", finalInt);
//			ARRAY_LENGTH_TYPE = new ObjectType(getType().getContent(), schema);
//		}
//		
//		mObject = new ObjectExpression(ARRAY_LENGTH_TYPE);
//	}
//
//	@Override
//	public ObjectExpression object(){
//		return mObject;
//	}

	@Override
	public IArrayExpression getValue() {
		return (IArrayExpression) super.getValue();
	}

	@Override
	public ArrayType getType() {
		return (ArrayType) super.getType();
	}

	@Override
	public void setValue(IExpression value) {
		if (value != null && !(value instanceof IArrayExpression))
			throw new RuntimeException("The value must be an array expression");

		//mData = (ArrayExpression) value;
		super.setValue(value);
	}
//
//	@Override
//	public boolean replace(IExpression find, IExpression replace) {
//		boolean replaced = super.replace(find, replace);
//		setValue(g[0]);
//		return replaced;
//	}
	
	
	
	@Override
	public ArrayVariable clone() {
		ArrayVariable clone = (ArrayVariable) super.clone();
		clone.mMapData = new HashMap<>(mMapData);
		//if (mObject != null)
			//clone.mObject = (ObjectExpression) mObject.clone();
		return clone;
	}

	/**
	 * Trả về ánh xạ giữa các chỉ số (kiểu nguyên) và giá trị của mảng ứng với các
	 * chỉ số này. Chỉ các giá trị khác null mới được liệt kê
	 */
	public LinkedHashMap<int[], IExpression> getAllValue(){
		LinkedHashMap<int[], IExpression> map = new LinkedHashMap<>();
		
		travelIndexMap(getValue(), new LinkedList<Integer>(), map);
		return map;
	}
	
	/**
	 * Duyệt các giá trị ứng theo chỉ số
	 */
	private void travelIndexMap(IExpression value, LinkedList<Integer> indexes, 
			LinkedHashMap<int[], IExpression> map){
		if (value == null)
			return;
		
		if (value instanceof IArrayExpression){
			IArrayExpression array = (IArrayExpression) value;
			
			for (int i = 0; i < array.length(); i++){
				indexes.add(i);
				travelIndexMap(array.getElement(i), indexes, map);
				indexes.removeLast();
			}
		} else {
			int[] indexs = new int[indexes.size()];
			
			for (int i = 0; i < indexs.length; i++)
				indexs[i] = indexes.get(i);
			map.put(indexs, value);
		}
	}
	
}







