package core.models;

import core.graph.Graphable;
import core.models.expression.ReturnExpression;

/**
 * Một câu lệnh đại diện cho một đỉnh trong dòng đường đi của chương trình,
 * thường là các <i>câu lệnh thông thường</i> của các ngôn ngữ lập trình
 * (thí dụ: <code>int a = 0;</code>).<br/>
 * Tuy nhiên với các <i>câu lệnh "phức hợp"</i> như:
 * <pre>
 * for (int i = 0; i < n; i++)
 * 	x = x + 1;
 * </pre>
 * , chúng ta có tới 4 câu lệnh:
 * <ul>
 * 	<li>int i = 0</li>
 * 	<li>i < n</li>
 * 	<li>i++</li>
 * 	<li>x = x + 1</li>
 * </ul>
 * Một câu lệnh luôn có nhánh đúng và nhánh sai của nó, đó là các câu lệnh
 * tiếp theo sẽ được thực thi ứng với biểu thức trong câu lệnh hiện thời mang giá trị
 * ĐÚNG hoặc SAI. Các câu lệnh mà biểu thức trong nó không là câu lệnh điều kiện
 * được ngầm định cả hai nhánh đều chỉ tới cùng một câu lệnh tiếp theo của nó.<br/><br/>
 * Một câu lệnh luôn có một biểu thức gốc 
 * bên trong của nó (xem {@link #setRoot(Expression)})
 * <br/><br/>
 * Dấu kết thúc ";" nên được bỏ qua trong thể hiện của câu lệnh
 * @author ducvu
 *
 */
public class Statement extends Element implements Graphable {
	
	private Statement mTrue, mFalse;
	private Expression mRoot;
	private boolean mVisit;
	private int mType;
	
	/**
	 * Tạo một câu lệnh với nội dung của nó
	 */
	public Statement(String content){
		super(content);
	}
	
	/**
	 * Tạo một câu lệnh với nội dung và hai nhánh của nó
	 * @see #setBranch(Statement, Statement)
	 */
	public Statement(String content, Statement trueBranch, Statement falseBranch){
		super(content);
		setBranch(trueBranch, falseBranch);
	}
	
	/**
	 * Thiết đặt hai nhánh cho câu lệnh hiện thời. Với câu lệnh không là điều kiện, 
	 * cả hai nhánh này là như nhau.<br/>
	 * Ví dụ về nhánh cho câu lệnh hiện thời: <code>x < 0</code>
	 * <pre>
	 * if (x < 0)
	 * 	x = 1;
	 * else
	 * 	x = -1;
	 * </pre>
	 * @param trueBranch Câu lệnh <code>x = 1</code> trong ví dụ
	 * @param falseBranch Câu lệnh <code>x = -1</code> trong ví dụ
	 */
	public void setBranch(Statement trueBranch, Statement falseBranch){
		mTrue = trueBranch;
		mFalse = falseBranch;
	}
	
	/**
	 * Thiết đặt hai nhánh của câu lệnh tới cùng một câu lệnh tiếp theo.<br/>
	 * Dành cho các câu lệnh không là câu lệnh điều kiện
	 * @param nextStatement câu lệnh tiếp theo trên đường thực thi
	 * @see #setBranch(Statement, Statement)
	 */
	public void setBranch(Statement nextStatement){
		setBranch(nextStatement, nextStatement);
	}
	
	/**
	 * Đặt nhánh đúng cho câu lệnh hiện thời
	 * @param trueBranch câu lệnh ở nhánh đúng
	 */
	public void setTrue(Statement trueBranch){
		setBranch(trueBranch, mFalse);
	}
	
	/**
	 * Đặt nhánh sai cho câu lệnh hiện thời
	 * @param trueBranch câu lệnh ở nhánh sai
	 */
	public void setFalse(Statement falseBranch){
		setBranch(mTrue, falseBranch);
	}
	
	/**
	 * Trả về câu lệnh ở nhánh đúng của câu lệnh hiện thời 
	 */
	public Statement getTrue(){
		return mTrue;
	}
	
	/**
	 * Trả về câu lệnh ở nhánh sai của câu lệnh hiện thời 
	 */
	public Statement getFalse(){
		return mFalse;
	}
	
	/**
	 * Kiểm tra câu lệnh điều kiện
	 */
	public boolean isCondition(){
		return mTrue != mFalse;
	}
	
	/**
	 * Gán biểu thức gốc cho câu lệnh này
	 * @param root biểu thức gốc đầy đủ chứa trong câu lệnh. Cấu trúc này phải được
	 * liên kết với các biểu thức con bên trong nó trước đó khi biểu thức này là 
	 * biểu thức nhóm.<br/>
	 * Kiểu của câu lệnh ({@link #getType()}) được xác định khi gọi phương thức này
	 * @example
	 * Câu lệnh: <code>(x>0)&&(x<10)</code><br/>
	 * Gốc: biểu thức biểu thức nhị phân đã có 2 biểu thức con là (x>0) và (x<10)
	 */
	public void setRoot(Expression root){
		mRoot = root;
		
		if (root instanceof ReturnExpression)
			mType = RETURN;
	}
	
	/**
	 * Trả về biểu thức gốc của câu lệnh này
	 * @see #setRoot(Expression)
	 */
	public Expression getRoot(){
		return mRoot;
	}
	
	/**
	 * Trả về kiểu của câu lệnh, được xác định khi gọi {@link #setRoot(Expression)}
	 */
	public int getType(){
		if (isCondition())
			mType = CONDITION;
		return mType;
	}
	
	/**
	 * Đánh dấu câu lệnh đã được thăm hay chưa, thường dùng cho việc duyệt đồ thị
	 * @param visited true: đã thăm qua câu lệnh
	 */
	public void setVisit(boolean visited){
		mVisit = visited;
	}
	
	/**
	 * Kiểm tra câu lệnh đã được thăm hay chưa
	 * @see Statement#setVisit(boolean)
	 */
	public boolean isVisited(){
		return mVisit;
	}

	@Override
	public String getHTMLContent() {
		return getContent();
	}
	
	/**
	 * Câu lệnh chưa xác định
	 */
	public static final int UNSPECIFIED = 0;
	
	/** 
	 * Câu lệnh khai báo biến
	 */
	public static final int DECLARATION = 1;
	
	/**
	 * Câu lệnh gán biến
	 */
	public static final int ASSIGNMENT = 2;
	
	/**
	 * Câu lệnh điều kiện
	 */
	public static final int CONDITION = 3;
	
	/**
	 * Câu lệnh gọi hàm
	 */
	public static final int FUNCTION_CALL = 4;	
	
	/**
	 * Câu lệnh return, kêt thúc hàm
	 */
	public static final int RETURN = 5;
	
}








