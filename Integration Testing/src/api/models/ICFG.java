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
	 * Lấy danh sách các đường thi hành (thường tối thiểu) thỏa mãn một cấp độ phủ
	 * cho trước
	 * @param cover cấp độ phủ mã nguồn yêu cầu
	 */
	public List<IBasisPath> getPathsCover(int cover);
	
	public static final int COVER_STATEMENT = 1;
	public static final int COVER_BRANCH = 2;
	public static final int COVER_SUBCONDITION = 3;
}
