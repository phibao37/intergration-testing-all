package core.models.type;

import core.expression.StringExpression;
import api.expression.IExpression;

public class StringType extends Type {

	public static final StringType CONST_CHAR_ARR = new StringType("const char*", 1);
	public static final StringType CHAR_ARR = new StringType("char*", 2);
	
	protected StringType(String content, int size) {
		super(content, size);
	}

	@Override
	public IExpression getDefaultValue() {
		return StringExpression.EMPTY;
	}


}
