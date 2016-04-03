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
import api.expression.INumberExpression;
import api.models.IType;
import api.models.IVariable;
import api.solver.IVariableTable;
import core.expression.ExpressionEval;
import core.expression.ExpressionVisitor;
import core.models.ArrayVariable;
import core.models.Variable;
import core.models.type.ArrayType;

public class VariableTable extends ArrayList<IVariable> implements IVariableTable {
	private static final long serialVersionUID = 1L;

	private int scope = 0;
	private ExpressionEval exEval = new ExpressionEval();
	
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
		value = fill(value);
		IExpression[] newIndexes = new IExpression[indexes.length];
		
		for (int i = 0; i < indexes.length; i++)
			newIndexes[i] = eval(indexes[i]);
		
		((ArrayVariable)find(name)).setValueAt(value, newIndexes);
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
					IType arrayType = type;
					IArrayIndexExpression array = (IArrayIndexExpression) left;
					IExpression[] indexes = array.getIndexes();
					
					for (int i = indexes.length - 1; i >= 0; i--){
						int size = 0;
						
						if (indexes[i] instanceof INumberExpression)
							size = ((INumberExpression) indexes[i]).intValue();
						arrayType = new ArrayType(arrayType, size);
					}
					
					array.setType(type);
					var = new ArrayVariable(array.getName(), (ArrayType) arrayType);
				}
				
				//Khai báo và khởi tạo giá trị
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
			public void leave(IArrayIndexExpression arrayEx) {
				ArrayVariable array = (ArrayVariable) find(arrayEx.getName());
				
				if (injectType){
					IType type = array.getType();
					for (int i = arrayEx.getIndexes().length; i > 0; i--)
						type = ((ArrayType)type).getSubType();
					arrayEx.setType(type);
					return;
				}
				
				IExpression[] indexes = arrayEx.getIndexes().clone();
				for (int i = 0; i < indexes.length; i++)
					indexes[i] = eval(indexes[i]);
				
				if (array.isValueSet(indexes)){
					IExpression value = array.getValueAt(indexes)
							.clone().blockReplace(true);
					justReplace.add(value);
					group.replaceChild(arrayEx, value);
				}
				
				arrayEx.notifyValueUsed();
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
		return exEval;
	}

}
