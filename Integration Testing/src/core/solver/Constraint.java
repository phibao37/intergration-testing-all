package core.solver;

import java.util.ArrayList;
import java.util.List;

import api.expression.IArrayIndexExpression;
import api.expression.IExpression;
import api.models.IBasisPath;
import api.models.IVariable;
import api.solver.IConstraint;

public class Constraint implements IConstraint {

	private List<IExpression> listLogic;
	private List<IArrayIndexExpression> listArray;
	private IVariable[] params;
	private IBasisPath path;
	private IExpression returnExpression;
	private int type;
	
	public Constraint(IVariable[] params, IBasisPath path) {
		this.listLogic = new ArrayList<>();
		this.listArray = new ArrayList<>();
		this.params = params;
		this.path = path;
		this.type = TYPE_NORMAL;
	}
	
	@Override
	public List<IExpression> getLogicConstraints() {
		return listLogic;
	}

	@Override
	public void addLogicConstraint(IExpression constraint) {
		listLogic.add(constraint);
	}

	@Override
	public IVariable[] getParameters() {
		return params;
	}

	@Override
	public IExpression getReturnExpression() {
		return returnExpression;
	}

	@Override
	public IConstraint setReturnExpression(IExpression returnExpression) {
		this.returnExpression = returnExpression;
		return this;
	}

	@Override
	public IBasisPath getPath() {
		return path;
	}
	
	@Override
	public void setPath(IBasisPath path) {
		this.path = path;
	}

	@Override
	public int getConstraintType() {
		return type;
	}

	@Override
	public IConstraint setConstraintType(int type) {
		this.type = type;
		return this;
	}

	@Override
	public IConstraint clone() {
		try {
			Constraint clone = (Constraint) super.clone();
			clone.listLogic = new ArrayList<>(listLogic);
			return clone;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return listLogic.toString();
	}

	@Override
	public List<IArrayIndexExpression> getArrayAccess() {
		return listArray;
	}

	@Override
	public void addArrayAccess(IArrayIndexExpression arrayIndex) {
		listArray.add(arrayIndex);
	}

	

}
