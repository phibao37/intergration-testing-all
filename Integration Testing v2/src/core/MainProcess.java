package core;

import core.error.CoreException;
import core.error.FunctionNotFoundException;
import core.inte.*;
import core.models.Expression;
import core.models.Function;
import core.models.Function.TestcaseManager;
import core.models.Statement;
import core.models.Testcase;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.type.ObjectType;
import core.solver.Solver;
import core.solver.Solver.Result;
import core.unit.BasisPath;
import core.unit.BasisPathParser;
import core.unit.ConstraintEquations;
import core.unit.LoopablePath;
import core.visitor.BodyFunctionVisitor;
import core.visitor.ExpressionVisitor;
import core.visitor.UnitVisitor;
import javafx.util.Pair;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Các công việc cho việc kiểm thử
 * @author ducvu
 *
 */
public abstract class MainProcess implements FilenameFilter {

	private ArrayList<File> mFiles = new ArrayList<>();
	
	private FunctionCallGraph mFunctions = new FunctionCallGraph();
	//private ArrayList<Variable> mVariables = new ArrayList<>();
	private ArrayList<ObjectType> mObjectTypes = new ArrayList<>(); 
	
	private UnitVisitor mUnitVisitor;
	private BodyFunctionVisitor mBodyVisitor;
	
	private BasisPathParser mUnitPathParser = BasisPathParser.DEFAULT;
	private IntegrationPathParser mIntePathParser = IntegrationPathParser.DEFAULT;
	private StubSuiteManager mStubMgr = new StubSuiteManager();
	
	/**
	 * Đối tượng chương trình chính đang điểu khiển
	 */
	public static MainProcess instance;
	
	protected MainProcess(){
		instance = this;
	}
	
	/**
	 * Nạp các hàm (và các biến toàn cục, ..) vào tiến trình
	 * @throws IOException 
	 */
	public void loadFunctionFromFiles() throws IOException{
		//Xóa dữ liệu cũ
		mFunctions.clear();
		mObjectTypes.clear();
		//mVariables.clear();
		
		//Duyệt các tập tin đang làm việc, thêm các hàm và biến tìm được 
		//vào danh sách
		for (File file: mFiles)
			loadFile(file);
		
		//Duyệt qua các thân hàm để tạo các đồ thị CFG
		for (Function func: mFunctions)
			func.parseCFG(mBodyVisitor);
		
		//Duyệt qua các lời gọi hàm để liên kết tới các khai báo tương ứng
		for (Function func: mFunctions){
			ExpressionVisitor visitor = new ExpressionVisitor() {
				@Override
				public int visit(FunctionCallExpression call) {
					try {
						Function refer = findFunctionByCall(call);
						
						call.setFunction(refer);
						func.addRefer(refer);
					} catch (FunctionNotFoundException e) {
						//e.printStackTrace();
						//Tìm trong các #include<header> của bộ biên dịch ???
					}
					return PROCESS_CONTINUE;
				}
				
			};
			func.accept(visitor, false);
			func.accept(visitor, true);
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
			if (isMatchFunctionCall(func, call))
				return func;
		}
		throw new FunctionNotFoundException(call);
	}
	
