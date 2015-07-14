package core.solver;

import java.util.ArrayList;

import core.models.Expression;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.unit.ConstraintEquations;

/**
 * Một bộ giải hệ các ràng buộc để tìm ra nghiệm số thỏa mãn những ràng buộc này
 * @author ducvu
 *
 */
public interface Solver {
	
	/**
	 * Bắt đầu quá trình giải hệ ràng buộc
	 * @param testcases danh sách các biến cần được giải để làm thỏa mãn hệ ràng buộc
	 * @param constraints hệ ràng buộc, là một danh sách các biểu thức chỉ chứa 
	 * các biến testcase, hợp với các phép toán so sánh (==, <, >=,...) hoặc các
	 * phép toán logic (&&, ||, !)
	 * @param array danh sách các biểu thức truy cập vào các biển mảng testcase, cần dùng
	 * để khởi tạo các phần tử mảng tại các vị trí truy cập này. <br/>
	 * Thí dụ với testcase của hàm int min(int a[], int n), 
	 * trong chương trình có truy cập vào a[2], a[n-1]. Giải hệ được n = 5, giả sử biến a
	 * không tham gia vào các điều kiện (chỉ dùng để lấy giá trị), biến mảng a cũng cần
	 * được khởi tạo tại các vị trí 2, 4, tức nó phải là mảng chứa được 4 phần tử
	 * @return kết quả sau khi đã giải xong hệ ràng buộc
	 */
	public Result solve(Variable[] testcases, ConstraintEquations constraints,
			ArrayList<ArrayIndexExpression> array);
	
	
	/**
	 * Kết quả sau khi giải một hệ ràng buộc
	 */
	public static class Result{
		
		private int mCode;
		private String mMessage;
		private Variable[] mSolution;
		private Expression mReturnValue;
		
		/**
		 * Tạo một kết quả sau khi đã giải xong các ràng buộc
		 * @param code xem {@link #getSolutionCode()}
		 * @param message xem {@link #getSolutionMessage()}
		 * @param solution xem {@link #getSolution()}
		 * @param returnValue xem {@link #getReturnValue()}
		 */
		public Result(int code, String message, Variable[] solution, 
				Expression returnValue){
			mCode = code;
			mMessage = message;
			mSolution = solution;
			mReturnValue = returnValue;
		}
		
		/**
		 * Trả về nghiệm tìm được của hệ, hoặc null nếu không có
		 */
		public Variable[] getSolution(){
			return mSolution;
		}
		
		/**
		 * Trả về cờ hiệu xác định quá trình giải hệ có thành công hay không
		 * @return
		 * {@link #SUCCESS}: giải ra nghiệm thành công<br/>
		 * {@link #ERROR}: hệ vô nghiệm<br/>
		 * {@link #UNKNOWN}: không xác định được
		 */
		public int getSolutionCode(){
			return mCode;
		}
		
		/**
		 * Trả về thông báo khi giải hệ. <br/>
		 * Nếu thành công, nên liệt kê ra các nghiệm đã được giải.<br/>
		 * Nếu không, trả về chuỗi thông báo là hệ vô nghiệm hay không xác định được. 
		 * Có thể dẫn thêm nguyên nhân nếu có
		 */
		public String getSolutionMessage(){
			return mMessage;
		}
		
		/**
		 * Trả về biểu thức return hàm đã được rút gọn
		 */
		public Expression getReturnValue(){
			return mReturnValue;
		}
	}
	
	/**
	 * Hệ giải ra nghiệm thành công
	 */
	public static final int SUCCESS = 1;
	
	/**
	 * Hệ này vô nghiệm
	 */
	public static final int ERROR = 0;
	
	/**
	 * Không biết rõ được có nghiệm hay không, nhưng không giải ra được nghiệm
	 */
	public static final int UNKNOWN = -1;
}
