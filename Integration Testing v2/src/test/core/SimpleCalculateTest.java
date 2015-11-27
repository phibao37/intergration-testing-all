package test.core;

import static org.junit.Assert.*;

import org.junit.Test;

import cdt.visitor.EpUtils;
import core.eval.SimpleEval;
import core.models.expression.IDExpression;

public class SimpleCalculateTest {

	public static String parse(String expression){
		return calc(expression).getContent();
	}
	
	public static IDExpression calc(String expression){
		return SimpleEval.calculate(
				new EpUtils(null).parseNode(EpUtils.getExpression(expression)));
	}
	
	@Test
	public void testInteger() {
		assertEquals(parse("1+2"), "3");
		assertEquals(parse("1-2"), "-1");
		assertEquals(parse("1*2"), "2");
		assertEquals(parse("1/2"), "0");
		assertEquals(parse("1%2"), "1");
		assertEquals(parse("1+2 == 3"), "true");
		assertEquals(parse("6-9 != -3"), "false");
		assertEquals(parse("1+1/2 < 3"), "true");
		assertEquals(parse("1+9/6 <= 2"), "true");
		assertEquals(parse("0-11 > 0 - 11"), "false");
		assertEquals(parse("1+3 >= 2+ 2"), "true");
		assertEquals(parse("-(6-9)"), "3");
		assertEquals(parse("- - 4"), "4");
	}
	
	@Test
	public void testBoolean() {
		assertEquals(parse("true"), "true");
		assertEquals(parse("true&&false"), "false");
		assertEquals(parse("true||false"), "true");
		assertEquals(parse("true&&!false"), "true");
		assertEquals(parse("!!false"), "false");
	}
	
	@Test
	public void testIntVsBoolean(){
		assertEquals(parse("1+true"), "2");
		assertEquals(parse("true+false"), "1");
	}
	
	@Test
	public void testFloat(){
		assertEquals(parse("3/2.0"), "1.5");
		assertEquals(parse("1.0/3"), "0.33333334");
		assertEquals(parse("4/5.0 == 0.8"), "true");
	}
	
	@Test
	public void testExtra(){
		assertEquals(calc("!((1+1)<8)").boolValue(), false);
	}
	
}
