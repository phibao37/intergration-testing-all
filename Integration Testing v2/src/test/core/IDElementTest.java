package test.core;

import static org.junit.Assert.*;

import org.junit.Test;

import core.models.expression.IDExpression;
import core.models.type.BasicType;

public class IDElementTest {
	
	static Class<?> UNDER_TEST = IDExpression.class;
	
	@Test
	public void testGetTypeInt() {
		assertEquals(new IDExpression("123").getType(), BasicType.INT);
		assertNotEquals(new IDExpression(" 123").getType(), BasicType.INT);
	}
	
	@Test
	public void testGetTypeFloat() {
		assertEquals(new IDExpression("123f").getType(), BasicType.FLOAT);
		assertEquals(new IDExpression("123.0f").getType(), BasicType.FLOAT);
		assertEquals(new IDExpression("0.0f").getType(), BasicType.FLOAT);
		
		assertNotEquals(new IDExpression("123.0").getType(), BasicType.FLOAT);
		assertNotEquals(new IDExpression("0.f").getType(), BasicType.FLOAT);
	}
	
	@Test
	public void testGetTypeDouble() {
		assertEquals(new IDExpression("123d").getType(), BasicType.DOUBLE);
		assertEquals(new IDExpression("123.0").getType(), BasicType.DOUBLE);
		assertEquals(new IDExpression("123.0d").getType(), BasicType.DOUBLE);
		assertEquals(new IDExpression(".12").getType(), BasicType.DOUBLE);
		assertEquals(new IDExpression(".12d").getType(), BasicType.DOUBLE);
		
		assertNotEquals(new IDExpression("123.").getType(), BasicType.DOUBLE);
	}
	
	@Test
	public void testGetTypeChar() {
		/*assertEquals(new IDElement("'a'").getType(), BasicType.CHAR);
		assertEquals(new IDElement("'$'").getType(), BasicType.CHAR);
		assertEquals(new IDElement("'a'").getType(), BasicType.CHAR);
		assertEquals(new IDElement("'a'").getType(), BasicType.CHAR);char c  = '$';
		
		assertNotEquals(new IDElement("123.").getType(), BasicType.CHAR);*/
	}
	
	@Test
	public void testGetTypeBool() {
		assertEquals(new IDExpression("true").getType(), BasicType.BOOL);
		assertEquals(new IDExpression("false").getType(), BasicType.BOOL);
		assertNotEquals(new IDExpression("TRUE").getType(), BasicType.BOOL);
		assertNotEquals(new IDExpression("FALSE").getType(), BasicType.BOOL);
	}
}
