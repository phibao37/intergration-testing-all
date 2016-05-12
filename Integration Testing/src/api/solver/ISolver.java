package api.solver;

public interface ISolver {
	
	/**
	 * Từ một hệ ràng buộc, giải ra kết quả tương ứng
	 */
	ISolution solveConstraint(IPathConstraints constraint) throws InterruptedException;
	
	/**
	 * Trả về tên của bộ giải
	 */
	String getName();
}
