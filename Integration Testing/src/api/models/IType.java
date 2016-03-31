package api.models;

import java.util.List;

import api.expression.IExpression;


public interface IType extends IElement, Comparable<IType> {
	
	/**
	 * Trả về giá trị mặc định cho kiểu này.<br/>
	 * Giá trị này dùng để khởi tạo các biến số không tham gia vào các ràng buộc
	 */
	public IExpression getDefaultValue();
	
	/**
	 * Trả về cỡ/độ lớn của kiểu, thường dùng để quyết định các phép tính, chuyển kiểu
	 */
	public int getSize();
	
	@Override
	public default int compareTo(IType t){
		return Integer.compare(getSize(), t.getSize());
	}
	
	/**
	 * Trả về danh sách các cờ hiệu của kiểu, hoặc null nếu không có
	 */
	public List<ITypeModifier> getModifiers();
	
	public default boolean hasModifier(ITypeModifier mdf){
		List<ITypeModifier> mdfs = getModifiers();
		
		if (mdfs == null)
			return false;
		
		for (ITypeModifier item: mdfs)
			if (item.equalsContent(mdf))
				return true;
		
		return false;
	}
	
	public default boolean hasModifier(Class<? extends ITypeModifier> cls){
		List<ITypeModifier> mdfs = getModifiers();
		
		if (mdfs == null)
			return false;
		
		for (ITypeModifier item: mdfs)
			if (cls.isInstance(item))
				return true;
		
		return false;
	}
	
	public static interface ITypeModifier extends IElement{
		
	}
}
