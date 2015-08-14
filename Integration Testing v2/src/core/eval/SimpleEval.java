package core.eval;

import core.models.Expression;
import core.models.expression.BinaryExpression;
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
		if (expression.isConstant())
			expression = calculate(expression);
		
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
			String op = unary.getOperator();
			
			//Phép toán phủ định: !
			if (op.equals(UnaryExpression.LOGIC_NOT))
				return new IDExpression(!sub.boolValue());	
			
			//Phép toán +(-4), không cần xử lý
			else if (op.equals(UnaryExpression.PLUS))
				return sub;
			
			//Phép toán -(-4), cần xử lý => 4
			else {
				switch (sub.getType().getSize()){
				case BasicType.LONG_SIZE:
					return new IDExpression(-sub.longValue());
				case BasicType.FLOAT_SIZE:
					return new IDExpression(-sub.floatValue());
				case BasicType.DOUBLE_SIZE:
					return new IDExpression(-sub.doubleValue());
				default:
					return new IDExpression(-sub.intValue());
				}
				
			}
		}
		
		//Các phép toán 2 bên
		else if (ex instanceof BinaryExpression){
			BinaryExpression bin = (BinaryExpression) ex;
			IDExpression l = calculate(bin.getLeft());
			IDExpression r = calculate(bin.getRight());
			String op = bin.getOperator();
			
			//Phép toán logic AND, OR
			if (op.equals(BinaryExpression.LOGIC_AND))
				return new IDExpression(l.boolValue() && r.boolValue());
			else if (op.equals(BinaryExpression.LOGIC_OR))
				return new IDExpression(l.boolValue() || r.boolValue());
			
			//Các phép toán tính toán (+,-, ...), và so sánh (==, <, ...)
			else {
				int max = Math.max(l.getType().getSize(), r.getType().getSize());
				
				if (op.equals(BinaryExpression.EQUALS)){
					switch (max){
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() == r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() == r.doubleValue());
					default:
						return new IDExpression(l.longValue() == r.longValue());
					}
				}
				
				else if (op.equals(BinaryExpression.NOT_EQUALS)){
					switch (max){
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() != r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() != r.doubleValue());
					default:
						return new IDExpression(l.longValue() != r.longValue());
					}
				}
				
				else if (op.equals(BinaryExpression.LESS)){
					switch (max){
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() < r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() < r.doubleValue());
					default:
						return new IDExpression(l.longValue() < r.longValue());
					}
				}
				
				else if (op.equals(BinaryExpression.LESS_EQUALS)){
					switch (max){
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() <= r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() <= r.doubleValue());
					default:
						return new IDExpression(l.longValue() <= r.longValue());
					}
				}
				
				else if (op.equals(BinaryExpression.GREATER)){
					switch (max){
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() > r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() > r.doubleValue());
					default:
						return new IDExpression(l.longValue() > r.longValue());
					}
				}
				
				else if (op.equals(BinaryExpression.GREATER_EQUALS)){
					switch (max){
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() >= r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() >= r.doubleValue());
					default:
						return new IDExpression(l.longValue() >= r.longValue());
					}
				}
				
				else if (op.equals(BinaryExpression.ADD)){
					switch (max){
					case BasicType.LONG_SIZE:
						return new IDExpression(l.longValue() + r.longValue());
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() + r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() + r.doubleValue());
					default:
						return new IDExpression(l.intValue() + r.intValue());
					}
				}
				
				else if (op.equals(BinaryExpression.MINUS)){
					switch (max){
					case BasicType.LONG_SIZE:
						return new IDExpression(l.longValue() - r.longValue());
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() - r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() - r.doubleValue());
					default:
						return new IDExpression(l.intValue() - r.intValue());
					}
				}
				
				else if (op.equals(BinaryExpression.MUL)){
					switch (max){
					case BasicType.LONG_SIZE:
						return new IDExpression(l.longValue() * r.longValue());
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() * r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() * r.doubleValue());
					default:
						return new IDExpression(l.intValue() * r.intValue());
					}
				}
				
				else if (op.equals(BinaryExpression.DIV)){
					switch (max){
					case BasicType.LONG_SIZE:
						return new IDExpression(l.longValue() / r.longValue());
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() / r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() / r.doubleValue());
					default:
						return new IDExpression(l.intValue() / r.intValue());
					}
				}
				
				else if (op.equals(BinaryExpression.MOD)){
					switch (max){
					case BasicType.LONG_SIZE:
						return new IDExpression(l.longValue() % r.longValue());
					case BasicType.FLOAT_SIZE:
						return new IDExpression(l.floatValue() % r.floatValue());
					case BasicType.DOUBLE_SIZE:
						return new IDExpression(l.doubleValue() % r.doubleValue());
					default:
						return new IDExpression(l.intValue() % r.intValue());
					}
				}
			}
		}
		
		try{
			return (IDExpression) ex;
		} catch (Exception e){
			System.out.println("Cast error: " + ex);
			throw new RuntimeException(e);
		}
	}
	
}
