package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import core.expression.ExpressionVisitor;
import core.expression.FunctionCallExpression;
import core.models.CFG;
import api.IProject;
import api.Value;
import api.models.IFunction;
import api.models.ITestResult;
import api.models.IType;
import api.models.IVariable;

public abstract class AbstractProject implements IProject {
	
	private List<IFunction> listFunction;
	private List<IVariable> listGlobalVar;
	private List<IType> listType;
	private File[] listFile;

	public AbstractProject(File... sources){
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
			func.setCFG(Value.COVER_BRANCH, new CFG(
					getBodyParser().parseBody(func.getBody(), false, this)));
			func.setCFG(Value.COVER_SUBCONDITION, new CFG(
					getBodyParser().parseBody(func.getBody(), true, this)));
			
		}
		
		//Liên kết các lời gọi hàm tới các hàm
		for (IFunction func: listFunction){
			func.accept(new ExpressionVisitor() {

				@Override
				public int visit(FunctionCallExpression call) {
					try {
						IFunction refer = findFunctionByCall(call);
						func.addRefer(refer);
					} catch (FunctionNotFoundException e) {
						System.out.println("Loi goi ham ben ngoai: " + call);
						//Load #include<header> của bộ biên dịch ???
					}
					return PROCESS_CONTINUE;
				}
				
			});
		}
		
	}
	
	

	@Override
	public ITestResult testFunction(IFunction func) {
		
		
		return null;
	}


	/**
	 * Tìm hàm số trong danh sách khai báo khớp với một lời gọi hàm
	 * @param call lời gọi hàm, thí dụ: test(x, y)
	 * @return hàm số tương thích với với lời gọi, thí dụ: void test(int x, int y){..} 
	 * @throws FunctionNotFoundException không tìm được khai báo khớp lời gọi ham
	 */
	protected IFunction findFunctionByCall(FunctionCallExpression call) 
			throws FunctionNotFoundException{
		for (IFunction func: listFunction){
			if (isMatchFunctionCall(func, call))
				return func;
		}
		throw new FunctionNotFoundException();
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
		
		//TODO các biểu thức tham số phải khớp kiểu trong khai báo hàm
		//TODO có nhũng lời gọi bỏ qua tham số cuối cùng đã được định nghĩa
		//System.out.printf("Find %s => Found %s\n", call, func.getName());
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
	
	static class FunctionNotFoundException extends Exception {
		private static final long serialVersionUID = 1L;
	}

}
