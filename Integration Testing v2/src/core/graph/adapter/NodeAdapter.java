package core.graph.adapter;

import java.util.ArrayList;

import core.graph.Graphable;
import core.graph.node.Node;

/**
 * Lưu trữ danh sách các nút đồ họa có cấu trúc liên kết với nhau.<br/>
 * Thứ tự của các nút thường theo thứ tự duyệt cây cấu trúc liên kết theo hàng ngang
 * (trên xuống dưới, trái sang phải)
 */
public class NodeAdapter<T extends Node> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * Tìm nút trong danh sách có phần tử chỉ định
	 */
	protected T getNodeByElement(Graphable element){
		return getNodeByElement(element, this);
	}
	
	/**
	 * Tìm nút trong danh sách có phần tử chỉ định, trong một danh sách chỉ định
	 */
	protected T getNodeByElement(Graphable element, Iterable<T> iter){
		for (T item: iter)
			if (item != null && item.getElement() == element)
				return item;
		return null;
	}
}
