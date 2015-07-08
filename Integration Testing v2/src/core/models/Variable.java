package core.models;

/**
 * Mô tả một biến số. Mỗi biến số tương đương với các biến trong chương trình, tồn tại
 * trong một khoảng phạm vi nhất định.<br/>
 * Mỗi biến số thường có tên, kiểu biến và giá trị của biến số 
 * @author ducvu
 *
 */
public class Variable extends ExpressionGroup {
	
	private String mName;
	private Type mType;
	
	/**
	 * Tạo một biến số mới qua tên và kiểu, chưa đặt giá trị.<br/>
	 * Thường dùng cho các tham số của hàm số
	 * @param name tên biến số
	 * @param type kiểu của biến số
	 */
	public Variable(String name, Type type){
		this(name, type, null);
	}
	
	/**
	 * Tạo một biến số mới qua tên, kiểu biến và giá trị của nó
	 * @param name tên biến số
	 * @param type kiểu của biến số
	 * @param value giá trị khởi đầu của biến số
	 */
	public Variable(String name, Type type, Expression value){
		g = new Expression[1];
		mName = name;
		mType = type;
		setValue(value);
	}
	
	/**
	 * Trả về tên của biến số
	 */
	public String getName(){
		return mName;
	}
	
	/**
	 * Trả về kiểu của biến số
	 */
	public Type getType(){
		return mType;
	}
	
	/**
	 * Thiết đặt giá trị cho biến số
	 * @param value giá trị mới của biến số
	 */
	public void setValue(Expression value){
		g[0] = value;
		setContent(generateContent());
	}
	
	/**
	 * Trả về giá trị của biến số
	 */
	public Expression getValue(){
		return g[0];
	}
	
	/**
	 * Thiết đặt giá trị cho biến nếu như nó chưa có giá trị
	 */
	public void initValueIfNotSet(){
		if (getValue() == null)
			setValue(getType().getDefaultValue());
	}

	@Override
	protected String generateContent() {
		return String.format("%s(%s, %s)", getName(), getType(), getValue());
	}
}
