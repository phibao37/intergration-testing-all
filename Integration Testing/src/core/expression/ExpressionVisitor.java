package core.expression;

import api.models.IStatement;

/**
 * Bộ visit dùng để duyệt trong cây biểu thức
 *
 */
public abstract class ExpressionVisitor {
	
	/**
	 * Bỏ qua việc duyệt các biểu thức khác, thoát hoàn toàn
	 */
	public static final int PROCESS_ABORT = -1;
	
	/**
	 * Bỏ qua duyệt các biểu thức con, tiếp tục duyệt các biểu thức sau
	 */
	public static final int PROCESS_SKIP = 0;
	
	/**
	 * Tiếp tục duyệt các biểu thức con, sau đó là các biểu thức đằng sau
	 */
	public static final int PROCESS_CONTINUE = 1;
	
	
	/**
	 * Trước khi duyệt qua một biểu thức nào đó
	 * @param expression biểu thức sẽ được duyệt
	 * @return 
	 * <b>true</b>: duyệt qua biểu thức
	 * <b>false</b>: bỏ qua biểu thức
	 */
	public boolean preVisit(Expression expression){
		return true;
	}
	
	/**
	 * Sau khi duyệt qua một biểu thức nào đó
	 * @param expression biểu thức đã được duyệt
	 */
	public void postVisit(Expression expression) {}
	
	
	public int visit(NameExpression name){
		return PROCESS_CONTINUE;
	}
	
	public int visit(FunctionCallExpression call){
		return PROCESS_CONTINUE;
	}
	
	public int visit(ArrayExpression array){
		return PROCESS_CONTINUE;
	}
	
	public int visit(ArrayIndexExpression array){
		return PROCESS_CONTINUE;
	}
	
	public int visit(BinaryExpression bin){
		return PROCESS_CONTINUE;
	}
	
	public int visit(UnaryExpression unary){
		return PROCESS_CONTINUE;
	}
	
	public int visit(DeclareExpression declare){
		return PROCESS_CONTINUE;
	}
	
	public int visit(NumberExpression id){
		return PROCESS_CONTINUE;
	}
	
//	public int visit(PlaceHolderExpression place){
//		return PROCESS_CONTINUE;
//	}
//	
//	public int visit(ObjectExpression object){
//		return PROCESS_CONTINUE;
//	}
	
	public int visit(MemberAccessExpression member){
		return PROCESS_CONTINUE;
	}
	
	public void leave(NameExpression name){}
	
	public void leave(FunctionCallExpression call){}
	
	public void leave(ArrayExpression array){}
	
	public void leave(ArrayIndexExpression array){}
	
	public void leave(BinaryExpression bin){}
	
	public void leave(UnaryExpression unary){}
	
	public void leave(DeclareExpression declare){}
	
	public void leave(NumberExpression id){}
	
	//public void leave(PlaceHolderExpression place){}
	
	//public void leave(ObjectExpression object) {}
	
	public void leave(MemberAccessExpression member) {}
	
	/**
	 * Được gọi khi đang duyệt qua các câu lệnh trong hàm số
	 * @param statement câu lệnh đang được duyệt qua
	 * @return {@link #PROCESS_CONTINUE} biểu thức gốc sẽ được thăm tiếp theo<br/>
	 * {@link #PROCESS_SKIP} bỏ qua biểu thức gốc, thăm câu lệnh kế tiếp<br/>
	 * {@link #PROCESS_CONTINUE} hủy bỏ quả trình duyệt
	 */
	public int visit(IStatement statement){
		return PROCESS_CONTINUE;
	}
}
