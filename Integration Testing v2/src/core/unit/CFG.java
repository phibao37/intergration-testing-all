package core.unit;

import java.util.ArrayList;

import core.models.Statement;

/**
 * Mô tả một đồ thị dòng điều khiển, nó là một danh sách các câu lệnh ({@link Statement})
 * có liên kết với nhau tạo nên dòng đường đi của một chương trình
 * @author ducvu
 *
 */
public class CFG{
	
	private Statement[] mList;
	private ArrayList<BasisPath> mPaths;
	
	/**
	 * Khởi tạo một đồ thị từ câu lệnh gốc của chương trình
	 * @param statementList danh sách các câu lệnh đã được liên kết
	 * bởi chương trình
	 */
	public CFG(Statement[] statementList){
		mList = statementList;
	}
	
	/**
	 * Lấy danh sách các đường thi hành cơ bản ứng với đồ thị
	 * @return danh sách các đường thi hành tuyến tính độc lập, mỗi đường là một đường đi
	 * duy nhất qua các câu lệnh để đi hết chương trình
	 */
	public ArrayList<BasisPath> getBasisPaths(){
		if (mPaths == null)
			mPaths = parseBasisPaths(mList);
		return mPaths;
	}
	
	/**
	 * Trả về danh sách các câu lệnh trong đồ thị
	 */
	public Statement[] getStatements(){
		return mList;
	}
	
	/**
	 * Phân tích danh sách các đường thi hành dựa vào câu lệnh gốc
	 * @param statementList danh sách các câu lệnh đã được liên kết
	 * @return danh sách các đường thi hành tuyến tính độc lập, mỗi đường là một đường đi
	 * duy nhất qua các câu lệnh để đi hết chương trình
	 */
	protected ArrayList<BasisPath> parseBasisPaths(Statement[] statementList){
		//TODO xay dung danh sach duong thi hanh
		return null;
	}
}




