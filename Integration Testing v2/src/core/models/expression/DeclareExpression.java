package core.models.expression;

import core.models.Expression;
import core.models.ExpressionGroup;
import core.models.Type;
import core.visitor.ExpressionVisitor;

/**
 * Một dạng "biểu thức" đặc biệt, mô tả câu lệnh khai báo biến trong chương trình.<br/>
 * Biểu thức này bao gồm kiểu khai báo, theo sau đó là một danh sách các biến được 
 * khai báo và có thể được gán giá trị mặc định
 * @example
 * <ul>
 * 	<li>int x, y;</li>
 * 	<li>float x, y = 0.1f, *z;</li>
 * </ul>
 * Biểu thức này thường đứng riêng lẻ, không kết hợp với các biểu thức khác (như là con
 * của một biểu thức nhóm nào đó)
 * @author ducvu
 *
 */
public class DeclareExpression extends ExpressionGroup {
	
	private Type mType;
	
	/**
	 * Tạo một biểu thức khai báo mới
	 * @param type kiểu khai báo
	 * @param declare danh sách các biểu thức biến được khai báo, thường là một
	 * {@link NameExpression}, {@link BinaryExpression} (phép gán),
	 *  {@link ArrayIndexExpression}
	 */
	public DeclareExpression(Type type, Expression... declare){
		super(declare);
		mType = type;
	}
	
	/**
	 * Trả về kiểu của khai báo
	 */
	public Type getDeclareType(){
		return mType;
	}
	
	/**
	 * Trả về danh sách các biểu thức biến được khai báo
	 */
	public Expression[] getDeclares(){
		return g;
	}
	
	@Override
	protected String generateContent() {
		String content = getDeclareType() + " " + g[0];
		
		for (int i = 1; i < g.length; i++)
			content += ", " + g[i];
		return content;
	}

	@Override
	protected int handle(ExpressionVisitor visitor) {
		return visitor.visit(this);
	}

}
