package api.expression;

/**
 * Các biểu thức có thể đăng kí sự kiện khi giá trị được sử dụng
 */
public interface IRegistterValueUsed extends IExpression {

	/**
	 * Đăng kí sự kiện kích hoạt một lần khi biểu thức được sử dụng
	 */
	public void setOnValueUsedOne(OnValueUsed listener);
	
	/**
	 * Thông báo biểu thức đã được sử dụng
	 */
	public void notifyValueUsed();
	
	public interface OnValueUsed{
		public void valueUsed(IExpression ex);
	}
}
