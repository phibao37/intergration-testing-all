package core.error;

/**
 * Ngoại lệ xảy ra khi thực thi một tiến trình và tiến trình đó kết thúc với giá trị
 * trả về lỗi (khác 0)
 */
public class ProcessErrorException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	private int mCode;
	private String mMessage;
	
	/**
	 * Tạo ngoại lệ với mã kết thúc và mô tả lỗi
	 * @param code giá trị kết thúc của tiến trình
	 * @param message cung cấp mô tả về lỗi
	 */
	public ProcessErrorException(int code, String message) {
		super("Process exited with code %d\nMessage: %s", code, message);
		mCode = code;
		mMessage = message;
	}
	
	/**
	 * Trả về giá trị kết thúc lỗi từ tiến trình
	 */
	public int getCode(){
		return mCode;
	}
	
	/**
	 * Trả về chuỗi mô tả nội dung lỗi xảy ra 
	 */
	public String getErrorMessage(){
		return mMessage;
	}
	
}
