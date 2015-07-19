package jdt;

import java.util.List;

import org.eclipse.jdt.core.dom.ArrayInitializer;

import core.models.Expression;
import core.models.expression.ArrayExpression;

public class Convert {
	
	@SuppressWarnings("unchecked")
	public static Expression parse(org.eclipse.jdt.core.dom.Expression ep){
		if (ep instanceof ArrayInitializer){
			List<org.eclipse.jdt.core.dom.Expression> list = ((ArrayInitializer) ep).expressions();
			Expression[] array = new Expression[list.size()];
			
			for (int i = 0; i < array.length; i++)
				array[i] = parse(list.get(i));
			
			return new ArrayExpression(array);
		}
		
		return null;
	}
	
}
