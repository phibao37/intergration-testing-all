package core.models.expression;

import core.models.Expression;
import core.models.Type;
import core.models.type.BasicType;
import core.visitor.ExpressionVisitor;

/**
 * Mô tả các biểu thức hằng trong ngôn ngữ lập trình, thường là:
 * <ul>
 * 	<li>123: hằng dạng số nguyên</li>
 * 	<li>12.3: hằng dạng số thực</li>
 * 	<li>'1': hằng dạng kí tự</li>
 * 	<li>"123": hằng dạng chuỗi</li>
 * 	<li>true: hằng dạng nhị phân</li>
 * </ul>
 * @author ducvu
 *
 */
public class IDExpression extends Expression {

	public static void main(String[] args){
		System.out.println(Double.valueOf("12dd"));
	}
	
	private Type mType;
	private Object mValue;
	
	/**
	 * Tạo một biểu thức hằng
	 * @param content nội dung của biểu thức ở dạng chuỗi
	 */
	public IDExpression(String content){
		super(content);
	}

	@Override
	protected void setContent(String content) {
		super.setContent(content);
		mType = getTypeForContent(content);
	}
	
	/**
	 * Nhận dạng kiểu của khối biểu thức hằng cho trước, đặt giá trị java cho nó
	 * @param content nội dung của biểu thức hằng ở dạng chuỗi
	 * @return kiểu của biểu thức hằng
	 */
	protected Type getTypeForContent(String content){
		if (content.matches("[\\+\\-]?\\d+")){ //length?
			mValue = Integer.valueOf(content);
			return BasicType.INT;
		}
		if (content.matches("[\\+\\-]?((\\d+)|(\\d*\\.\\d+))f")){
			mValue = Float.valueOf(content);
			return BasicType.FLOAT;
		}
		if (content.matches("[\\+\\-]?(\\d+d)|(\\d*\\.\\d+d?)")){
			mValue = Double.valueOf(content);
			return BasicType.DOUBLE;
		}
		
		//TODO getTypeForContent kieu char, string
		
		if (content.matches("(true)|(false)")){
			mValue = Boolean.valueOf(content);
			return BasicType.BOOL;
		}
		
		return null;
	}

	/**
	 * Trả về kiểu của biểu thức hằng
	 */
	public Type getType(){
		return mType;
	}
	
	/**
	 * Trả về giá trị tương đương của biểu thức hằng trong java
	 */
	public Object getJavaValue(){
		return mValue;
	}

	@Override
	protected int handle(ExpressionVisitor visitor) {
		return visitor.visit(this);
	}
}
