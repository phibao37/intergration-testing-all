package core.expression;

import core.models.type.StringType;
import api.expression.IStringExpression;
import api.models.IType;

public class StringExpression extends Expression implements IStringExpression {

	private String value;
	
	public static final StringExpression EMPTY = new StringExpression("");
	
	public StringExpression(String value) {
		this.value = value;
	}

	@Override
	public IType getType() {
		return StringType.CONST_CHAR_ARR;
	}

	@Override
	public String stringValue() {
		return value;
	}

}
