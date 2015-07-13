package core.error;

/**
 * Ngoại lệ khi định dạng format không phù hợp với một kiểu nhất định
 */
public class IllegalFormatException extends CoreException {
	private static final long serialVersionUID = 1L;

	/**
	 * Tạo ngoại lệ về định dạng format
	 * @param format chuỗi format được cho là không hợp lệ
	 * @param type kiểu của format
	 */
	public IllegalFormatException(String format, String type) {
		super("\"%s\" is not a valid format for type %s", format, type);
	}

}
