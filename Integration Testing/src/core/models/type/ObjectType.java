package core.models.type;

import java.util.LinkedHashMap;

import api.expression.IExpression;
import api.models.IType;
import core.expression.ObjectExpression;

public class ObjectType extends Type {

	private LinkedHashMap<String, IType> schema;
	
	public ObjectType(String content, LinkedHashMap<String, IType> schema) {
		super(content, -1);
		this.schema = schema;
	}
	
	/**
	 * Trả về định nghĩa của kiểu, la một ánh xạ giữa tên thuộc tính 
	 * và kiểu dữ liệu tương ứng
	 */
	public LinkedHashMap<String, IType> getSchema(){
		return schema;
	}

	@Override
	public IExpression getDefaultValue() {
		return new ObjectExpression(this, new IExpression[schema.size()]);
	}

}
