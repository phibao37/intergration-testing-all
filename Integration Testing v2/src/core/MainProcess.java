package core;

import core.error.CoreException;
import core.models.Function;
import core.models.Statement;
import core.models.type.ObjectType;
import core.solver.Solver;
import core.solver.Solver.Result;
import core.unit.BasisPath;
import core.unit.BasisPathParser;
import core.unit.ConstraintEquations;
import core.unit.LoopablePath;
import core.visitor.BodyFunctionVisitor;
import core.visitor.UnitVisitor;
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
	
	private ArrayList<Function> mFunctions = new ArrayList<Function>();
	//private ArrayList<Variable> mVariables = new ArrayList<>();
	private ArrayList<ObjectType> mObjectTypes = new ArrayList<>(); 
	
	private UnitVisitor mUnitVisitor;
	private BodyFunctionVisitor mBodyVisitor;
	
	private BasisPathParser mUnitPathParser = BasisPathParser.DEFAULT;
	
	private boolean mSupportLengthArray;
	
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
						mUnitPathParser.parseBasisPath(path, func);
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
					
					mUnitPathParser.parseBasisPath(basis, current);
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
	public ArrayList<Function> getFunctionList(){
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
	 * Kiểm tra process có hỗ trợ thuộc tính "length" của biến mảng
	 */
	public boolean isSupportLengthArray() {
		return mSupportLengthArray;
	}

	/**
	 * Đặt sự hỗ trợ thuộc tính "length" của biến mảng
	 */
	public void setSupportLengthArray(boolean support) {
		this.mSupportLengthArray = support;
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








