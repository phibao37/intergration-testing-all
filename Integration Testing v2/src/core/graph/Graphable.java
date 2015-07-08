package core.graph;

/**
 * Các phần tử có thể được hiển thị ra trong các Canvas
 */
public interface Graphable {
	
	/**
	 * Lấy nội dung hiển thị của phần tử
	 */
	public String getContent();
	
	/**
	 * Lấy nội dung hiển thị dưới dạng HTML của phần tử.<br/>
	 * Đây là nội dung tóm tắt sẽ được hiển thị khi di chuột vào phần tử này
	 */
	public String getHTMLContent();
}
