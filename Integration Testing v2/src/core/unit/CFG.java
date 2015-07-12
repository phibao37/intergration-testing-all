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
	 * Phân tích danh sách các đường thi hành
	 * @param statementList danh sách các câu lệnh đã được liên kết
	 * @return danh sách các đường thi hành tuyến tính độc lập, mỗi đường là một đường đi
	 * duy nhất qua các câu lệnh để đi hết chương trình
	 */
	protected ArrayList<BasisPath> parseBasisPaths(Statement[] statementList){
		mPaths = new ArrayList<BasisPath>();
		travel(new BasisPath(), statementList[0]);
		return mPaths;
	}
	
	/**
	 * Duyệt qua một câu lệnh để tìm ra các đường đi..
	 * @param path đường đi đang xây dựng
	 * @param current câu lệnh cần được thêm vào đường đi
	 * @refer Anh DucAnh
	 */
	private void travel(BasisPath path, Statement current){
		if (current == null)
			mPaths.add(path.clone());
		else if (checkStatement(path, current)){
			path.add(current);

			travel(path, current.getFalse());
			if (current.isCondition())
				travel(path, current.getTrue());
			
			path.remove(path.size()-1);
		}
	}
	
	/**
	 * Kiểm tra một câu lệnh trong đường thi hành không xuất hiện quá 1 lần 
	 * @refer Anh DucAnh
	 */
	private boolean checkStatement(BasisPath path, Statement current){
		int dem = 0;
		
		for (Statement stm: path)
			if (stm == current)
				dem++;
		return dem <= 1;
	}
}




