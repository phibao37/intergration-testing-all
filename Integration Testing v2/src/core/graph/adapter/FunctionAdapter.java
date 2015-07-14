package core.graph.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import core.error.MainNotFoundException;
import core.graph.node.FunctionNode;
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
	 * @param fList Danh sách hàm số đã được gán các hàm được gọi bên trong nó
	 * @throws MainNotFoundException khi không có hàm <b>main</b> được tìm thấy
	 */
	public FunctionAdapter(ArrayList<Function> fList) throws MainNotFoundException{
		Function main = null;
		for (Function f: fList){
			if (f.getName().equals("main")){
				main = f;
			}
		}
		if (main == null)
			throw new MainNotFoundException();
		generateNode(main);
	}
	
	/**
	 * Phân tích cấu trúc hàm gọi hàm với một hàm gốc được chỉ định
	 * @param root hàm số gốc, không nhất thiết là hàm main
	 */
	public FunctionAdapter(Function root){
		if (root == null)
			throw new NullPointerException();
		generateNode(root);
	}
	
	/**
	 * Tạo cấu trúc đồ thị tương ứng với cấu trúc gọi hàm
	 */
	private void generateNode(Function main){
		FunctionNode node, rNode;
		Queue<FunctionNode> queue = new LinkedList<FunctionNode>();

		node = new FunctionNode(main);
		queue.add(node);
		queue.add(null);
		
		while (!queue.isEmpty()){
			node = queue.remove();
			this.add(node);
			
			ArrayList<Function> refer = node.getFunction().getRefers();
			ArrayList<FunctionNode> nRefer = new ArrayList<FunctionNode>();
			
			for (Function f: refer){
				rNode = getNodeByElement(f);
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
	
}










