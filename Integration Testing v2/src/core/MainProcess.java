package core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import core.error.FunctionNotFoundException;
import core.error.StatementNoRootException;
import core.models.Expression;
import core.models.Function;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.FunctionCallExpression;
import core.solver.Solver;
import core.solver.Solver.Result;
import core.solver.Z3Solver;
import core.unit.BasisPath;
import core.unit.BasisPathParser;
import core.unit.CFG;
import core.unit.ConstraintEquations;
import core.visitor.BodyFunctionVisitor;
import core.visitor.ExpressionVisitor;
import core.visitor.UnitVisitor;

/**
 * Các công việc cho việc kiểm thử
 * @author ducvu
 *
 */
public abstract class MainProcess implements FilenameFilter {

	private ArrayList<File> mFiles = new ArrayList<>();
	
	private ArrayList<Function> mFunctions = new ArrayList<>();
	private ArrayList<Variable> mVariables = new ArrayList<>();
	
	private UnitVisitor mUnitVisitor;
	private BodyFunctionVisitor mBodyVisitor;
	
	private BasisPathParser mPathParser = BasisPathParser.DEFAULT;
	private Solver mSolver = Z3Solver.DEFAULT;
	
	/**
	 * Chạy tiến trình nạp chính
	 */
	public void run(){
		//Xóa dữ liệu cũ
		mFunctions.clear();
		mVariables.clear();
		
		//Duyệt các tập tin đang làm việc, thêm các hàm và biến tìm được vào danh sách
		for (File file: mFiles)
			loadFile(file);
		
		//Duyệt qua các thân hàm để tạo các đồ thị CFG
		for (Function func: mFunctions)
			func.parseCFG(mBodyVisitor);
		
		//Duyệt qua các lời gọi hàm để liên kết tới các khai báo tương ứng
		for (Function func: mFunctions){
			func.accept(new ExpressionVisitor() {
				@Override
				public int visit(FunctionCallExpression call) {
					try {
						func.addRefer(findFunctionByCall(call));
					} catch (FunctionNotFoundException e) {
						//e.printStackTrace();
						//Tìm trong các #include<header> của bộ biên dịch ???
					}
					return PROCESS_CONTINUE;
				}
				
			});
		}
	}
	
	/**
	 * Tìm hàm số trong danh sách khai báo khớp với một lời gọi hàm
	 * @param call lời gọi hàm, thí dụ: test(x, y)
	 * @return hàm số tương thích với với lời gọi, thí dụ: void test(int x, int y){..} 
	 * @throws FunctionNotFoundException không tìm được khai báo khớp lời gọi ham
	 */
	protected Function findFunctionByCall(FunctionCallExpression call) 
			throws FunctionNotFoundException{
		for (Function func: mFunctions){
			
			//Lọc ra hàm khớp tên lời gọi
			if (!func.getName().equals(call.getName()))
				continue;
			
			//Lọc ra hàm có số lượng đối số bằng số lượng tham số gọi 
			if (func.getParameters().length != call.getArguments().length)
				continue;
			
			//TODO các biểu thức tham số phải khớp kiểu trong khai báo hàm
			//TODO có nhũng lời gọi bỏ qua tham số cuối cùng đã được định nghĩa
			//System.out.printf("Find %s => Found %s\n", call, func.getName());
			
			return func;
			
		}
		throw new FunctionNotFoundException(call);
	}
	
	/**
	 * Bắt đầu quá trình kiểm thử một hàm (đơn vị)
	 * @param func hàm cần kiểm thử
	 * @return danh sách các đường thi hành đã được gán các ràng buộc và các testcase
	 */
	public ArrayList<BasisPath> beginTestFunction(Function func){
		ArrayList<BasisPath> paths = func.getCFG(true).getBasisPaths();
		
		for (BasisPath path: paths){
			try {
				mPathParser.parseBasisPath(path, func);
			} catch (StatementNoRootException e1) {
				System.out.println(" !!! " + e1.getMessage());
				continue;
			}
			
			ConstraintEquations ce = mPathParser.getConstrains();
			path.setConstraint(ce);
			
			Result result = mSolver.solve(ce.getTestcases(), ce, ce.getArrayAccesses());
			path.setSolveResult(result);
		}
		
		return paths;
	}
	
