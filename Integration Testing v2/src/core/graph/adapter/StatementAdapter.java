package core.graph.adapter;

import core.graph.node.StatementNode;
import core.models.Statement;
import core.unit.CFG;

/**
 * Từ một câu lệnh bắt đầu hàm, phân tích và tạo ra các nút đồ họa {@link StatementNode} 
 * tương ứng với các câu lệnh, có cùng các mối quan hệ nhánh đúng/sai như ở các câu lệnh
 * @author ducvu
 *
 */
public class StatementAdapter extends NodeAdapter<StatementNode> {
	private static final long serialVersionUID = 1L;

	/**
	 * Phân tích các liên kết nhánh đúng/sai từ câu lệnh bắt đầu hàm, tạo ra các nút 
	 * đồ họa tương ứng
	 */
	public StatementAdapter(CFG cfg){
		Statement[] stmList = cfg.getStatements();
		
		for (Statement stm: stmList)
			this.add(new StatementNode(stm));
		
		for (StatementNode node: this){
			Statement stm = node.getStatement();
			StatementNode nodeTrue = this.getNodeByElement(stm.getTrue());
			StatementNode nodeFalse = this.getNodeByElement(stm.getFalse());
			
			if (nodeTrue == nodeFalse)
				node.setRefers(new StatementNode[]{nodeTrue});
			else
				node.setRefers(new StatementNode[]{nodeTrue, nodeFalse});
		}
		/*StatementNode beginNode = new StatementNode(cfg.getStatements()[0]);
		Queue<StatementNode> queue = new LinkedList<StatementNode>();
		
		queue.add(beginNode);
		this.add(beginNode);
		while (!queue.isEmpty()){
			StatementNode node = queue.remove();
			Statement sNode = node.getStatement();
			int length = sNode.isCondition() ? 2 : 1;
			StatementNode[] refer = new StatementNode[length];
			Statement[] sRefer = new Statement[length];
			
			sRefer[0] = sNode.getTrue();
			if (length == 2)
				sRefer[1] = sNode.getFalse();
			
			for (int i = 0; i < length; i++){
				if (sRefer[i] == null)
					continue;
				
				refer[i] = getNodeByElement(sRefer[i]);
				if (refer[i] == null){
					refer[i] = new StatementNode(sRefer[i]);
					queue.add(refer[i]);
					this.add(refer[i]);
				}
			}
			
			node.setRefers(refer);
		}*/
		
	}
}