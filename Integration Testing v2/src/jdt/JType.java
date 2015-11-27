package jdt;

import java.util.HashMap;

import core.ProcessInterface;
import core.models.Type;
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
	 * Lây đối tượng kiểu trong Java
	 * @param content chuỗi nội dung của kiểu
	 * @return kiểu ứng với nội dung
	 */
	public static Type parse(String content, ProcessInterface process){
		Type type = typeMap.get(content);
		
		//Không phải kiểu cơ bản, duyệt trong các kiểu cấu trúc
		if (type == null){
			for (Type t: process.getDeclaredTypes())
				if (t.getContent().equals(content)){
					type = t;
					break;
				}
		}
		return type;
	}

}
