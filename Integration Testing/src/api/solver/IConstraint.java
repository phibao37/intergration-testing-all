package api.solver;

import java.util.List;

import api.expression.IExpression;
import api.models.IVariable;

/**
 * Các ràng buộc cần được giải để sinh testcase
 */
public interface IConstraint {
	
	/**
	 * Lấy danh sách các ràng buộc dạng biểu thức logic (đúng/sai), là các phương trình
	 * ràng buộc cần được giải
	 */
	public List<IExpression> getLogicConstraints();
	
	/**
	 * Lấy danh sách các tham số của hàm, là các biến số cần được giải ràng buộc
	 */
	public IVariable[] getParameters();
	
	/**
	 * Lấy biểu thức trả về của hàm ứng với điều kiện ràng buộc
	 */
	public IExpression getReturnExpression();
}
