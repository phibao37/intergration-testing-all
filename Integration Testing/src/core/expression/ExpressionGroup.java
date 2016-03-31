package core.expression;

import api.expression.IExpression;
import api.expression.IExpressionGroup;
import api.expression.IExpressionVisitor;
import api.models.IType;

/**
 * Kiểu biểu thức nhóm trong ngôn ngữ mà nó chứa các biểu thức con ở bên trong.<br/>
 * Thí dụ: (x > 0) && (x < 10) là biểu thức nhóm bao gồm 2 biểu thức con là
 * (x > 0), (x < 10)
 * @author ducvu
 *
 */
public abstract class ExpressionGroup extends Expression implements IExpressionGroup {
	
	/**
	 * Mảng các biểu thức con
	 */
	protected IExpression[] g;
	
	/**
	 * Khởi tạo biểu thức nhóm cùng với danh sách các biểu thức con của nó
	 * @param elements danh sách biểu thức con
	 */
	public ExpressionGroup(IExpression... elements) {
		g = elements;
	}
	
	/**
	 * Đặt nội dung hiển thị của biểu thức nhóm.<br/>
	 * Để truy cập mảng các biểu thức con, sử dụng đối tượng {@link #g}<br/>
	 */
	protected abstract String generateContent();
	
	/**
	 * Thông báo rằng nội dung hiển thị đã bị thay đổi do có sự chỉnh sửa các phần tử
	 */
	protected final void notifyContentChanged(){
		mContent = null;
	}
	
	/**
	 * Thay thế một biểu thức con bằng một biểu thức khác tại cùng vị trí
	 * @param find biểu thức cần thay thế. Một biểu thức được gọi là khớp để thay thế
	 * nếu 2 biểu thức này đều là một (phép so sánh ==) hoặc có cùng chung một nguồn 
	 * (xem {@link #equalsSource(Expression)})
	 * @param replace biểu thức sẽ được thay thế tại vị trí tương ứng
	 * @return 
	 * <i>true</i>: thay thế thành công<br/>
	 * <i>false</i>: biểu thức cần thay thế không được tìm thấy
	 * @throws NullPointerException biểu thức tìm kiểm bằng null
	 */
	public boolean replaceChild(IExpression find, IExpression replace) {
		boolean replaced = false;
		
		//Duyệt qua các biểu thức con trong nhóm
		for (int i = 0; i < g.length; i++){
			IExpression item = g[i];
			
			//Biểu thức con này không cho phép thay thế, bỏ qua
			if (item == null || item.isReplaceBlocked())
				continue;
			
			//Nếu biểu thức con khớp với biểu thức tìm kiểm, thay thế nó tại cùng vị trí
			if (find.equalsSource(item)){
				g[i] = replace;
				replaced = true;
			}
			
			//Tiếp tục tìm trong biểu thức con này khi chính nó cũng là biểu thức nhóm
			else if (item instanceof ExpressionGroup){
				replaced = ((ExpressionGroup) item).replaceChild(find, replace);
			}
			
			if (replaced)
				break;
		}
		
		//Đã có sự thay thế thành công, làm mới lại nội dung hiển thị
		if (replaced)
			setContent(generateContent());
		
		return replaced;
	}

	/**
	 * Một biểu thức nhóm là hằng số nếu tất cả các biểu thức con đều là hằng số
	 */
	@Override
	public boolean isConstant() {
		for (IExpression child: g)
			if (!child.isConstant())
				return false;
		return true;
	}

	/**
	 * Tạo ra một bản sao của biểu thức. Các thành phần biểu thức con cũng tự nó được
	 * sao chép (deep-clone)
	 */
	@Override
	public ExpressionGroup clone(){
		ExpressionGroup clone = (ExpressionGroup) super.clone();
		
		clone.g = new Expression[g.length];
		for (int i = 0; i < g.length; i++){
			if (g[i] != null)
			clone.g[i] = g[i].clone();
		}
		return clone;
	}

	@Override
	public IExpression ungroup() {
		return this;
	}
	
	
}

class SingleGroupExpression extends ExpressionGroup{

	public SingleGroupExpression(IExpression ex) {
		super(ex);
	}
	
	@Override
	public IExpressionGroup group() {
		return this;
	}
	
	@Override
	public IExpression ungroup() {
		return g[0];
	}

	@Override
	public IType getType() {
		return g[0].getType();
	}

	@Override
	public int _handleVisit(IExpressionVisitor visitor) {
		return IExpressionVisitor.PROCESS_CONTINUE;
	}

	@Override
	public void _handleLeave(IExpressionVisitor visitor) {}

	@Override
	protected String generateContent() {
		return g[0].getContent();
	}
	
}





