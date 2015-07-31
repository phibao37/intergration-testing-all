package core.eval;

import core.models.Expression;
import core.models.Type;
import core.models.expression.BinaryExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.UnaryExpression;
import core.models.type.BasicType;

/**
 * Bộ tính toán đơn giản, bao gồm tính toán giá trị, sau đó rút gọn kiểu nếu được
 * (1.0 => 1)
 */
public class SimpleEval implements Evaluateable {
	
	/**
	 * Bộ tính toán mặc định
	 */
	public static final SimpleEval DEFAULT = new SimpleEval();
	
	@Override
	public Expression evalExpression(Expression expression) {
		//if (expression.isConstant())
			//expression = calculate(expression);
		
		return expression;
	}
	
	
	/**
	 * Tính toán biểu thức và trả về kết quả hằng
	 */
	public static IDExpression calculate(Expression ex){
		
		//Các phép toán 1 bên
		if (ex instanceof UnaryExpression){
			UnaryExpression unary = (UnaryExpression) ex;
			IDExpression sub = calculate(unary.getSubElement());
			Type subType = sub.getType();
			String op = unary.getOperator();
			
			//Phép toán phủ định: !
			if (op.equals(UnaryExpression.LOGIC_NOT)){
				
				//Nếu chưa phải là kiểu bool, chuyển sang: 1,2,-1 => true, 0 => false
				if (subType != BasicType.BOOL)
					sub = number2Bool(sub);
					
				return new IDExpression(!(boolean)sub.getJavaValue());	
			}
			
			//Phép toán +(4), không cần xử lý
			else if (op.equals(UnaryExpression.PLUS))
				return sub;
			
			//Phép toán -(-4), cần xử lý => 4
			else {
				String num = sub.getContent();
				
				if (num.charAt(0) == '-')
					return new IDExpression(num.substring(1));
				else
					return new IDExpression("-" + num);
			}
		}
		
		//Các phép toán 2 bên
		else if (ex instanceof BinaryExpression){
			
		}
		
		else if (ex instanceof FunctionCallExpression)
			throw new RuntimeException("I can't calculate this: " + ex);
		
		return (IDExpression) ex;
	}
	
	/**
	 * Ép kiểu từ số sang boolean, thí dụ: !1 => !true
	 */
	public static IDExpression number2Bool(IDExpression num){
		return calculate(new BinaryExpression(
				num, 
				BinaryExpression.NOT_EQUALS, 
				new IDExpression(0)
		));
	}
	
}