	/**
	 * Kiểm tra một lời gọi hàm có khớp với một hàm số
	 */
	private static boolean isMatchFunctionCall(Function func, 
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
	
	/**
	 * Bắt đầu quá trình kiểm thử một hàm (đơn vị). Các đường thi hành của cả 2 đồ thị
	 * phủ cấp 1-2 và 3 đều được phân tích và giải ra nghiệm tương ứng
	 * @param func hàm cần kiểm thử
	 * @param listener các công việc khi việc kiểm thử đã xong
	 */
	public void beginTestUnit(Function func, Returned listener){

		new RunThread<Void>(listener) {
			
			@Override
			protected Void doTask() throws CoreException {
				GUI.instance.setStatus("Đang phân tích các đường thi hành");
				ArrayList<BasisPath> paths = new ArrayList<>();
				
				paths.addAll(func.getCFG(false).getBasisPaths());
				paths.addAll(func.getCFG(true).getBasisPaths());
				
				int i = 1, length = paths.size();
				
				try {
					for (BasisPath path : paths) {
						mUnitPathParser.parseBasisPath(path, func, 
								mStubMgr.getSelectedStubSuite());
						ConstraintEquations ce = mUnitPathParser.getConstrains();
						path.setConstraint(ce);

						GUI.instance.setStatus("Đang giải hệ %d/%d", i++,
								length);
						Result result = Solver.solveByList(ce);
						path.setSolveResult(result);
					}
				} finally{
					GUI.instance.setStatus(null);
				}
				return null;
			}

		}.start();

	}
	
	/**
	 * Bắt đầu quá trình kiểm thử một đường thi hành có chứa vòng lặp. Các vòng lặp này
	 * sẽ được nhân hệ số lần lặp dựa vào danh sách chỉ số cung cấp, sau đó ghép nối lại
	 * tạo ra một danh sách các đường thi hành lặp. Cuối cùng, ta giải hệ ràng buộc
	 * tại các đường thi hành này để sinh testcase
	 * @param path đường thi hành có chứa vòng lặp
	 * @param indexes danh sách các chỉ số mô tả số lần lặp, duyệt theo thứ tự in-order
	 * (duyệt cha -> duyệt lần lượt các vòng lặp con)
	 * @param listener công việc cần thực hiện khi việc kiểm thử đã xong. Một danh sách
	 * các đường thi hành đã đính kèm nghiệm testcase sẽ được truyền vào
	 */
	public void beginTestLoopPath(LoopablePath path, ArrayList<Integer> indexes, 
			Function current, Return<ArrayList<BasisPath>> listener) {
		
		new RunThread<ArrayList<BasisPath>>(listener) {
			@Override
			protected ArrayList<BasisPath> doTask() throws CoreException {
				ArrayList<ArrayList<Statement>> paths = new ArrayList<>();
				ArrayList<BasisPath> basisPath = new ArrayList<>();
				
				paths.add(new ArrayList<>());
				GUI.instance.setStatus("Đang phân tích các vòng lặp");
				path.joinLoopStatement(paths, indexes.iterator());
				int i = 1, length = paths.size();
				
				try{
				for (ArrayList<Statement> path: paths){
					BasisPath basis = new BasisPath();
					
					basis.addAll(path);
					basisPath.add(basis);
					
					mUnitPathParser.parseBasisPath(basis, current, 
							mStubMgr.getSelectedStubSuite());
					ConstraintEquations ce = mUnitPathParser.getConstrains();
					basis.setConstraint(ce);

					GUI.instance.setStatus("Đang giải hệ %d/%d", i++, length);
					Result result = Solver.solveByList(ce);
					basis.setSolveResult(result);
				}
				} finally {
					GUI.instance.setStatus(null);
				}
				
				return basisPath;
			}
		}.start();
	}
	
	/**
	 * Bắt đầu quá trình kiểm thử một cặp hàm gọi hàm. Đầu tiên, phân tích các đường đi
	 * trong tập phủ câu lệnh của hàm gọi, lọc lấy các đường đi có gọi đến hàm được gọi.
	 * <br/> Sau đó phân tích trên các đường đi này để tìm các ràng buộc sao cho đi
	 * đúng đường đi này và tương tác được với hàm được gọi. Cuối cùng, ta giải hệ
	 * để tạo ra testcase cho hàm gọi
	 * @param pair cặp hàm gọi hàm
	 * @param paths danh sách các đường thi hành dùng để kiểm thử việc gọi hàm
	 */
	public void beginTestFunctionPair(FunctionPair pair, 
			Return<ArrayList<BasisPath>> paths){
		
		new RunThread<ArrayList<BasisPath>>(paths) {
			
			private boolean foundCall;
			
			@Override
			protected ArrayList<BasisPath> doTask() throws CoreException {
				Function caller = pair.getCaller();
				Function calling = pair.getCalling();
				StubSuite stub = mStubMgr.getSelectedStubSuite();
				
				//Lấy danh sách các đường đi phủ nhánh trong hàm gọi
				ArrayList<BasisPath> paths = caller.getCFG(false).getCoverBranchPaths();
				ExpressionVisitor visitor = new ExpressionVisitor() {
					@Override
					public int visit(FunctionCallExpression call) {
						if (isMatchFunctionCall(calling, call)){
							foundCall = true;
							return PROCESS_ABORT;
						}
						return PROCESS_CONTINUE;
					}
					
				};
				
				GUI.instance.setStatus("Tìm các đường đi chứa lời gọi");
				for (int i = paths.size() - 1; i >= 0; i--){
					foundCall = false;
					paths.get(i).accept(visitor);
					
					if (!foundCall)
						paths.remove(i);
				}
				
				int i = 1, length = paths.size();
				
				try {
				for (BasisPath basis: paths){
					basis.setAnalyzic(null);
					GUI.instance.setStatus("Đang phân tích %d/%d", i++, length);
					mIntePathParser.setCalling(calling);
					
					TestcaseManager tm = calling.getTestcaseManager();
					int j = 0;
					
					//Chưa có testcase nào, tự động tạo một bộ đơn giản
					if (tm.size() == 0){
						for (BasisPath path: calling.getCFG(false).getBasisPaths()){
							BasisPathParser parser = BasisPathParser.DEFAULT;
							
							try{
								parser.parseBasisPath(path, calling, stub);
							} catch (CoreException e){
								GUI.instance.setStatus(1, e.getMessage());
								continue;
							}
							
							for (Solver solver: S.SOLVE_LIST)
								try {
									Result r = solver.solve(parser.getConstrains());
									if (r.getSolutionCode() == Result.SUCCESS)
										tm.add(new Testcase(r));
								} catch (CoreException e) {
									GUI.instance.setStatus(1, e.getMessage());
								}
						}
					}
					
					System.out.println("Number of testcase: " + tm.size());
					for (; j < tm.size(); j++){
						System.out.printf("Testcase %s\n", Utils.merge(", ", 
								tm.get(j).getInputs()));
						
						mIntePathParser.setCallingTestcase(tm.get(j));
						mIntePathParser.parseBasisPath(basis, caller, stub);
						
						ConstraintEquations ce = mIntePathParser.getConstrains();
						Result result = Solver.solveByList(ce);
						
						if (result.getSolutionCode() == Result.SUCCESS){
							basis.setConstraint(ce);
							basis.setSolveResult(result);
							break;
						}
					}
					
					if (j == tm.size()){
						basis.setSolveResult(new Result(
								Result.ERROR, caller.getRefers().size() > 0 ? 
									"No testcase match" : "No testcase needed"));
					}
				}
				} finally {
					GUI.instance.setStatus(null);
				}
				
				return paths;
			}
		}.start();
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
	 * Đặt bộ phân tích một đường thi hành để tìm ra hệ ràng buộc trên một 
	 * đơn vị kiểm thử
	 */
//	public void setUnitPathParser(BasisPathParser parser){
//		mUnitPathParser = parser;
//	}
	
	/**
	 * Duyệt qua một tập tin và lấy ra các đối tượng như hàm, biến toàn cục
	 * @param file tập tin mã nguồn
	 * @throws IOException 
	 */
	protected void loadFile(File file) throws IOException {
		mUnitVisitor.parseSource(file);
		mFunctions.addAll(mUnitVisitor.getFunctionList());
		//mVariables.addAll(mUnitVisitor.getGlobalVariableList());
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
					Collections.addAll(mFiles, file.listFiles(this));
			}
	}
	
	private void recurAddFile(File dir){
		File[] files = dir.listFiles();
		assert files != null;

		for (File file: files)
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
	 * Trả về danh sách các hàm được khai báo được lưu trong cấu trúc gọi hàm
	 */
	public FunctionCallGraph getFunctionCallGraph(){
		return mFunctions;
	}
	
	/**
	 * Kiểm tra không có hàm số nào được tìm thấy
	 */
	public boolean isEmptyFunction(){
		return mFunctions.isEmpty();
	}
	
	/**
	 * Trả về danh sách các cấu trúc (struct/class) được định nghĩa trong chương trình
	 */
	public ArrayList<ObjectType> getDeclaredTypes(){
		return mObjectTypes;
	}
	
	/**
	 * Thêm một kiểu cấu trúc được định nghĩa
	 * @param type kiểu cấu trúc
	 * @return đối tượng hiện thời
	 */
	public MainProcess addDeclaredType(ObjectType type){
		mObjectTypes.add(type);
		return this;
	}
	
	/**
	 * Trả về danh sách các biến toàn cục được khai báo
	 */
//	public ArrayList<Variable> getGlobalVariables(){
//		return mVariables;
//	}
	
	/**
	 * Trả về bộ quản lý stub cho các hàm số
	 */
	public StubSuiteManager getStubManager(){
		return mStubMgr;
	}
	
	/**
	 * Yêu cầu tạo một bộ stub mới
	 * @param strMap danh sách các cặp hàm số-chuỗi biểu thức stub
	 */
	public void requestNewStubSuite(ArrayList<Pair<Function, String>> strMap) 
			throws Exception {
		StubSuite s = new StubSuite();
		for (Pair<Function, String> p: strMap){
			Function f = p.getKey();
			Expression e = IDExpression.parse(p.getValue(), f.getReturnType());
			s.put(f, e);
		}
		mStubMgr.add(s);
		GUI.instance.setStatus(2, "Đã thêm bộ stub \"%s\" thành công", s.getName());
	}
	
	/**
	 * Thread dùng để chạy các công việc cần xử lý
	 * @param <R> listener cho kết quả
	 */
	protected static abstract class RunThread<R> extends Thread{
		
		protected Return<R> mCallBack;
		
		/**
		 * Tạo một thread cùng với listener kết quả, có thể null nếu không muốn nhận
		 */
		public RunThread(Return<R> listener){
			mCallBack = listener;
		}
		
		@Override
		public final void run() {
			R result = null;
			CoreException core = null;
			
			try {
				result = doTask();
			} catch (CoreException e) {
				core = e;
			} catch (Throwable e){
				while (e.getCause() != null)
					e = e.getCause();
				String msg = e.getMessage();
				if (msg == null || msg.isEmpty())
					msg = e.getClass().getSimpleName();
				
				core = new CoreException(msg, e);
			}
			
			if (mCallBack != null){
				if (core == null)
					mCallBack.receive(result);
				else
					mCallBack.error(core);
			}
		}

		/**
		 * Các công việc xử lý chính diễn ra ở đây
		 * @return kết quả sau khi công việc đã hoàn thành
		 * @throws CoreException có thể có lỗi trong quá trình xử lý
		 */
		protected abstract R doTask() throws CoreException;
		
	}
	
	/**
	 * Mô tả một listener dùng để truyền vào các thread để lấy kết quả
	 * @param <R> kiểu kết quả mong đợi
	 */
	public interface Return<R>{
		
		/**
		 * Phương thức sẽ được gọi khi tiến trình hoàn thành công việc
		 * @param result kết quả của công việc
		 */
		void receive(R result);
		
		/**
		 * Phương thức sẽ được gọi khi tiến trình có ngoại lệ xảy ra
		 * @param e ngoại lệ trong việc xử lý
		 */
		default void error(CoreException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Mô tả một listener dùng để truyền vào các thread.<br/>
	 * Hàm {@link #receive()} sẽ được gọi sau khi thread đã xong công việc
	 */
	public static abstract class Returned implements Return<Void>{

		@Override
		public final void receive(Void result) {
			receive();
		}
		
		/**
		 * Công việc cần thực hiện sau khi thread đã hoàn thành công việc
		 */
		public abstract void receive();
	}
	
}








