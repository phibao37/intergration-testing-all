package core.models;

import api.expression.IExpression;
import api.models.IType;
import api.models.IVariable;

/**
 * Mô tả một biến số. Mỗi biến số tương đương với các biến trong chương trình, tồn tại
 * trong một khoảng phạm vi nhất định.<br/>
 * Mỗi biến số thường có tên, kiểu biến và giá trị của biến số 
 * @author ducvu
 *
 */
public class Variable extends Element implements IVariable {
	
	private String mName;
	private IType mType;
	private IExpression mValue;
	private int mScope;
	
	/**
	 * Tạo một biến số mới qua tên và kiểu, chưa đặt giá trị.<br/>
	 * Thường dùng cho các tham số của hàm số
	 * @param name tên biến số
	 * @param type kiểu của biến số
	 */
	public Variable(String name, IType type){
		this(name, type, null);
	}
	
	/**
	 * Tạo một biến số mới qua tên, kiểu biến và giá trị của nó
	 * @param name tên biến số
	 * @param type kiểu của biến số
	 * @param value giá trị khởi đầu của biến số
	 */
	public Variable(String name, IType type, IExpression value){
		mName = name;
		mType = type;
		setValue(value);
	}
	
	@Override
	public String getName(){
		return mName;
	}
	
	@Override
	public void setScope(int scope){
		mScope = scope;
	}
	
	@Override
	public int getScope(){
		return mScope;
	}
	
	@Override
	public boolean isValueSet(){
		return getValue() != null;
	}

	@Override
	public IType getType() {
		return mType;
	}

	@Override
	public void setValue(IExpression value) {
		mValue = value;
		setContent(getType() + " " + getName() + 
				(value == null ? "" : "(" + value + ")"));
	}

	@Override
	public IExpression getValue() {
		return mValue;
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
	public Variable clone() {
		Variable v = (Variable) super.clone();
		
		if (mValue != null)
			v.mValue = mValue.clone();
		return v;
	}
	
}
