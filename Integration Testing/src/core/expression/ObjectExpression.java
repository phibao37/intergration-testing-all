package core.expression;

import java.util.LinkedHashMap;

import api.expression.IExpression;
import api.expression.IObjectExpression;
import api.models.IType;
import core.models.type.ObjectType;

public class ObjectExpression extends ExpressionGroup implements IObjectExpression {

	private LinkedHashMap<String, Integer> mapData;
	private ObjectType mType;
	
	public ObjectExpression(ObjectType type, IExpression[] datas) {
		mType = type;
		LinkedHashMap<String, IType> schema = type.getSchema();
		int i = 0;
		
		g = new IExpression[schema.size()];
		mapData = new LinkedHashMap<>(g.length);
		for (String key: schema.keySet()){
			if (datas != null)
				g[i] = datas[i];
			mapData.put(key, i++);
		}
	}
	
	/**
	 * Tạo một biểu thức đối tượng rỗng mới
	 * @param type kiểu của đối tượng
	 */
	public ObjectExpression(ObjectType type) {
		this(type, null);
	}
	
	@Override
	public IType getType() {
		return mType;
	}

	@Override
	public IExpression getMember(String member) {
		return g[mapData.get(member)];
	}

	@Override
	public IType getMemberType(String member) {
		return mType.getSchema().get(member);
	}

	@Override
	public void setMember(String name, IExpression member) {
		g[mapData.get(name)] = member;
		invalidateChild();
	}

	@Override
	protected String generateContent() {
		String data = "";
		int i = 0;
		
		for (String key: mType.getSchema().keySet()){
			if (g[i] != null)
				data += ", " + key + ": " + g[i];
			i++;
		}
		
		return data.isEmpty() ? "{}" : "{" + data.substring(2) + "}";
	}

}
