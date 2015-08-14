package core.models;

import core.solver.Solver.Result;

/**
 * Một testcase giúp xác định một thành phần của ứng dụng hoạt động đúng hay không. <br/>
 * Đối với việc kiểm thử đơn vị, nó thường bao gồm: hàm được kiểm thử 
 * (unit-under-test), các dữ liệu đầu vào, đầu ra ứng với đầu vào khi chạy, đầu ra 
 * mong muốn,... 
 *
 */
public class Testcase {
	
	private Variable[] listInput;
	
	private Variable[] listOutput;
	private Expression mOutput;
	
	/**
	 * Tạo một testcase mới với danh sách biến đầu vào và kết quả đầu ra
	 * @param inputs danh sách các biến số đầu vào (đã có sẵn giá trị)
	 * @param output biểu thức kết quả của unit, có thể là null
	 */
	public Testcase(Variable[] inputs, Expression output){
		this(inputs, output, null);
	}
	
	/**
	 * Tạo một testcase mới cho trường hợp các biến số bị thay đổi giá trị sau khi chạy
	 * @param inputs danh sách các biến số đầu vào (đã có sẵn giá trị)
	 * @param output biểu thức kết quả của unit, có thể là null
	 * @param outputs danh sách các biến số đầu vào, với giá trị sau khi unit được chạy
	 */
	public Testcase(Variable[] inputs, Expression output, Variable[] outputs){
		listInput = inputs;
		listOutput = outputs;
		mOutput = output;
	}
	
	/**
	 * Tạo một testcase từ kết quả giải hệ ràng buộc.
	 * @param result
	 * @throws RuntimeException kết quả không có nghiệm
	 */
	public Testcase(Result result) throws RuntimeException{
		if (result.getSolutionCode() != Result.SUCCESS)
			throw new RuntimeException("This result has no solution");
		
		listInput = result.getSolution();
		mOutput = result.getReturnValue();
	}
	
	/**
	 * Lấy danh sách các biến số đầu vào được truyền vào unit khi kiểm thử 
	 */
	public Variable[] getInputs(){
		return listInput;
	}
	
	/**
	 * Một số unit có làm thay đổi giá trị của các biến số đầu vào (thí dụ: truyền vào
	 * biến mảng và có câu lệnh sửa đổi một phần tử mảng). Phương thức này trả về danh
	 * sách các biến số tương ứng với danh sách đầu vào, với một số biến có giá trị khác
	 * trong quá trình chạy. Có thể trả về <i>null</i> nếu unit không làm thay đổi các
	 * biến đầu vào
	 */
	public Variable[] getOutputs(){
		return listOutput;
	}
	
	/**
	 * Lấy biểu thức trả về sau khi đơn vị được chạy với testcase này
	 */
	public Expression getReturnOutput(){
		return mOutput;
	}
	
	/**
	 * Trả về chuỗi danh sách các giá trị đầu vào
	 */
	public String getSummaryInput(){
		String s = "(";
		if (listInput.length > 0){
			s += listInput[0].getValueString();
			for (int i = 1; i < listInput.length; i++)
				s += ", " + listInput[i].getValueString();
		}
		return s + ")";
	}
	
}
