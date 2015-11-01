package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
import core.models.Type;

/**
 * Mô tả một biểu thức truy cập phần tử mảng, bao gồm tên của biến mảng, 
 * theo sau đó là tập các chỉ số dùng để xác định vị trí của phần tử mảng cần truy cập
 * @example
 * <ul>
 * 	<li>a[1]</li>
 * 	<li>b[x][y+1]</li>
 * </ul>
 * @author ducvu
 *
 */
public class ArrayIndexExpression extends ExpressionGroup implements NamedAttribute {
	
	private boolean mDeclare;
	private Type mType;
	
	/**
	 * Tạo một biểu thức truy cập mảng từ tên mảng và danh sách chỉ số
	 * @param arrayName tên của biến mảng đang tham chiếu
	 * @param index danh sách các chỉ số theo thứ tự từ ngoài vào trong, xác định vị trí
	 * phần tử trong mảng.<br/>
	 * Đối với chỉ số rỗng trong khai báo (thí dụ <code>int a[][3]<code>) ta truyền null
	 * vào vị trí rỗng đó (thí dụ <code>new ArrayExpression(a, [null, 3])</code>) 
	 */
	public ArrayIndexExpression(Expression arrayName, Expression... index){
		g = new Expression[index.length + 1];
		g[0] = arrayName;
		System.arraycopy(index, 0, g, 1, index.length);
		
		if (arrayName instanceof NameExpression)
			((NameExpression) arrayName).setRole(NameExpression.ROLE_ARRAY);
	}
	
	/**
	 * Tạo một biểu thức truy cập mảng từ tên mảng và danh sách chỉ số
	 * @param arrayName tên của biến mảng đang tham chiếu
	 * @param index danh sách các chỉ số nguyên theo thứ tự từ ngoài vào trong, 
	 * xác định vị trí phần tử trong mảng
	 */
	public ArrayIndexExpression(Expression arrayName, int... index){
		this(arrayName, int2exp(index));
	}
	
	@Override
	protected String generateContent() {
		String content = getName();
		for (Expression ep: g)
			content += "[" + ep + "]";
		return content;
	}
	
	/**
	 * Trả về danh sách các chỉ số truy cập mảng
	 * @note
	 * 	Đây là tham chiếu trực tiếp, không nên chỉnh sửa các phần tử trong tập này
	 */
	public Expression[] getIndexes(){
		return g;
	}
	
	/**
	 * Thiết đặt đây là một biểu thức khai báo mảng: int a[1][] = ...
	 */
	public ArrayIndexExpression setDeclare(){
		mDeclare = true;
		return this;
	}
	
	/**
	 * Kiểm tra đây là biểu thức trong khai báo
	 */
	public boolean isDeclare(){
		return mDeclare;
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}

	/**
	 * Chuyển từ mảng các chỉ số nguyên sang mảng chỉ số biểu thức
	 */
	private static Expression[] int2exp(int[] indexes){
		Expression[] arr = new Expression[indexes.length];
		
		for (int i = 0; i < arr.length; i++)
			arr[i] = new IDExpression(indexes[i]);
		return arr;
	}

	@Override
	public void setType(Type type) {
		mType = type;
		if (isCloneExpression())
			((ArrayIndexExpression)getSource()).setType(type);
	}

	@Override
	public Type getType() {
		return mType;
	}

	@Override
	public Expression getNameExpression() {
		return g[0];
	}
}
