package cdt.models;

import java.util.HashMap;

import core.MainProcess;
import core.models.Type;
import core.models.type.BasicType;

/**
 * Kiểu trong chương trình C
 * @author ducvu
 *
 */
public class CType {
	
	private static HashMap<String, BasicType> typeMap;
	
	static{
		typeMap = new HashMap<>();
		typeMap.put("int", BasicType.INT);
		typeMap.put("long", BasicType.LONG);
		typeMap.put("float", BasicType.FLOAT);
		typeMap.put("double", BasicType.DOUBLE);
		typeMap.put("bool", BasicType.BOOL);
		typeMap.put("char", BasicType.CHAR);
		typeMap.put("void", BasicType.VOID);
	}
	
	/**
	 * Lây đối tượng kiểu trong C
	 * @param content chuỗi nội dung của kiểu
	 * @return kiểu ứng với nội dung
	 */
	public static Type parse(String content){
		Type type = typeMap.get(content);
		
		//Không phải kiểu cơ bản, duyệt trong các kiểu cấu trúc
		if (type == null){
			for (Type t: MainProcess.instance.getDeclaredTypes())
				if (t.getContent().equals(content)){
					type = t;
					break;
				}
		}
		return type;
	}
}
