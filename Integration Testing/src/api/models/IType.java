package api.models;

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
}
