package api.solver;

public interface ISolver {
	
	/**
	 * Từ một hệ ràng buộc, giải ra kết quả tương ứng
	 */
	ISolveResult solveConstraint(IConstraint constraint);
	
	/**
	 * Trả về tên của bộ giải
	 */
	String getName();
}
