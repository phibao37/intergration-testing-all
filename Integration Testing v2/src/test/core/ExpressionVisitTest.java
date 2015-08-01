package test.core;

import static org.junit.Assert.*;

import org.junit.Test;

import cdt.visitor.EpUtils;
import core.models.Expression;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.models.expression.UnaryExpression;
import core.visitor.ExpressionVisitor;

public class ExpressionVisitTest {
	
	public static Expression parse(String expression){
		return EpUtils.parseNode(EpUtils.getExpression(expression));
	}
	
	@Test
	public void testSimpleVisitor() {
		Expression e = parse("2*n+(1*3-m)+xx[4][7]+test(3,a)+test(1,c)"
				+ "+xy[7][4]+(+xm)-(-ym)");
		StringBuilder _id = new StringBuilder();
		StringBuilder _name = new StringBuilder();
		StringBuilder _call = new StringBuilder();
		StringBuilder _arr = new StringBuilder();
		StringBuilder _bin = new StringBuilder();
		StringBuilder _unary = new StringBuilder();
		e.accept(new ExpressionVisitor() {

			@Override
			public int visit(IDExpression id) {
				_id.append(id);
				return PROCESS_CONTINUE;
			}

			@Override
			public int visit(NameExpression name) {
				_name.append(name);
				return PROCESS_CONTINUE;
			}

			@Override
			public int visit(FunctionCallExpression call) {
				_call.append(call);
				return PROCESS_CONTINUE;
			}

			@Override
			public int visit(ArrayIndexExpression array) {
				_arr.append(array);
				return PROCESS_CONTINUE;
			}

			@Override
			public int visit(BinaryExpression bin) {
				_bin.append(bin);
				return PROCESS_CONTINUE;
			}

			@Override
			public int visit(UnaryExpression unary) {
				_unary.append(unary);
				return PROCESS_CONTINUE;
			}
			
		});
		
		assertEquals("213473174", _id.toString());
		assertEquals("nmacxmym", _name.toString());
		assertEquals("test(3,a)test(1,c)", _call.toString());
		assertEquals("xx[4][7]xy[7][4]", _arr.toString());
		assertEquals("(+xm)(-ym)", _unary.toString());
		assertEquals("((((((((2*n)+((1*3)-m))+xx[4][7])+test(3,a))+test(1,c))+xy[7][4])+(+xm))-(-ym))"
				+ "(((((((2*n)+((1*3)-m))+xx[4][7])+test(3,a))+test(1,c))+xy[7][4])+(+xm))"
				+ "((((((2*n)+((1*3)-m))+xx[4][7])+test(3,a))+test(1,c))+xy[7][4])"
				+ "(((((2*n)+((1*3)-m))+xx[4][7])+test(3,a))+test(1,c))"
				+ "((((2*n)+((1*3)-m))+xx[4][7])+test(3,a))"
				+ "(((2*n)+((1*3)-m))+xx[4][7])"
				+ "((2*n)+((1*3)-m))"
				+ "(2*n)"
				+ "((1*3)-m)"
				+ "(1*3)", _bin.toString());
	}

}
