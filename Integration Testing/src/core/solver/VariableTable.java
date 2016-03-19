package core.solver;

import java.util.ArrayList;

import api.expression.IArrayIndexExpression;
import api.expression.IExpression;
import api.expression.IExpressionEval;
import api.expression.IExpressionGroup;
import api.expression.IMemberAccessExpression;
import api.expression.INameExpression;
import api.models.IVariable;
import api.parser.IVariableTable;
import core.expression.ExpressionVisitor;

public class VariableTable extends ArrayList<IVariable> implements IVariableTable {
	private static final long serialVersionUID = 1L;

	private int scope = 0;
	
	@Override
	public void increaseScope() {
		scope++;
	}

	@Override
	public void decreaseScope() {
		removeIf(v -> (v.getScope() == scope));
		scope--;
	}

	@Override
	public void addVariable(IVariable var) {
		var.setScope(scope);
		add(var);
	}

	@Override
	public void updateVariable(String name, IExpression value) {
		value = fill(value);
		find(name).setValue(value);
	}

	@Override
	public void updateArrayElement(String name, IExpression indexes, 
			IExpression value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IExpression fill(IExpression expression) {
		IExpressionGroup group = expression.clone().group();
		ArrayList<IExpression> justReplace = new ArrayList<>();
		
		group.accept(new ExpressionVisitor() {

			@Override
			public void leave(INameExpression name) {
				
			}

			@Override
			public void leave(IArrayIndexExpression array) {
				
			}

			@Override
			public void leave(IMemberAccessExpression member) {
				
			}
			
		});
		
		justReplace.forEach(ex -> ex.blockReplace(false));
		return group.ungroup();
	}


	@Override
	public IVariable find(String name) {
		for (int i = size() - 1; i >= 0; i--){
			IVariable v = get(i);
			if (v.getName().equals(name))
				return v;
		}
		return null;
	}

	@Override
	public IExpressionEval getExpressionEval() {
		return null;
	}

}
