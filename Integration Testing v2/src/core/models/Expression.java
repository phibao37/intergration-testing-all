package core.models;

import java.lang.reflect.InvocationTargetException;

import core.visitor.ExpressionVisitor;

/**
 * Mô tả một biểu thức có trong mã nguồn, có thẻ là:
 * <ul>
 * 	<li>12: biểu thức hằng số</li>
 * 	<li>x, y[0]: biểu thức biến số</li>
 * 	<li>x+y, x < y: biểu thức nhị phân</li>
 * 	<li>x++: biểu thức một bên</li>
 * 	<li>f(x, y): biểu thức gọi hàm</li>
 * </ul>
 * @author ducvu
 *
 */
public abstract class Expression extends Element {
	
	private Expression mSource = this;
	private boolean mBlockReplace = false;
	
	/**
	 * Khởi tạo một biểu thức rỗng<br/>
	 * Sử dụng {@link #setContent(String)} để thiết đặt nội dung cho biểu thức
	 */
	public Expression() {
		this(null);
	}
	
	/**
	 * Khởi tạo một biểu thức cùng với nội dung lưu trữ của nó
	 * @param content nội dung biểu thức, là chuỗi hiển thị của biểu thức trong mã nguồn
	 */
	public Expression(String content){
		super(content);
	}
	
	/**
	 * Trả về nội dung hiển thị của biểu thức
	 */
	public String getContent(){
		if (mContent == null)
			mContent = generateContent();
		return mContent;
	}
	
	/**
	 * Tạo ra nội dung hiển thị của biểu thức khi nội dung này chưa được thiết đặt
	 * @return nội dung hiển thị mới
	 */
	protected String generateContent(){
		return mContent;
	}
	
	/**
	 * Tạo ra một bản sao của biểu thức. Các biểu thức bản sao và cả bản chính đều sẽ
	 * có chung một nguồn, sử dụng {@link #equalsSource(Expression)} để kiểm tra
	 */
	public Expression clone(){
		Expression clone = (Expression) super.clone();
		
		clone.mSource = this.mSource;
		return clone;
	}
	
	/**
	 * Kiểm tra 2 biểu thức được sao chép từ cùng một nguồn, hoặc cái này được
	 * sao chép từ cái kia (sao chép qua {@link #clone()})
	 */
	protected boolean equalsSource(Expression other){
		return mSource == other.mSource;
	}
	
	/**
	 * Đánh dấu một biểu thức có được phép thay thế hay không
	 * (xem {@link ExpressionGroup#replace(Expression, Expression)})
	 * @param block <b>true</b>: không cho phép thay thế chính nó và các biểu thức
	 * con của nó, hoặc <b>false (mặc định)</b> nếu cho phép
	 * @return đối tượng biểu thức hiện thời
	 */
	public Expression setBlockReplace(boolean block){
		mBlockReplace = block;
		return this;
	}
	
	/**
	 * Kiểm tra việc khóa sự thay thế
	 */
	protected boolean canNotReplace(){
		return mBlockReplace;
	}
	
	/**
	 * Áp dụng một bộ duyệt cấu trúc cho biểu thức. Bộ duyệt này sẽ lần lượt được
	 * truyền từ biểu thức gốc, sau đó là các biểu thức con bên trong nó
	 * @param visitor bộ duyệt trên các biểu thức. Để "bắt" được các biểu biểu thức đang
	 * được duyệt qua, sử dụng (override) một trong các phương thức 
	 * <code>int visit(SomeExpression ep) {...}</code> trong bộ duyệt, với
	 * <code>SomeExpression</code> là loại biểu thức cần "bắt"
	 * @return
	 * {@link ExpressionVisitor#PROCESS_CONTINUE}: đã duyệt qua hết mọi biểu thức<br/>
	 * {@link ExpressionVisitor#PROCESS_SKIP}: các biểu thức con bị bỏ qua<br/>
	 * {@link ExpressionVisitor#PROCESS_ABORT}: một biểu thức đã hủy áp dụng<br/>
	 */
	public int accept(ExpressionVisitor visitor){
		int process = ExpressionVisitor.PROCESS_SKIP;
		
		//Tiền xử lý biểu thức cho phép thăm chính thức
		if (visitor.preVisit(this)){
			
			//Chính thức thăm một biểu thức ứng với kiểu cụ thể
			process = handle(visitor);
			
			//Kết quả thăm biểu thức cho phép duyệt các biểu thức con
			if (process == ExpressionVisitor.PROCESS_CONTINUE 
					&& this instanceof ExpressionGroup){
				
				//Áp dụng bộ duyệt cho biểu thức con
				for (Expression e: ((ExpressionGroup)this).g){
					if (e == null) continue;
					int child_process = e.accept(visitor);
					
					//Kết quả duyệt biểu thức con yêu cầu hủy quá trình duyệt,
					//bỏ qua các biểu thức con khác, trả về kết quả lên biểu thức cha
					if (child_process == ExpressionVisitor.PROCESS_ABORT){
						process = child_process;
						break;
					}
				}
				
			}
		}
		
		//Hậu xử lý và trả về kết quả duyệt
		visitor.postVisit(this);
		return process;
	}
	
	/**
	 * Xử lý với từng loại biểu thức
	 */
	private int handle(ExpressionVisitor visitor){
		boolean stop = false;
		Class<?> cls = getClass();
		
		do{
			try {
				return (int) ExpressionVisitor.class
						.getMethod("visit", cls)
						.invoke(visitor, this);
			} catch (NoSuchMethodException 
					| IllegalAccessException | InvocationTargetException e) {
				cls = cls.getSuperclass();
				if (cls == Expression.class || cls == ExpressionGroup.class)
					stop = true;
			}
		} while (!stop);
		return ExpressionVisitor.PROCESS_CONTINUE;
	}
	
	/**
	 * Trả về <i>true</i> nếu giá trị của biểu thức luôn là một hằng số<br/>
	 * Trả về <i>false</i> nếu trong biểu thức tồn tại các tham số, biến,...
	 */
	public abstract boolean isConstant();
	
	/**
	 * In cây quan hệ
	 * @param margin khoảng cách đầu dòng
	 */
	public void printTree(String margin){
		System.out.println(margin + this + ", " + this.getClass().getSimpleName());
		if (this instanceof ExpressionGroup){
			Expression[] g = ((ExpressionGroup)this).g;
			for (Expression ep: g)
				if (ep == null)
					System.out.println(margin + "   NULL");
				else
					ep.printTree(margin + "   ");
		}
	}
	
}
