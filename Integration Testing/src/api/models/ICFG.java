package api.models;

import java.util.List;

public interface ICFG {
	
	/**
	 * Trả về danh sách các câu lệnh có trong đồ thị
	 */
	public IStatement[] getStatements();
	
	/**
	 * Lấy danh sách các đường thi hành cơ bản ứng với đồ thị
	 * @return danh sách các đường thi hành tuyến tính độc lập, mỗi đường là một 
	 * đường đi duy nhất qua các câu lệnh để đi hết chương trình
	 */
	public List<IBasisPath> getAllBasisPaths();
	
	/**
	 * Trả về danh sách các đường đi phủ hết được các câu lệnh
	 * @return danh sách tối giản các đường đi, sau khi duyệt hết các đường đi này,
	 * mọi câu lệnh trong đồ thị đều được thăm
	 */
	List<IBasisPath> getCoverStatementPaths();
	
	/**
	 * Trả về danh sách các đường đi phủ hết được các nhánh
	 * @return danh sách tối giản các đường đi, sau khi duyệt hết các đường đi này, 
	 * mọi nhánh trong đồ thị (các cạnh) đều được thăm
	 */
	List<IBasisPath> getCoverBranchPaths();
	
	public static final int COVER_STATEMENT = 1;
	public static final int COVER_BRANCH = 2;
	public static final int COVER_SUBCONDITION = 3;
}
