package api.expression;

public interface INumberExpression extends IConstantExpression {

	/**
	 * Trả về giá trị số nguyên của biểu thức, hoặc 0 nếu không thể chuyển sang được
	 */
	public abstract int intValue();

	/**
	 * Trả về giá trị số nguyên của biểu thức, hoặc 0 nếu không thể chuyển sang được
	 */
	public abstract long longValue();

	/**
	 * Trả về giá trị số thực của biểu thức, hoặc 0.0 nếu không thể chuyển sang được
	 */
	public abstract float floatValue();

	/**
	 * Trả về giá trị số thực của biểu thức, hoặc 0.0 nếu không thể chuyển sang được
	 */
	public abstract double doubleValue();

	/**
	 * Trả về giá trị kí tự của biểu thức, hoặc 0 nếu không thể chuyển sang được
	 */
	public abstract char charValue();

	/**
	 * Trả về giá trị nhị phân của biểu thức, hoặc false nếu không thể chuyển sang được
	 */
	public abstract boolean boolValue();

}