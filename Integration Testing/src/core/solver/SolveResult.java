package core.solver;

import api.expression.IExpression;
import api.models.IVariable;
import api.solver.ISolveResult;
import api.solver.ISolver;

public class SolveResult implements ISolveResult {

	private IVariable[] solution;
	private int code;
	private String message;
	private IExpression returnValue;
	private ISolver solver;
	
	public SolveResult(IVariable[] solution, int code, String message, 
			IExpression returnValue, ISolver solver) {
		this.solution = solution;
		this.code = code;
		this.message = message;
		this.returnValue = returnValue;
		this.solver = solver;
	}
	
	@Override
	public IVariable[] getSolution() {
		return solution;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public IExpression getReturnValue() {
		return returnValue;
	}

	@Override
	public ISolver getSolver() {
		return solver;
	}

	public String toString(){
		return getMessage() + ",return " + returnValue;
	}
}
