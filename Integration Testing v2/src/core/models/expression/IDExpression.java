package core.models.expression;

import core.models.Expression;
import core.models.Type;
import core.models.type.BasicType;

/**
 * Mô tả các khối hằng trong ngôn ngữ lập trình, thường là:
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

	private Type mType;
	
	/**
	 * Tạo một khối cấu trúc hằng
	 * @param content nội dung của cấu trúc ở dạng chuỗi
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
	 * Nhận dạng kiểu của khối cấu trúc hằng cho trước
	 * @param content nội dung của cấu trúc hằng ở dạng chuỗi
	 * @return kiểu của cấu trúc hằng
	 */
	protected Type getTypeForContent(String content){
		if (content.matches("\\d+")) //length?
			return BasicType.INT;
		if (content.matches("((\\d+)|(\\d*\\.\\d+))f"))
			return BasicType.FLOAT;
		if (content.matches("(\\d+d)|(\\d*\\.\\d+d?)"))
			return BasicType.DOUBLE;
		
		//TODO getTypeForContent kieu char, string
		
		if (content.matches("(true)|(false)"))
			return BasicType.BOOL;
		
		return null;
	}

	/**
	 * Trả về kiểu của cấu trúc hằng
	 */
	public Type getType(){
		return mType;
	}
}
