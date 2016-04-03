package core.expression;

import api.expression.IExpression;
import api.expression.IExpressionEval;
import core.models.type.BasicType;

public class ExpressionEval implements IExpressionEval{

	@Override
	public IExpression eval(IExpression ex) {
		if (ex.isConstant())
			ex = calculate(ex);
		
		return ex;
	}

	/**
	 * Tính toán biểu thức và trả về kết quả hằng
	 */
	public static NumberExpression calculate(IExpression ex){
		
		//Các phép toán 1 bên
		if (ex instanceof UnaryExpression){
			UnaryExpression unary = (UnaryExpression) ex;
			NumberExpression sub = calculate(unary.getSubElement());
			String op = unary.getOperator();
			
			//Phép toán phủ định: !
			switch (op) {
				case UnaryExpression.LOGIC_NOT:
					return new NumberExpression(!sub.boolValue());
//Phép toán +(-4), không cần xử lý
				case UnaryExpression.PLUS:
					return sub;

				//Phép toán -(-4), cần xử lý => 4
				default:
					switch (sub.getType().getSize()) {
						case BasicType.LONG_SIZE:
							return new NumberExpression(-sub.longValue());
						case BasicType.FLOAT_SIZE:
							return new NumberExpression(-sub.floatValue());
						case BasicType.DOUBLE_SIZE:
							return new NumberExpression(-sub.doubleValue());
						default:
							return new NumberExpression(-sub.intValue());
					}

			}
		}
		
		//Các phép toán 2 bên
		else if (ex instanceof BinaryExpression){
			BinaryExpression bin = (BinaryExpression) ex;
			NumberExpression l = calculate(bin.getLeft());
			NumberExpression r = calculate(bin.getRight());
			String op = bin.getOperator();
			
			//Phép toán logic AND, OR
			switch (op) {
				case BinaryExpression.LOGIC_AND:
					return new NumberExpression(l.boolValue() && r.boolValue());
				case BinaryExpression.LOGIC_OR:
					return new NumberExpression(l.boolValue() || r.boolValue());

				//Các phép toán tính toán (+,-, ...), và so sánh (==, <, ...)
				default:
					int max = Math.max(l.getType().getSize(), r.getType().getSize());

					switch (op) {
						case BinaryExpression.EQUALS:
							switch (max) {
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() == r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() == r.doubleValue());
								default:
									return new NumberExpression(l.longValue() == r.longValue());
							}
						case BinaryExpression.NOT_EQUALS:
							switch (max) {
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() != r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() != r.doubleValue());
								default:
									return new NumberExpression(l.longValue() != r.longValue());
							}
						case BinaryExpression.LESS:
							switch (max) {
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() < r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() < r.doubleValue());
								default:
									return new NumberExpression(l.longValue() < r.longValue());
							}
						case BinaryExpression.LESS_EQUALS:
							switch (max) {
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() <= r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() <= r.doubleValue());
								default:
									return new NumberExpression(l.longValue() <= r.longValue());
							}
						case BinaryExpression.GREATER:
							switch (max) {
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() > r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() > r.doubleValue());
								default:
									return new NumberExpression(l.longValue() > r.longValue());
							}
						case BinaryExpression.GREATER_EQUALS:
							switch (max) {
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() >= r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() >= r.doubleValue());
								default:
									return new NumberExpression(l.longValue() >= r.longValue());
							}
						case BinaryExpression.ADD:
							switch (max) {
								case BasicType.LONG_SIZE:
									return new NumberExpression(l.longValue() + r.longValue());
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() + r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() + r.doubleValue());
								default:
									return new NumberExpression(l.intValue() + r.intValue());
							}
						case BinaryExpression.MINUS:
							switch (max) {
								case BasicType.LONG_SIZE:
									return new NumberExpression(l.longValue() - r.longValue());
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() - r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() - r.doubleValue());
								default:
									return new NumberExpression(l.intValue() - r.intValue());
							}
						case BinaryExpression.MUL:
							switch (max) {
								case BasicType.LONG_SIZE:
									return new NumberExpression(l.longValue() * r.longValue());
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() * r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() * r.doubleValue());
								default:
									return new NumberExpression(l.intValue() * r.intValue());
							}
						case BinaryExpression.DIV:
							switch (max) {
								case BasicType.LONG_SIZE:
									return new NumberExpression(l.longValue() / r.longValue());
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() / r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() / r.doubleValue());
								default:
									return new NumberExpression(l.intValue() / r.intValue());
							}
						case BinaryExpression.MOD:
							switch (max) {
								case BasicType.LONG_SIZE:
									return new NumberExpression(l.longValue() % r.longValue());
								case BasicType.FLOAT_SIZE:
									return new NumberExpression(l.floatValue() % r.floatValue());
								case BasicType.DOUBLE_SIZE:
									return new NumberExpression(l.doubleValue() % r.doubleValue());
								default:
									return new NumberExpression(l.intValue() % r.intValue());
							}
					}
					break;
			}
		}
		
		try{
			return (NumberExpression) ex;
		} catch (Exception e){
			System.out.println("Cast error: " + ex);
			throw new RuntimeException(e);
		}
	}
}
