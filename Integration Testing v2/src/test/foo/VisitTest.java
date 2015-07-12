package test.foo;

import cdt.visitor.EpUtils;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Type;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.models.type.ArrayType;
import core.models.type.BasicType;

public class VisitTest {
	
	public static Expression parse(String expression){
		return EpUtils.parseNode(EpUtils.getExpression(expression));
	}
	
	public static void main(String[] args){
		Type t = BasicType.INT;
		
		t = new ArrayType(t, 0);
		
		ArrayVariable ar = new ArrayVariable("a", (ArrayType) t);
		System.out.println(ar);
		
		ar.setValueAt(new IDExpression("37"),new NameExpression("vu"));
		//ar.setValueAt(new IDExpression("37"), 1);
		//ar.setValueAt(new IDExpression("38"), 0, 0);
		System.out.println(ar.getValueAt(new NameExpression("vu")));
	}
	
	
}
