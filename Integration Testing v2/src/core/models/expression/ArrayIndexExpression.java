package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
import core.visitor.ExpressionVisitor;

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
	
	private String mName;
	private boolean mDeclare;
	
	/**
	 * Tạo một biểu thức truy cập mảng từ tên mảng và danh sách chỉ số
	 * @param arrayName tên của biến mảng đang tham chiếu
	 * @param index danh sách các chỉ số theo thứ tự từ ngoài vào trong, xác định vị trí
	 * phần tử trong mảng.<br/>
	 * Đối với chỉ số rỗng trong khai báo (thí dụ <code>int a[][3]<code>) ta truyền null
	 * vào vị trí rỗng đó (thí dụ <code>new ArrayExpression(a, [null, 3])</code>) 
	 */
	public ArrayIndexExpression(String arrayName, Expression... index){
		super(index);
		mName = arrayName;
	}
	
	@Override
	protected String generateContent() {
		String content = getName();
		for (Expression ep: g)
			content += "[" + ep + "]";
		return content;
	}

	/**
	 * Trả về tên của biến mảng đang tham chiếu
	 */
	@Override
	public String getName() {
		return mName;
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
	protected int handle(ExpressionVisitor visitor) {
		return visitor.visit(this);
	}
}
