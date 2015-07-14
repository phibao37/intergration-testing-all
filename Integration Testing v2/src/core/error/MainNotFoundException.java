package core.error;

/**
 * Ngoại lệ được ném ra khi không tìm được hàm main trong tập các hàm đầu vào
 */
public class MainNotFoundException extends CoreException {
	private static final long serialVersionUID = 1L;

	/**
	 * Tạo một ngoại lệ không tìm được hàm main
	 */
	public MainNotFoundException() {
		super("No function has name \"main\"");
	}

}
