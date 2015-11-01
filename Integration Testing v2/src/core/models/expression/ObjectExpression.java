package core.models.expression;

import java.util.LinkedHashMap;
import core.models.Expression;
import core.models.ExpressionGroup;
import core.models.Type;
import core.models.type.ObjectType;

/**
 * Dạng biểu thức "đặc biệt", nó là một đối tượng có chứa các biểu thức con bên trong nó 
 * được tham chiếu từ tên các thuộc tính tương ứng. Thi dụ:
 * <pre>
 * {
 *   age: 10,
 *   name: "John"
 * }
 * </pre>
 */
public class ObjectExpression extends ExpressionGroup {

	private LinkedHashMap<String, Integer> mapData;
	private ObjectType mType;
	
	/**
	 * Tạo một biểu thức đối tượng mới
	 * @param type kiểu của đối tượng
	 * @param datas danh sách biểu thức ứng với các thuộc tính của đối tượng
	 * @throws ArrayIndexOutOfBoundsException kích thước danh sách biểu thức nhỏ hơn
	 * kích thước danh sách các thuộc tính được khai báo trong kiểu
	 */
	public ObjectExpression(ObjectType type, Expression[] datas) 
			throws ArrayIndexOutOfBoundsException{
		mType = type;
		LinkedHashMap<String, Type> schema = type.getSchema();
		int i = 0;
		
		g = new Expression[schema.size()];
		mapData = new LinkedHashMap<>(g.length);
		for (String key: schema.keySet()){
			g[i] = datas[i];
			mapData.put(key, i++);
		}
	}
	
	/**
	 * Trả về biểu thức con ứng với tên thuộc tính của đối tượng
	 */
	public Expression getMember(String name){
		return g[mapData.get(name)];
	}
	
	/**
	 * Thiết đặt biểu thức thuộc tính của đối tượng
	 * @param name tên của thuộc tính 
	 * @param value giá trị mới của thuộc tính
	 */
	public void setMember(String name, Expression value){
		replace(getMember(name), value);
	}
	
	@Override
	protected String generateContent() {
		if (mType.getSchema().isEmpty())
			return "{}";
		String data = "";
		int i = 0;
		
		for (String key: mType.getSchema().keySet())
			data += ", " + key + ": " + g[i++];
		
		return "{" + data.substring(2) + "}";
	}

	@Override
	public Type getType() {
		return mType;
	}

}
