package core.models;

import java.util.ArrayList;
import java.util.HashSet;
import api.models.IBasisPath;
import api.models.ICFG;
import api.models.IStatement;

/**
 * Mô tả một đồ thị dòng điều khiển, nó là một danh sách các câu lệnh 
 * ({@link IStatement}) có liên kết với nhau tạo nên dòng đường đi của một chương trình
 * @author ducvu
 *
 */
public class CFG implements ICFG{
	
	private IStatement[] mList;
	private ArrayList<IBasisPath> mPaths;
	
	/**
	 * Khởi tạo một đồ thị từ danh sách các câu lệnh
	 * @param statementList danh sách các câu lệnh đã được liên kết
	 * bởi chương trình
	 */
	public CFG(IStatement[] statementList){
		mList = statementList;
	}
	
	/**
	 * Lấy danh sách các đường thi hành cơ bản ứng với đồ thị
	 * @return danh sách các đường thi hành tuyến tính độc lập, mỗi đường là một 
	 * đường đi duy nhất qua các câu lệnh để đi hết chương trình
	 */
	public ArrayList<IBasisPath> getAllBasisPaths(){
		if (mPaths == null)
			mPaths = parseBasisPaths(mList);
		return mPaths;
	}
	
	/**
	 * Trả về danh sách các câu lệnh trong đồ thị
	 */
	public IStatement[] getStatements(){
		return mList;
	}
	
	@Override
	public ArrayList<IBasisPath> getCoverStatementPaths(){
		
		//Tạo bản sao danh sách các đường thi hành
		ArrayList<IBasisPath> copy = new ArrayList<>(getAllBasisPaths());
		
		//Duyệt lần lượt các đường thi hành
		for (int i = copy.size() - 1; i >= 0; i--){
			
			//Đánh dấu tất cả các câu lệnh là chưa được thăm
			for (IStatement stm: mList)
				stm.setVisit(false);
			
			//Duyệt lần lượt các đường thi hành còn lại, đánh dấu các câu lệnh trên
			//các đường thi hành này là đã được thăm
			for (IBasisPath path: copy){
				if (path == copy.get(i)) continue;
				for (IStatement stm: path)
					stm.setVisit(true);
			}
			
			int sum = 0;
			for (IStatement stm: mList)
				if (stm.isVisited())
					sum++;
			
			//Nếu tổng các câu lệnh được thăm bằng đúng tổng tất cả câu lệnh, việc 
			//bỏ đi đường thi hành đang xét vẫn làm thỏa mản cấp độ phủ câu lệnh
			if (sum == mList.length)
				copy.remove(i);
		}
		
		//Trả về bản sao này, một số đường thi hành "thừa" đã được loại bỏ 
		return copy;
	}
	
	@Override
	public ArrayList<IBasisPath> getCoverBranchPaths(){
		//Tạo bản sao danh sách các đường thi hành
		ArrayList<IBasisPath> copy = new ArrayList<>(getAllBasisPaths());
		
		//Tập các cạnh trong đồ thị
		HashSet<Edge> edgeList = new HashSet<>();
		
		//Duyệt các cạnh trong đồ thị để đếm tổng
		for (IStatement stm: mList){
			if (stm.getTrue() == null) continue;
			
			edgeList.add(new Edge(stm, stm.getTrue()));
			if (stm.isCondition())
				edgeList.add(new Edge(stm, stm.getFalse()));
		}
		int edgeCount = edgeList.size(); //Tổng số lượng các cạnh trong đồ thị
		
		// Duyệt lần lượt các đường thi hành
		for (int i = copy.size() - 1; i >= 0; i--) {

			//Làm rỗng danh sách cạnh
			edgeList.clear();

			//Duyệt lần lượt các đường thi hành còn lại, thêm các cạnh trên các
			//đường này vào danh sách
			for (IBasisPath path : copy) {
				if (path == copy.get(i))
					continue;
				for (int j = 0; j < path.size() - 1; j++)
					edgeList.add(new Edge(path.get(j), path.get(j+1)));
			}

			// Nếu tổng các cạnh được thăm bằng đúng tổng tất cả cạnh,
			// việc bỏ đi đường thi hành đang xét vẫn làm thỏa mản cấp độ phủ nhánh
			if (edgeList.size() == edgeCount)
				copy.remove(i);
		}
		
		return copy;
	}
	
	
	/**
	 * Phân tích danh sách các đường thi hành
	 * @param statementList danh sách các câu lệnh đã được liên kết
	 * @return danh sách các đường thi hành tuyến tính độc lập, mỗi đường 
	 * là một đường đi duy nhất qua các câu lệnh để đi hết chương trình
	 */
	protected ArrayList<IBasisPath> parseBasisPaths(IStatement[] statementList){
		mPaths = new ArrayList<>();
		travel(new BasisPath(), statementList[0]);
		return mPaths;
	}
	
	/**
	 * Duyệt qua một câu lệnh để tìm ra các đường đi..
	 * @param path đường đi đang xây dựng
	 * @param current câu lệnh cần được thêm vào đường đi
	 * @refer Anh DucAnh
	 */
	private void travel(IBasisPath path, IStatement current){
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
	private boolean checkStatement(IBasisPath path, IStatement current){
		int dem = 0;
		
		for (IStatement stm: path)
			if (stm == current)
				dem++;
		return dem <= 1;
	}
	
	/**
	 * Mô tả một cạnh có hướng trong đồ thị, là đường đi có hướng từ một câu lệnh đến
	 * một câu lệnh khác
	 */
	private static class Edge{
		private IStatement mStart, mEnd;
		
		/**
		 * Tạo một cạnh mới
		 * @param start câu lệnh ở đầu cạnh
		 * @param end câu lệnh cuối cạnh
		 */
		public Edge(IStatement start, IStatement end) {
			mStart = start;
			mEnd = end;
		}
		
		@Override
		public int hashCode() {
			//Bỏ qua tạo mã hash, kiểm tra qua equals sau
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || obj.getClass() != this.getClass())
				return false;
			
			Edge other = (Edge) obj;
			return other.mStart == mStart && other.mEnd == mEnd;
		}
		
	}
}




