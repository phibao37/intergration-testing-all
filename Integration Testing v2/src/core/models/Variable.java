package core.models;

import core.graph.Graphable;
import core.models.expression.ObjectExpression;

/**
 * Mô tả một biến số. Mỗi biến số tương đương với các biến trong chương trình, tồn tại
 * trong một khoảng phạm vi nhất định.<br/>
 * Mỗi biến số thường có tên, kiểu biến và giá trị của biến số 
 * @author ducvu
 *
 */
public class Variable extends ExpressionGroup implements Graphable {
	
	private String mName;
	private Type mType;
	private int mScope;
	
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
	 * Trả về kiểu của dữ liệu mà biến số lưu trữ. Với biến thường, nó chính là kiểu
	 * của biến số. Đối với biến mảng, nó là kiểu của dữ liệu phần tử mảng
	 */
	public Type getDataType(){
		return getType();
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
	 * Trả về chuỗi hiển thị giá trị của biến số
	 */
	public String getValueString(){
		return String.valueOf(getValue());
	}
	
	/**
	 * Thiết đặt scope (vùng khối mà nó có hiệu lực) cho biến
	 */
	public void setScope(int scope){
		mScope = scope;
	}
	
	/**
	 * Trả về scope của biến
	 */
	public int getScope(){
		return mScope;
	}
	
	/**
	 * Kiểm tra biến đã được gán giá trị
	 */
	public boolean isValueSet(){
		return getValue() != null;
	}
	
	/**
	 * Thiết đặt giá trị cho biến nếu như nó chưa có giá trị
	 */
	public Variable initValueIfNotSet(){
		if (!isValueSet())
			setValue(getType().getDefaultValue());
		return this;
	}

	@Override
	protected String generateContent() {
		return String.format("%s(%s, %s)", getName(), getType(), getValue());
	}

	/**
	 * Tạo ra một biến mới có cùng giá trị (đã được sao chép) từ biến này
	 */
	@Override
	public Variable clone() {
		return (Variable) super.clone();
	}

	@Override
	public String getHTMLContent() {
		return String.format("%s %s%s", 
				getType().getHTMLContent(), 
				getName(), 
				isValueSet() ? " = " + getValue() : "");
	}
	
	/**
	 * Trả về biểu thức giá trị dạng đối tượng khi biến này là biến cấu trúc
	 * hoặc trả về <i>null</i> nếu không đúng
	 */
	public ObjectExpression object() {
		try{
			return (ObjectExpression) getValue();
		} catch (ClassCastException e){
			return null;
		}
	}
	
	/**
	 * Kiểm tra biến có chứa giá trị dạng đối tượng
	 */
	public boolean containObject(){
		return object() != null;
	}
	
}
