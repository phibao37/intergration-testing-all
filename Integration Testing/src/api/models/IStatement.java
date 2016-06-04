package api.models;

import core.models.Statement;
import api.expression.IExpression;
import api.graph.IHasFileInfo;

/**
 * Giao diện cho một câu lệnh, nó có chứa một biểu thức gốc ứng với nội dung của nó.<br/>
 * Mỗi câu lệnh còn có liên kết tới 2 nhánh đúng/sai ứng với dòng điều khiển chương trình
 */
public interface IStatement extends IElement, IHasFileInfo {

	/**
	 * Gán biểu thức gốc cho câu lệnh này
	 * @param root biểu thức gốc đầy đủ chứa trong câu lệnh. Cấu trúc này phải được
	 * liên kết với các biểu thức con bên trong nó trước đó khi biểu thức này là 
	 * biểu thức nhóm
	 */
	public void setRoot(IExpression root);
	
	/**
	 * Trả về biểu thức gốc của câu lệnh này
	 */
	public IExpression getRoot();
	
	/**
	 * Thiết đặt câu lệnh ở nhánh đúng
	 */
	public IStatement setTrue(IStatement trueBranch);
	
	/**
	 * Thiết đặt câu lệnh ở nhánh sai
	 */
	public IStatement setFalse(IStatement falseBranch);
	
	/**
	 * Trả về câu lệnh ở nhánh đúng
	 */
	public IStatement getTrue();
	
	/**
	 * Trả về câu lệnh ở nhánh sai
	 */
	public IStatement getFalse();
	
	/**
	 * Kiểm tra câu lệnh điều kiện
	 */
	public default boolean isCondition(){
		return getTrue() != getFalse();
	}
	
	/**
	 * Kiểm tra câu lệnh dạng thông thường, khác với một số câu lệnh đặc biệt dùng
	 * để đánh dấu giúp cho việc xử lý
	 */
	public default boolean isNormal(){
		return true;
	}
	
	/**
	 * Có nên hiển thị trong đồ thị hay không
	 */
	public default boolean shouldDisplay(){
		return isNormal();
	}
	
	/**
	 * Có nên được nhóm lại trong một nhóm câu lệnh không
	 */
	public default boolean shouldInGroup(){
		return !isCondition();
	}
	
	/**
	 * Thiết đặt các nhánh liên kết
	 * @param trueBranch câu lệnh tiếp theo khi điều kiện câu lệnh đúng
	 * @param falseBranch câu lệnh tiếp theo khi điều kiện câu lệnh sai
	 */
	public default void setBranch(IStatement trueBranch, IStatement falseBranch){
		setTrue(trueBranch);
		setFalse(falseBranch);
	}
	
	/**
	 * Thiết đặt câu lệnh tiếp theo, áp dụng cho các câu lệnh phi điều kiện
	 * @param next câu lệnh tiếp theo sẽ được thực hiện
	 */
	public default void setBranch(IStatement next){
		setBranch(next, next);
	}
	
	/**
	 * Đánh dấu câu lệnh đã được thăm hay chưa, thường dùng cho việc duyệt đồ thị
	 * @param visited true: đã thăm qua câu lệnh
	 */
	public void setVisit(boolean visited);
	
	/**
	 * Kiểm tra câu lệnh đã được thăm hay chưa
	 * @see Statement#setVisit(boolean)
	 */
	public boolean isVisited();
	
	public void setId(int id);
	public int getId();
	
	
}
