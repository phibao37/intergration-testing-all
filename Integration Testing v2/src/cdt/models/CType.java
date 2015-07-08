package cdt.models;

import java.util.HashMap;

import core.models.type.BasicType;

/**
 * Kiểu trong chương trình C
 * @author ducvu
 *
 */
public class CType extends BasicType {

	protected CType(String content, Object defaultValue) {
		super(content, defaultValue);
	}
	
	private static HashMap<String, BasicType> typeMap;
	
	static{
		typeMap = new HashMap<>();
		typeMap.put("int", BasicType.INT);
		typeMap.put("float", BasicType.FLOAT);
		typeMap.put("double", BasicType.DOUBLE);
		typeMap.put("bool", BasicType.BOOL);
		typeMap.put("char", BasicType.CHAR);
	}
	
	/**
	 * Lây đối tượng kiểu cơ bản trong C
	 * @param content chuỗi nội dung của kiểu
	 * @return kiểu cơ bản ứng với nội dung
	 */
	public static BasicType parse(String content){
		return typeMap.get(content);
	}
}
