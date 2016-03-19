package api.solver;

import java.util.List;

import api.expression.IExpression;
import api.models.IBasisPath;
import api.models.IVariable;

/**
 * Các ràng buộc cần được giải để sinh testcase
 */
public interface IConstraint extends Cloneable{
	
	/**
	 * Lấy danh sách các ràng buộc dạng biểu thức logic (đúng/sai), là các phương trình
	 * ràng buộc cần được giải
	 */
	public List<IExpression> getLogicConstraints();
	
	/**
	 * Thêm ràng buộc vào hệs
	 */
	public void addLogicConstraint(IExpression constraint);
	
	/**
	 * Lấy danh sách các tham số của hàm, là các biến số cần được giải ràng buộc
	 */
	public IVariable[] getParameters();
	
	/**
	 * Lấy biểu thức trả về của hàm ứng với điều kiện ràng buộc
	 */
	public IExpression getReturnExpression();
	
	public IConstraint setReturnExpression(IExpression returnExpression);
	
	public IBasisPath getPath();
	public void setPath(IBasisPath path);
	
	/**
	 * Trả về kiểu của hệ ràng buộc. Một số hệ ràng buộc tương ứng với kết quả lỗi
	 */
	public int getConstraintType();
	
	public IConstraint setConstraintType(int type);
	
	final int
			TYPE_NORMAL = 0,
			TYPE_DIVIDE_ZERO = 1,
			TYPE_OUT_OF_BOUND = 2,
			TYPE_NULL_POINTER = 3;
	
	/**
	 * Tạo bản sao hệ ràng buộc
	 */
	public IConstraint clone();
}
