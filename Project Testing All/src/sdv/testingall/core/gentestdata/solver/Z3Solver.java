/**
 * Implement for Z3 solver
 * @file Z3Solver.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.core.gentestdata.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;

import sdv.testingall.core.expression.IBinaryExpression;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.INameExpression;
import sdv.testingall.core.expression.INumberExpression;
import sdv.testingall.core.expression.IUnaryExpression;
import sdv.testingall.core.gentestdata.IGenTestConfig;
import sdv.testingall.core.gentestdata.symbolicexec.IVariable;
import sdv.testingall.core.type.IType;

/**
 * Solve path constraint using Z3 solver - a external solver from Microsoft.<br/>
 * This library will be loaded from PATH environment variable
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
@SuppressWarnings("nls")
public abstract class Z3Solver extends BaseSolver {

	protected Context	ctx;
	protected Solver	solver;

	protected Map<IVariable, Expr> mapInput;

	/**
	 * Create new Z3 solver to execute solving constraint
	 * 
	 * @param constraint
	 *            path constraint need to solve
	 * @param config
	 *            configuration for test data generation
	 */
	protected Z3Solver(IPathConstraint constraint, IGenTestConfig config)
	{
		super(constraint, config);
		List<IVariable> params = constraint.getInputs();
		mapInput = new HashMap<>();

		// Setup Z3 context
		HashMap<String, String> cfg = new HashMap<>(params.size());
		cfg.put("model", "true");
		ctx = new Context(cfg);
		solver = ctx.mkSolver();
		Params solver_params = ctx.mkParams();
		solver_params.add("timeout", config.getZ3SolveTimeout());
		solver.setParameters(solver_params);

		// Declare variable
		for (IVariable var : params) {
			Expr exp = declareInput(var);
			mapInput.put(var, exp);
		}

		// Add constraint
		for (IExpression cstr : constraint.getConstraints()) {
			solver.add((BoolExpr) convertExpression(cstr));
		}

		// Check result
		switch (solver.check()) {
		case UNSATISFIABLE:
			resultType = RESULT_UNSAT;
		case UNKNOWN:
			return;
		case SATISFIABLE:
			resultType = RESULT_SAT;
		}

		inputData = new ArrayList<>();
		Model model = solver.getModel();

		// Later: model.eval
		// Get result value
		FuncDecl[] funcList = model.getDecls();
		for (IVariable var : params) {
			inputData.add(var);
			FuncDecl func = findFunctionDecl(funcList, var.getName());

			if (func == null) {
				System.out.printf("%s = not solve\n", var.getName());
				continue;
			}

			Expr z3Value = model.getConstInterp(func);
			System.out.printf("%s = %s", var.getName(), z3Value);

			IExpression value = convertZ3Const(z3Value, var.getType());
			System.out.printf(" -> Convert = %s\n", value);
			var.setValue(value);
		}
	}

	/**
	 * Convert from core expression to Z3 expression
	 * 
	 * @param exp
	 *            core expression
	 * @return converted Z3 expression
	 */
	protected Expr convertExpression(IExpression exp)
	{
		if (exp instanceof INameExpression) {
			String name = ((INameExpression) exp).getName();
			Expr expr = findVariable(name);
			assert expr != null;
			return expr;
		}

		else if (exp instanceof INumberExpression) {
			return convertNumber((INumberExpression) exp);
		}

		else if (exp instanceof IBinaryExpression) {
			IBinaryExpression binExp = (IBinaryExpression) exp;
			Expr op1 = convertExpression(binExp.getLeft());
			Expr op2 = convertExpression(binExp.getRight());

			if (binExp.isBitwiseExpression()) {
				op1 = wrap2Bitvec(op1);
				op2 = wrap2Bitvec(op2);
			} else {
				op1 = unwrap2Bitvec(op1);
				op2 = unwrap2Bitvec(op2);
			}

			switch (binExp.getOperator()) {
			case IBinaryExpression.ADD:
				return ctx.mkAdd((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.MINUS:
				return ctx.mkSub((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.MUL:
				return ctx.mkMul((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.DIV:
				return ctx.mkDiv((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.MOD:

				return ctx.mkRem((IntExpr) op1, (IntExpr) op2);
			case IBinaryExpression.LOGIC_AND:
				return ctx.mkAnd((BoolExpr) op1, (BoolExpr) op2);
			case IBinaryExpression.LOGIC_OR:
				return ctx.mkOr((BoolExpr) op1, (BoolExpr) op2);

			case IBinaryExpression.EQUALS:
				return ctx.mkEq(op1, op2);
			case IBinaryExpression.NOT_EQUALS:
				return ctx.mkNot(ctx.mkEq(op1, op2));
			case IBinaryExpression.LESS:
				return ctx.mkLt((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.LESS_EQUALS:
				return ctx.mkLe((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.GREATER:
				return ctx.mkGt((ArithExpr) op1, (ArithExpr) op2);
			case IBinaryExpression.GREATER_EQUALS:
				return ctx.mkGe((ArithExpr) op1, (ArithExpr) op2);

			case IBinaryExpression.BIT_AND:
				return ctx.mkBVAND((BitVecExpr) op1, (BitVecExpr) op2);
			case IBinaryExpression.BIT_OR:
				return ctx.mkBVOR((BitVecExpr) op1, (BitVecExpr) op2);
			case IBinaryExpression.BIT_XOR:
				return ctx.mkBVXOR((BitVecExpr) op1, (BitVecExpr) op2);
			case IBinaryExpression.LEFT_SHIFT:
				return ctx.mkBVSHL((BitVecExpr) op1, (BitVecExpr) op2);
			case IBinaryExpression.RIGHT_SHIFT:
				return ctx.mkBVASHR((BitVecExpr) op1, (BitVecExpr) op2);
			}
		}

		else if (exp instanceof IUnaryExpression) {
			IUnaryExpression unaryExp = (IUnaryExpression) exp;
			Expr op1 = convertExpression(unaryExp.getSubExpression());

			if (unaryExp.isBitwiseExpression()) {
				op1 = wrap2Bitvec(op1);
			} else {
				op1 = unwrap2Bitvec(op1);
			}

			switch (unaryExp.getOperator()) {
			case IUnaryExpression.LOGIC_NOT:
				return ctx.mkNot((BoolExpr) op1);
			case IUnaryExpression.MINUS:
				return ctx.mkUnaryMinus((ArithExpr) op1);
			case IUnaryExpression.PLUS:
				return op1;
			case IUnaryExpression.BIT_NOT:
				return ctx.mkBVNot((BitVecExpr) op1);
			}
		}

		System.out.printf("Unsupport: %s\n", exp.getClass());
		return null;
	}

	/**
	 * Find Z3 variable expression by name
	 * 
	 * @param name
	 *            variable name to find
	 * @return Z3 expression
	 */
	protected Expr findVariable(String name)
	{
		for (IVariable input : getConstraint().getInputs()) {
			if (input.getName().equals(name)) {
				return mapInput.get(input);
			}
		}
		return null;
	}

	/**
	 * Find Z3 function declaration from given list
	 * 
	 * @param list
	 *            Z3 function list
	 * @param name
	 *            function name to find
	 * @return Z3 function
	 */
	protected FuncDecl findFunctionDecl(FuncDecl[] list, String name)
	{
		for (FuncDecl func : list) {
			if (func.getName().toString().equals(name)) {
				return func;
			}
		}
		return null;
	}

	/**
	 * Convert bit-vector expression to integer value if found
	 * 
	 * @param exp
	 *            expression to convert
	 * @return expression that is not a bit-vector
	 */
	protected abstract Expr unwrap2Bitvec(Expr exp);

	/**
	 * Convert expression to bit-vector if needed
	 * 
	 * @param exp
	 *            expression to convert
	 * @return Z3 bit-vector expression
	 */
	protected abstract BitVecExpr wrap2Bitvec(Expr exp);

	/**
	 * Convert from Z3 expression back to core expression
	 * 
	 * @param exp
	 *            Z3 expression
	 * @param type
	 *            target expression type
	 * @return converted core expression
	 */
	protected abstract IExpression convertZ3Const(Expr exp, IType type);

	/**
	 * Add input variable to tell the Z3 solver what need to solve
	 * 
	 * @param var
	 *            input variable
	 * @return Z3 expression
	 */
	protected abstract Expr declareInput(IVariable var);

	/**
	 * Convert constant number value to Z3 expression
	 * 
	 * @param number
	 *            number expression
	 * @return Z3 expression
	 */
	protected abstract Expr convertNumber(INumberExpression number);

}
