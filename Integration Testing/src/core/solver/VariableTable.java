package core.solver;

import java.util.ArrayList;

import api.expression.IArrayIndexExpression;
import api.expression.IBinaryExpression;
import api.expression.IDeclareExpression;
import api.expression.IExpression;
import api.expression.IExpressionEval;
import api.expression.IExpressionGroup;
import api.expression.IMemberAccessExpression;
import api.expression.INameExpression;
import api.models.IType;
import api.models.IVariable;
import api.parser.IVariableTable;
import core.expression.ExpressionVisitor;
import core.models.Variable;

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
	public void updateArrayElement(String name, IExpression[] indexes, 
			IExpression value) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void injectTypeExpression(IExpression ex) {
		if (ex instanceof IDeclareExpression){
			int oldSize = size();
			IDeclareExpression declare = (IDeclareExpression) ex;
			IType type = declare.getType();
			
			for (IExpression dc: declare.getDeclares()){
				IExpression left = dc, right = null;
				IVariable var = null;
				INameExpression ref = null;
				
				//Khai báo được gán giá trị
				if (left instanceof IBinaryExpression){
					IBinaryExpression bin = (IBinaryExpression) left;
					left = bin.getLeft();
					right = bin.getRight();
				}
				
				//Đây là một biến thường
				if (left instanceof INameExpression){
					ref = (INameExpression) left;
					ref.setType(type);
					var = new Variable(ref.getName(), type);
				}
				
				//Đây là một biến mảng
				else if (left instanceof IArrayIndexExpression) {
					//
				}
				
				//Khai báo và khởi tạo giá trị, thực hiện như gán
				if (right != null)
					injectTypeExpression(right);
					
				//Thêm biến vào bảng biến
				addVariable(var);
			}
			
			if (size() > oldSize)
				removeRange(oldSize, size());
		}
		else
			fill(ex, true);
		
	}

	@Override
	public IExpression fill(IExpression expression) {
		return fill(expression, false);
	}

	protected IExpression fill(IExpression expression, boolean injectType){
		IExpressionGroup group = expression.clone().group();
		ArrayList<IExpression> justReplace = new ArrayList<>();
		
		group.accept(new ExpressionVisitor() {

			@Override
			public void leave(INameExpression name) {
				if (name.getRole() != INameExpression.ROLE_NORMAL)
					return;
				
				IVariable find = find(name.getName());
				if (injectType){
					name.setType(find.getType());
					return;
				}
				
				if (find.isValueSet()){
					IExpression value = find.getValue().clone().blockReplace(true);
					justReplace.add(value);
					group.replaceChild(name, value);
				}
				name.notifyValueUsed();
			}

			@Override
			public void leave(IArrayIndexExpression array) {
				//
				array.notifyValueUsed();
			}

			@Override
			public void leave(IMemberAccessExpression member) {
				//
				member.notifyValueUsed();
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
