package core.error;

/**
 * Ngoại lệ khi cố gắng sửa đổi giá trị của một kiểu có gán cờ hiệu hằng số
 */
public class FinalModifierException extends CoreException {
	private static final long serialVersionUID = 1L;

	/**
	 * Tạo một ngoại lện sửa đổi giá trị hằng
	 */
	public FinalModifierException(String message, Object... args) {
		super(message, args);
	}

}
