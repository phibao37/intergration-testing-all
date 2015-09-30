package core.graph.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import core.graph.node.FunctionNode;
import core.graph.node.Node;
import core.inte.FunctionCallGraph;
import core.inte.FunctionPair;
import core.models.Function;

/**
 * Từ một danh sách các hàm ({@link Function}) đã có liên kết tham chiếu tới nhau, 
 * tạo ra một danh sách các nút đồ họa ({@link FunctionNode}) tương ứng để hiển thị,
 * các mối liên kết cũng được ánh xạ tương ứng.<br/>
 *  Các liên kết này tạo thành một cây cấu trúc hàm gọi hàm mà đỉnh cây
 *  chính là hàm số main (hoặc một hàm được đặt làm gốc chỉ định)
 * @author ducvu
 */
public class FunctionAdapter extends NodeAdapter<FunctionNode> {
	private static final long serialVersionUID = 1L;

	/**
	 * Phân tích cấu trúc hàm gọi hàm, tìm ra hàm số <b>main</b> làm nút gốc
	 * @param fGraph Danh sách hàm số đã được gán các hàm được gọi bên trong nó
	 */
	public FunctionAdapter(FunctionCallGraph fGraph){
		generateNode(fGraph.getRoot());
	}
	
	/**
	 * Tạo cấu trúc đồ thị tương ứng với cấu trúc gọi hàm
	 */
	private void generateNode(Function main){
		FunctionNode node, rNode;
		Queue<FunctionNode> queue = new LinkedList<>();

		node = new FunctionNode(main);
		queue.add(node);
		queue.add(null);
		
		while (!queue.isEmpty()){
			node = queue.remove();
			this.add(node);
			
			ArrayList<Function> refer = node.getFunction().getRefers();
			ArrayList<FunctionNode> nRefer = new ArrayList<>();
			
			for (Function f: refer){
				rNode = getNodeByElement(f);
				if (rNode == null)
					rNode = getNodeByElement(f, queue);
				if (rNode == null){
					rNode = new FunctionNode(f);
					queue.add(rNode);
				}
				nRefer.add(rNode);
			}
			node.setRefers(nRefer.toArray(new FunctionNode[nRefer.size()]));
			
			if (queue.peek() == null){
				this.add(queue.remove());
				if (!queue.isEmpty())
					queue.add(null);
			}
		}
	}

	/**
	 * Đánh dấu một cặp hàm gọi hàm là đang được chọn
	 */
	public void selectFunctionPair(FunctionPair pair) {
		FunctionNode caller = getNodeByElement(pair.getCaller());
		FunctionNode calling = getNodeByElement(pair.getCalling());
		int i = 0;
		
		for (Node refer: caller.getRefers()){
			if (refer == calling){
				caller.setSelectedRefer(i);
				break;
			}
			i++;
		}
	}
	
}










