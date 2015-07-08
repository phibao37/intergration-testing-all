package core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import core.models.Function;
import core.models.Variable;
import core.visitor.BodyFunctionVisitor;
import core.visitor.UnitVisitor;

/**
 * Các công việc cho việc kiểm thử
 * @author ducvu
 *
 */
public abstract class MainProcess implements FilenameFilter {
	
	private UnitVisitor mUnitVisitor;
	private BodyFunctionVisitor mBodyVisitor;
	
	private ArrayList<Function> mFunctions = new ArrayList<>();
	private ArrayList<Variable> mVariables = new ArrayList<>();
	
	private ArrayList<File> mFiles = new ArrayList<>();
	
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
	
}








