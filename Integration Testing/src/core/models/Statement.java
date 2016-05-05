package core.models;

import api.expression.IExpression;
import api.graph.IFileInfo;
import api.models.IStatement;

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
 *
 */
public class Statement extends Element implements IStatement {

	private IStatement mTrue, mFalse;
	private IExpression mRoot;
	private boolean mVisit;
	private int id;
	private IFileInfo fInfo;
	
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

	@Override
	public void setRoot(IExpression root) {
		mRoot = root;
	}

	@Override
	public IExpression getRoot() {
		return mRoot;
	}

	@Override
	public IStatement setTrue(IStatement trueBranch) {
		mTrue = trueBranch;
		return this;
	}

	@Override
	public IStatement setFalse(IStatement falseBranch) {
		mFalse = falseBranch;
		return this;
	}

	@Override
	public IStatement getTrue() {
		return mTrue;
	}

	@Override
	public IStatement getFalse() {
		return mFalse;
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
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setSourceInfo(IFileInfo info) {
		fInfo = info;
	}

	@Override
	public IFileInfo getSourceInfo() {
		return fInfo;
	}

}
