package core.models;

import core.graph.Graphable;
import core.models.type.ArrayType;

/**
 * Mô tả kiểu của các biến số trong các ngôn ngữ
 * @author ducvu
 *
 */
public abstract class Type extends Element implements Graphable {
	
	protected Object defaultValue;
	
	/**
	 * Tạo một kiểu cùng với nội dung của nó
	 * @param content chuỗi hiển thị của kiểu, thí dụ: int, float, bool,...
	 */
	public Type(String content){
		super(content);
	}
	
	/**
	 * Trả về giá trị mặc định cho kiểu này.<br/>
	 * Giá trị này dùng để khởi tạo các biến số không tham gia vào các ràng buộc
	 */
	public abstract Expression getDefaultValue();
	
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
