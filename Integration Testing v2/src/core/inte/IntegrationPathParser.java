package core.inte;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import core.error.CoreException;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Function;
import core.models.Testcase;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.BinaryExpression;
import core.models.expression.FunctionCallExpression;
import core.models.expression.IDExpression;
import core.models.expression.NameExpression;
import core.unit.BasisPathParser;

/**
 * Xử lý các câu lệnh gọi hàm trong các đường đi để phục vụ cho việc tích hợp
 */
public class IntegrationPathParser extends BasisPathParser {
	
	private Function mCalling;
	private Testcase mCallingTestcase;
	private ArrayList<Expression> mBaseConstraint;
	
	/**
	 * Thiết đặt hàm số con được xét là đang được xét kiểm thử
	 */
	public void setCalling(Function calling){
		mCalling = calling;
	}
	
	/**
	 * Chọn testcase trong danh sách các testcase của hàm con sẽ được chọn
	 * để phân tích 
	 */
	public void setCallingTestcase(Testcase testcase){
		mCallingTestcase = testcase;
	}

	/**
	 * Trả về danh sách các ràng buộc cơ bản được liên kết với lời gọi hàm
	 * <pre>
	 * int test1(int x, int y, int z) {}
	 * int test2(int a){
	 * if (a < 0 || a > 10)
	 *  test1(a+2, 3, a);
	 * }
	 * </pre>
	 * Các ràng buộc được lọc ra là: 
	 * <code>
	 * y == 3,
	 * z < 0 || z > 10
	 * </code>
	 * , chưa có thuật toán để suy diễn a < 0 || a > 10 => x < 2 || x > 12
	 */
	public ArrayList<Expression> getBaseCallConstraint(){
		mBaseConstraint = new ArrayList<>();
		//
		return mBaseConstraint;
	}
	
	/**
	 * Thêm các ràng buộc cần thiết để một tham số trong biểu thức gọi hàm khớp với
	 * một biến đầu vào cho 1 unit tương ứng
	 * @param arg tham số trong lời gọi hàm
	 * @param input biến đầu vào cho hàm ứng với lời gọi
	 */
	protected void addConstraintArgument(Expression arg, Variable input){
		
		//Kiểu mảng, cần so sánh từng phần tử
		if (input instanceof ArrayVariable){
			ArrayVariable array1 = (ArrayVariable) input;
			LinkedHashMap<int[], Expression> indexs = 
					array1.getAllValue(); 
			
			/*
			 * tham số khi truyền vào một mảng cần là 1 tên duy nhất
			 * void test(int a[]){...}, gọi qua {int m[]; test(m);}
			 * Chưa hỗ trợ: {int b[][];test(b[0])}, {test(m+2);} 
			 */
			ArrayVariable array2 = (ArrayVariable) 
					tables.find(arg.getContent());
			
			for (Entry<int[], Expression> entry: indexs.entrySet()){
				int[] key = entry.getKey();
				
				//Cả 2 mảng đều có phần tử tại vị trị này
				if (array2.isValueSet(key)){
					addConstraint(new BinaryExpression(
							entry.getValue(), 
							BinaryExpression.EQUALS, 
							array2.getValueAt(key)
					));
				}
				
				//Đây là biến testcase, tạo thêm truy cập mảng mới
				else if (array2.getScope() == 1){
					ArrayIndexExpression arr = new ArrayIndexExpression(
							new NameExpression(array2.getName()), key);
					
					addArrayAccess(arr);
					addConstraint(new BinaryExpression(
							entry.getValue(), 
							BinaryExpression.EQUALS, 
							arr
					));
				}
				
				//Cho hệ ràng buộc này vô nghiệm
				else
					addConstraint(new IDExpression(false));
			}
		}
		
		else
			addConstraint(new BinaryExpression(
				arg, 
				BinaryExpression.EQUALS, 
				input.getValue()
			));
	}

	@Override
	protected Expression handleFunctionCall(FunctionCallExpression call) 
			throws CoreException {
		Function link = call.getFunction();
		
		//Tạo bản sao, sau đó fill giá trị cho các tham số
		FunctionCallExpression _call = (FunctionCallExpression) call.clone();
		for (Expression arg: _call.getArguments())
			_call.replace(arg, tables.fillExpression(arg));
		
		//Lời gọi hàm này tương ứng với hàm đang được gọi kiểm thử
		if (link == mCalling){
			Testcase testcase = mCallingTestcase;
			Expression[] args = _call.getArguments();
			Variable[] inputs = testcase.getInputs();
			
			//Thêm ràng buộc các giá trị tham số phải giống các đầu vào testcase
			System.out.println("Lời gọi hàm: " + _call);
			for (int i = 0; i < args.length; i++){
				addConstraintArgument(args[i], inputs[i]);
				System.out.printf("Khớp %s với %s\n", 
						args[i], inputs[i].getValue());
			}
			System.out.println();
			
			//Thay thế biểu thức bằng kết quả output của testcase
			return testcase.getReturnOutput();
		}
		
		//Hàm được gọi không là hàm đang xét kiểm thử
		else {
			return super.handleFunctionCall(call);
		}
	}

	/**
	 * Bộ phân tích mặc định
	 */
	public static final IntegrationPathParser DEFAULT = new IntegrationPathParser();
}