	public void beginTestFunctionBeta(Function func){
		int feasible = 0;
		
		//CFG cfg_12 = func.getCFG(false);
		CFG cfg_3 = func.getCFG(true);
		
		//cfg_12.getBasisPaths();
		//cfg_3.getBasisPaths();
		
		for (BasisPath path: cfg_3.getBasisPaths()){
			System.out.println("\n" + path);
			
			try {
				mPathParser.parseBasisPath(path, func);
			} catch (StatementNoRootException e1) {
				System.out.println(" !!! " + e1.getMessage());
				continue;
			}
			ConstraintEquations ce = mPathParser.getConstrains();
			
			for (Expression e: ce)
				System.out.println(" ? " + e);
			for (ArrayIndexExpression e: ce.getArrayAccesses())
				System.out.println(" & " + e);
			
			Result result = mSolver.solve(ce.getTestcases(), ce, ce.getArrayAccesses());
			if (result.getSolutionCode() == Solver.SUCCESS){
				for (Variable sol: result.getSolution())
					System.out.println(" ==> " + sol);
				feasible++;
				System.out.println(" RETURN " + result.getReturnValue());
			}
			else
				System.out.println(" ==> " + result.getSolutionMessage());
		}
		System.out.printf("\n *** Feasible: %d, Unsat: %d\n\n", feasible, 
				cfg_3.getBasisPaths().size() - feasible);
	}
	
	/**
	 * Đặt bộ duyệt toàn cục
	 */
	public void setUnitVisitor(UnitVisitor unitVisitor){
		mUnitVisitor = unitVisitor;
	}
	
	/**
	 * Đặt bộ duyệt thân hàm
	 */
	public void setBodyVisitor(BodyFunctionVisitor bodyVisitor){
		mBodyVisitor = bodyVisitor;
	}
	
	/**
	 * Đặt bộ phân tích một đường thi hành để tìm ra hệ ràng buộc
	 */
	public void setBasisPathParser(BasisPathParser parser){
		mPathParser = parser;
	}
	
	/**
	 * Đặt bộ giải hệ ràng buộc. Bộ giải hệ phải được đặt trước khi có yêu cầu giải các
	 * ràng buộc trên các đường thi hành
	 */
	public void setSolver(Solver solver){
		mSolver = solver;
	}
	
	/**
	 * Duyệt qua một tập tin và lấy ra các đối tượng như hàm, biến toàn cục
	 * @param file tập tin mã nguồn
	 * @param args các tham số bổ sung
	 */
	protected void loadFile(File file, Object... args) {
		String source = null;
		try {
			source = Utils.getContentFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mUnitVisitor.parseSource(source, file, args);
		
		mFunctions.addAll(mUnitVisitor.getFunctionList());
		mVariables.addAll(mUnitVisitor.getGlobalVariableList());
	}
	
	/**
	 * Đặt danh sách các tập tin để làm việc<br/>
	 * Hàm lọc sẽ được sử dụng để lọc ra các tập tin chứa mã nguồn
	 * @param files danh sách các tập tin mã nguồn, các thư mục chứa mã nguồn
	 * @param recur tìm cả trong các thư mục con, đệ quy
	 */
	public void setWorkingFiles(File[] files, boolean recur){
		mFiles.clear();
		for (File file: files)
			if (file.isFile()){
				if (accept(file.getParentFile(), file.getName()))
					mFiles.add(file);
			} else {
				if (recur)
					recurAddFile(file);
				else
					for (File f_dir: file.listFiles(this))
						mFiles.add(f_dir);
			}
	}
	
	private void recurAddFile(File dir){
		for (File file: dir.listFiles())
			if (file.isFile()){
				if (accept(file.getParentFile(), file.getName()))
					mFiles.add(file);
			} else 
				recurAddFile(file);
	}

	/**
	 * Điều kiện lọc một danh sách các tập tin để tìm ra các tập tin chữa mã nguồn
	 */
	@Override
	public abstract boolean accept(File dir, String name);
	
	/**
	 * Trả về danh sách các hàm được khai báo
	 */
	public ArrayList<Function> getFunctions(){
		return mFunctions;
	}
	
	/**
	 * Kiểm tra không có hàm số nào được tìm thấy
	 */
	public boolean isEmptyFunction(){
		return mFunctions.isEmpty();
	}
	
	/**
	 * Trả về danh sách các biến toàn cục được khai báo
	 */
	public ArrayList<Variable> getGlobalVariables(){
		return mVariables;
	}
	
}








