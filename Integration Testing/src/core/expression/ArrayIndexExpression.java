package core.expression;

import api.expression.IArrayIndexExpression;
import api.expression.IExpression;
import core.models.type.Type;

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
public class ArrayIndexExpression extends ExpressionGroup implements IArrayIndexExpression {
	
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
	public ArrayIndexExpression(IExpression arrayName, IExpression... index){
		g = new IExpression[index.length + 1];
		g[0] = arrayName;
		System.arraycopy(index, 0, g, 1, index.length);
		
		//if (arrayName instanceof NameExpression)
			//((NameExpression) arrayName).setRole(NameExpression.ROLE_ARRAY);
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
		String content = g[0].getContent();
		for (int i = 1; i < g.length; i++)
			content += "[" + g[i] + "]";
		return content;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IArrayIndexExpression#getIndexes()
	 */
	@Override
	public Expression[] getIndexes(){
		Expression[] indexs = new Expression[g.length - 1];
		System.arraycopy(g, 1, indexs, 0, indexs.length);
		return indexs;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IArrayIndexExpression#setDeclare()
	 */
	@Override
	public ArrayIndexExpression setDeclare(){
		mDeclare = true;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IArrayIndexExpression#isDeclare()
	 */
	@Override
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
			arr[i] = new NumberExpression(indexes[i]);
		return arr;
	}

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
	public IExpression getName() {
		return g[0];
	}

}
