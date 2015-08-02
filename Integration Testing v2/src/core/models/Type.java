package core.models;

import core.graph.Graphable;
import core.models.type.ArrayType;

/**
 * Mô tả kiểu của các biến số trong các ngôn ngữ
 * @author ducvu
 *
 */
public abstract class Type extends Element implements Graphable, Comparable<Type> {
	
	private int mSize;
	
	/**
	 * Tạo một kiểu cùng với nội dung của nó
	 * @param content chuỗi hiển thị của kiểu, thí dụ: int, float, bool,...
	 * @param size cỡ của kiểu, xem {@link #getSize()}
	 */
	public Type(String content, int size){
		super(content);
		mSize = size;
	}
	
	/**
	 * Trả về giá trị mặc định cho kiểu này.<br/>
	 * Giá trị này dùng để khởi tạo các biến số không tham gia vào các ràng buộc
	 */
	public abstract Expression getDefaultValue();
	
	@Override
	public int compareTo(Type type) {
		return Integer.compare(getSize(), type.getSize());
	}

	/**
	 * Trả về cỡ/độ lớn của kiểu, thường dùng để quyết định các phép tính, chuyển kiểu
	 */
	public int getSize(){
		return mSize;
	}
	
	/**
	 * Kiểm tra kiểu mảng
	 */
	public boolean isArrayType(){
		return this instanceof ArrayType;
	}

	@Override
	public String getHTMLContent() {
		return String.format("<span style=\"color:%s\">%s</span>", 
				HTML_COLOR, getContent());
	}

	private static String HTML_COLOR = "blue";
}
