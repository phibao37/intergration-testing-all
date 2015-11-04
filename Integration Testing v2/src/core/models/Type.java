package core.models;

import java.util.ArrayList;

import core.graph.Graphable;
import core.models.type.ArrayType;
import core.models.type.ObjectType;

/**
 * Mô tả kiểu của các biến số trong các ngôn ngữ
 * @author ducvu
 *
 */
public abstract class Type extends Element implements Graphable, Comparable<Type> {
	
	private int mSize;
	private ArrayList<Modifier> mModifiers;
	
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
	
	/**
	 * Kiểm tra kiểu cấu trúc
	 */
	public boolean isObjectType(){
		return this instanceof ObjectType;
	}
	
	/**
	 * Thêm cờ hiệu tham số cho kiểu
	 */
	protected void addModifier(Modifier mod){
		if (mModifiers == null)
			mModifiers = new ArrayList<>();
		mModifiers.add(mod);
	}
	
	/**
	 * Trả về danh sách các cờ hiệu của kiểu, hoặc null nếu không có
	 */
	public ArrayList<Modifier> getModifiers(){
		return mModifiers;
	}
	
	/**
	 * Kiểm tra kiểu có chứa cờ hiệu hay không
	 */
	public boolean hasModifier(Modifier modifier){
		if (mModifiers == null)
			return false;
		for (Modifier mdf: mModifiers)
			if (mdf.equals(modifier))
				return true;
		return false;
	}
	
	/**
	 * Kiểm tra xem giá trị của biến mang kiểu có thể bị thay đổi sau khi 
	 * truyền qua hàm không<br/>
	 * Giá trị có thể là bị thay đổi một phần (như thay đổi phần tử mảng)
	 * hoặc toàn bộ (làm cho biến mang một giá trị khác) 
	 */
	public boolean isValueChangeable(){
		if (mModifiers != null)
			for (Modifier m: mModifiers)
				if (m.makeValueChangeable())
					return true;
		return false;
	}

	@Override
	public String getHTMLContent() {
		return String.format("<span style=\"color:blue\">%s</span>", getContent());
	}
	
	/**
	 * Mô tả các cờ hiệu tham số bổ sung nghĩa cho kiểu biến số. Thí dụ:
	 * <ul>
	 * 	<li>final/const: kiểu hằng, giá trị không thể thay đổi</li>
	 * 	<li>* (C/C++): kiểu con trỏ, giá trị là một địa chỉ trỏ tới biến khác </li>
	 * 	<li>& (C++): truyền tham chiếu thay vì tham trị</li>
	 * </ul>
	 *
	 */
	public static abstract class Modifier extends Element{
		
		/**
		 * Khởi tạo cờ hiệu tham số bằng chuỗi hiển thị
		 */
		public Modifier(String content){
			super(content);
		}
		
		/**
		 * Cờ hiệu này có làm cho giá trị biến số bị thay đổi sau khi truyền qua hàm
		 * hay không?
		 */
		public abstract boolean makeValueChangeable();
		
		/**
		 * Cờ hiệu hằng số cho các kiểu dữ liệu
		 */
		public static final Modifier FINAL_MODIFIER = new Modifier("const") {
			public boolean makeValueChangeable() { return false; }
		};
		
	}

}
