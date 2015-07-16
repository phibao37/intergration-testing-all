package core.error;

/**
 * Ngoại lệ khi có một thread đang xử lý công việc. Chỉ cho phép duy nhất một thread
 * để xử lý vì sau khi kết thúc, nó có thể chỉnh sửa vào giao diện GUI, nhiều thread cùng
 * kết thúc tại một thời điểm dễ dần đến các lỗi đồng bộ
 */
public class ThreadStateException extends CoreException {
	private static final long serialVersionUID = 1L;

	private Thread mThread;
	
	/**
	 * Tạo ngoại lệ về thread đang xử lý công việc 
	 * @param thread tiến trình con đang xử lý
	 */
	public ThreadStateException(Thread thread) {
		super("Thread id %s still doing its work", thread.getId());
		mThread = thread;
	}
	
	/**
	 * Trả về tiến trình đang chạy gây ra ngoại lệ
	 */
	public Thread getRunningThread(){
		return mThread;
	}

}
