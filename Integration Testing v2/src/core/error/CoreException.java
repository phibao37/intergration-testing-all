package core.error;

/**
 * Các ngoại lệ liên quan đến các tác vụ trong vùng core
 *
 */
public class CoreException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Tạo một ngoại lệ core với nội dung mô tả 
	 * @param message nội dung mô tả dẫn đến ngoại lệ
	 * @param args tham số trong nội dung ({@link String#format(String, Object...)})
	 */
	public CoreException(String message, Object... args){
		super(String.format(message, args));
	}
	
	/**
	 * Tạo một ngoại lệ core từ một ngoại lệ khác
	 * @param message nội dung ngoại lệ
	 * @param cause ngoại lệ được đính kém
	 */
	public CoreException(String message, Throwable cause){
		super(message, cause);
	}
}
