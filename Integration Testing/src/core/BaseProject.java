package core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.expression.FunctionCallExpression;
import core.models.CFG;
import core.solver.BaseConstraintParser;
import core.solver.TestResult;
import core.solver.z3.Z3Solver;
import api.IProject;
import api.models.IBasisPath;
import api.models.ICFG;
import api.models.IFunction;
import api.models.IStatement;
import api.models.ITestResult;
import api.models.IType;
import api.models.IVariable;
import api.parser.ConstraintParser;
import api.solver.IConstraint;
import api.solver.ISolveResult;
import api.solver.ISolver;

public abstract class BaseProject implements IProject {
	
	private List<IFunction> listFunction;
	private List<IVariable> listGlobalVar;
	private List<IType> listType;
	private File[] listFile;

	public BaseProject(File... sources){
		listFunction = new ArrayList<>();
		listGlobalVar = new ArrayList<>();
		listType = new ArrayList<>();
		listFile = sources;
	}
	

	@Override
	public void loadProject() {
		
		//Lọc các hàm từ các tập tin mã nguồn
		for (File source: listFile)
			getUnitParser().parseUnit(source, this);
		
		//Phân tích CFG cho các hàm
		for (IFunction func: listFunction){
			func.setCFG(ICFG.COVER_BRANCH, new CFG(
					getBodyParser().parseBody(func.getBody(), false, this)));
			func.setCFG(ICFG.COVER_SUBCONDITION, new CFG(
					getBodyParser().parseBody(func.getBody(), true, this)));
			
		}
		
		//Liên kết các lời gọi hàm tới các hàm
		
	}

	@Override
	public ITestResult testFunction(IFunction func) {
		ICFG cfg_12 = func.getCFG(ICFG.COVER_BRANCH),
			cfg_3 = func.getCFG(ICFG.COVER_SUBCONDITION);
		List<IBasisPath> allPath_12 = cfg_12.getAllBasisPaths(),
				coverPath_3 = cfg_3.getCoverBranchPaths(),
				errorPath = new ArrayList<>();
		IVariable[] params = func.getParameters();
		
		//Reset visit state
		for (IStatement stm: cfg_12.getStatements())
			stm.setVisit(false);
		for (IStatement stm: cfg_3.getStatements())
			stm.setVisit(false);
		
		for (IBasisPath path: allPath_12){
			List<IConstraint> cnts = getConstraintParser()
					.parseBasisPath(path, params, ConstraintParser.PARSE_ERROR_PATH);
			
			for (IConstraint cnt: cnts){
				System.out.println("\nHe rang buoc: " + cnt);
				ISolveResult r = getSolver().solveConstraint(cnt);
				cnt.getPath().setSolveResult(r);
				System.out.println("=> Ket qua: " + r);
				
				if (cnt.getConstraintType() != IConstraint.TYPE_NORMAL)
					errorPath.add(cnt.getPath());
			}
		}
		
		for (IBasisPath path: coverPath_3){
			IConstraint cnt = getConstraintParser()
					.parseBasisPath(path, params, ConstraintParser.DEFAULT).get(0);
			ISolveResult r = getSolver().solveConstraint(cnt);
			path.setSolveResult(r);
		}
		
		Map<Integer, List<IBasisPath>> result = new HashMap<>();
		result.put(ITestResult.STATEMENT, cfg_12.getCoverStatementPaths());
		result.put(ITestResult.BRANCH, cfg_12.getCoverBranchPaths());
		result.put(ITestResult.SUBCONDITION, coverPath_3);
		result.put(ITestResult.ALLPATH, allPath_12);
		result.put(ITestResult.ERROR, errorPath);
		return new TestResult(result);
	}


	/**
	 * Tìm hàm số trong danh sách khai báo khớp với một lời gọi hàm
	 * @param call lời gọi hàm, thí dụ: test(x, y)
	 * @return hàm số tương thích với với lời gọi
	 * @throws RuntimeException không tìm được khai báo khớp lời gọi ham
	 */
	protected IFunction findFunctionByCall(FunctionCallExpression call) 
			throws RuntimeException{
		for (IFunction func: listFunction){
			if (isMatchFunctionCall(func, call))
				return func;
		}
		throw new RuntimeException("Function not found");
	}
	
	/**
	 * Kiểm tra một lời gọi hàm có khớp với một hàm số
	 */
	private static boolean isMatchFunctionCall(IFunction func, 
			FunctionCallExpression call){
		//Lọc ra hàm khớp tên lời gọi
		if (!func.getName().equals(call.getName()))
			return false;
		
		//Lọc ra hàm có số lượng đối số bằng số lượng tham số gọi 
		if (func.getParameters().length != call.getArguments().length)
			return false;
		
		return true;
	}

	@Override
	public List<IFunction> getFunctions() {
		return listFunction;
	}

	@Override
	public void addFunction(IFunction function) {
		listFunction.add(function);
	}
	
	@Override
	public List<IVariable> getGlobalVars() {
		return listGlobalVar;
	}

	@Override
	public void addGlobalVar(IVariable global) {
		listGlobalVar.add(global);
	}
	
	@Override
	public List<IType> getLoadedType() {
		return listType;
	}

	@Override
	public void addLoadedType(IType type) {
		listType.add(type);
	}
	
	@Override
	public ConstraintParser getConstraintParser() {
		return new BaseConstraintParser();
	}


	@Override
	public ISolver getSolver() {
		return new Z3Solver();
	}

}
