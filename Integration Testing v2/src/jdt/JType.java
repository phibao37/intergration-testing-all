package jdt;

import java.util.HashMap;

import core.models.type.BasicType;

/**
 * Một số kiểu cơ bản trong Java
 */
public class JType{

	
	private static HashMap<String, BasicType> typeMap;
	
	static{
		typeMap = new HashMap<>();
		typeMap.put("int", BasicType.INT);
		typeMap.put("long", BasicType.LONG);
		typeMap.put("float", BasicType.FLOAT);
		typeMap.put("double", BasicType.DOUBLE);
		typeMap.put("boolean", BasicType.BOOL);
		typeMap.put("char", BasicType.CHAR);
		typeMap.put("void", BasicType.VOID);
	}
	
	/**
	 * Lây đối tượng kiểu cơ bản trong Java
	 * @param content chuỗi nội dung của kiểu
	 * @return kiểu cơ bản ứng với nội dung
	 */
	public static BasicType parse(String content){
		return typeMap.get(content);
	}

}
