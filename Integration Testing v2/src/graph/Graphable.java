package graph;

import core.models.Contentable;

/**
 * Các phần tử có thể được hiển thị ra trong các Canvas
 */
public interface Graphable extends Contentable {
	
	/**
	 * Lấy nội dung hiển thị của phần tử, nội dung này sẽ được hiện ra trên các node
	 * của canvas
	 */
	public default String getNodeContent(){
		return getContent();
	}
	
	/**
	 * Lấy nội dung hiển thị dưới dạng HTML của phần tử.<br/>
	 * Đây là nội dung tóm tắt sẽ được hiển thị khi di chuột vào phần tử này
	 */
	public String getHTMLContent();
}
