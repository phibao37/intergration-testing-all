package core.models.type;

import java.util.LinkedHashMap;
import core.models.Expression;
import core.models.Type;
import core.models.expression.ObjectExpression;

/**
 * Kiểu cấu trúc đối tượng (struct/class), bao gồm các dữ liệu thuộc các kiểu khác
 *  được chứa trong nó, thường được định nghĩa qua:
 *  <pre>
 *  struct, typedef struct //(C, C++)
 *  class //(C++, Java)
 *  </pre>
 */
public class ObjectType extends Type {

	private LinkedHashMap<String, Type> mSchema;
	
	/**
	 * Tạo một kiểu đối tượng mới
	 * @param content tên của kiểu
	 * @param schema ánh xạ giữa tên các thuộc tính và kiểu dữ liệu tương ứng
	 */
	public ObjectType(String content, LinkedHashMap<String, Type> schema) {
		super(content, Integer.MAX_VALUE);
		mSchema = schema;
	}
	
	/**
	 * Trả về định nghĩa của kiểu, la một ánh xạ giữa tên thuộc tính 
	 * và kiểu dữ liệu tương ứng
	 */
	public LinkedHashMap<String, Type> getSchema(){
		return mSchema;
	}

	@Override
	public Expression getDefaultValue() {
		Expression[] datas = new Expression[mSchema.size()];
//		int i = 0;
//		
//		for (Entry<String, Type> entry: mSchema.entrySet()){
//			Type t = entry.getValue();
//			
//			//Ngăn chặn việc cấp phát không dừng lại
//			if (t != this)
//				datas[i] = t.getDefaultValue();
//		}
		
		return new ObjectExpression(this, datas);
	}

}
