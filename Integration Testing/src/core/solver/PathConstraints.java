package core.solver;

import java.util.ArrayList;
import java.util.List;

import api.expression.IArrayIndexExpression;
import api.expression.IExpression;
import api.models.ITestpath;
import api.models.IVariable;
import api.solver.IPathConstraints;

public class PathConstraints implements IPathConstraints {

	private List<IExpression> listLogic;
	private List<IArrayIndexExpression> listArray;
	private IVariable[] params;
	private ITestpath path;
	private IExpression returnExpression;
	private int type;
	
	public PathConstraints(IVariable[] params, ITestpath path) {
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
	public IPathConstraints setReturnExpression(IExpression returnExpression) {
		this.returnExpression = returnExpression;
		return this;
	}

	@Override
	public ITestpath getPath() {
		return path;
	}
	
	@Override
	public void setPath(ITestpath path) {
		this.path = path;
	}

	@Override
	public int getConstraintType() {
		return type;
	}

	@Override
	public IPathConstraints setConstraintType(int type) {
		this.type = type;
		return this;
	}

	@Override
	public IPathConstraints clone() {
		try {
			PathConstraints clone = (PathConstraints) super.clone();
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
