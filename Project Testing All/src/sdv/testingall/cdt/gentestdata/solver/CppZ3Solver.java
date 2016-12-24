/**
 * Z3 solver handle for C/C++ data type
 * @file CppZ3Solver.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.gentestdata.solver;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.RatNum;

import sdv.testingall.cdt.expression.CppNumberExpression;
import sdv.testingall.cdt.expression.ICppNumberExpression;
import sdv.testingall.cdt.type.CppBasicType;
import sdv.testingall.core.expression.IExpression;
import sdv.testingall.core.expression.INumberExpression;
import sdv.testingall.core.gentestdata.solver.IPathConstraint;
import sdv.testingall.core.gentestdata.solver.Z3Solver;
import sdv.testingall.core.gentestdata.symbolicexec.IVariable;
import sdv.testingall.core.type.IType;

/**
 * Z3 solver handle for C/C++ data type
 * 
 * @author VuSD
 *
 * @date 2016-12-23 VuSD created
 */
public class CppZ3Solver extends Z3Solver {

	/**
	 * Create new Z3 solver to execute solving constraint
	 * 
	 * @param constraint
	 *            path constraint need to solve
	 */
	CppZ3Solver(IPathConstraint constraint)
	{
		super(constraint);
	}

	@Override
	protected Expr declareInput(IVariable var)
	{
		IType type = var.getType();
		Expr exp = null;

		if (!(type instanceof CppBasicType)) {
			System.out.printf("Unsupported type: %s\n", type.getClass()); //$NON-NLS-1$
			return exp;
		}
		CppBasicType basicType = (CppBasicType) type;
		String name = var.getName();

		switch (basicType.getType()) {
		case CppBasicType.INT:
		case CppBasicType.CHAR:
			exp = ctx.mkIntConst(name);
			break;
		case CppBasicType.BOOL:
			exp = ctx.mkBoolConst(name);
			break;
		case CppBasicType.FLOAT:
		case CppBasicType.DOUBLE:
			exp = ctx.mkRealConst(name);
			break;
		}

		return exp;
	}

	@Override
	protected Expr convertNumber(INumberExpression number)
	{
		IType type = number.getType();

		if (!(type instanceof CppBasicType)) {
			System.out.printf("Unsupported type: %s\n", type == null ? null : type.getClass()); //$NON-NLS-1$
			return null;
		}
		CppBasicType basicType = (CppBasicType) type;
		ICppNumberExpression cppNumber = (ICppNumberExpression) number;

		switch (basicType.getType()) {
		case CppBasicType.INT:
		case CppBasicType.CHAR:
			return ctx.mkInt(cppNumber.longValue());
		case CppBasicType.BOOL:
			return cppNumber.boolValue() ? ctx.mkTrue() : ctx.mkFalse();
		case CppBasicType.DOUBLE:
			return ctx.mkReal(cppNumber.toString());
		}
		return null;
	}

	@Override
	protected IExpression convertZ3Const(Expr exp, IType type)
	{
		if (!(type instanceof CppBasicType)) {
			System.out.printf("Unsupported type: %s\n", type == null ? null : type.getClass()); //$NON-NLS-1$
			return null;
		}
		CppBasicType basicType = (CppBasicType) type;

		if (exp instanceof BoolExpr) {
			return new CppNumberExpression(exp.isTrue(), basicType);
		} else if (exp instanceof IntNum) {
			// Hoho: exp.getBigInteger() --> long long int
			return new CppNumberExpression(((IntNum) exp).getInt64(), basicType);
		} else if (exp instanceof RatNum) {
			RatNum ratNum = (RatNum) exp;
			// Later: Create fraction constant
			double numer = ratNum.getNumerator().getInt64();
			double denom = ratNum.getDenominator().getInt64();
			return new CppNumberExpression(numer / denom, basicType);
		}
		return null;
	}

}
