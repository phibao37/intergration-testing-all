package core.error;

/**
 * Ngoại lệ khi giá trị cài đặt không hợp lệ
 */
public class InvalidSettingException extends CoreException {
	private static final long serialVersionUID = 1L;

	/**
	 * Tạo một ngoại lệ về giá trị cài đặt với chuỗi mô tả tương ứng
	 */
	public InvalidSettingException(String message, Object... args) {
		super(message, args);
	}

}
