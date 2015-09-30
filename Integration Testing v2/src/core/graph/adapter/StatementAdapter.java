package core.graph.adapter;

import java.util.ArrayList;

import core.graph.node.StatementNode;
import core.models.Statement;
import core.models.statement.ScopeStatement;
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
			if (!(stm instanceof ScopeStatement))
				this.add(new StatementNode(stm));
		
		for (StatementNode node: this){
			Statement stm = node.getStatement();
			StatementNode nodeTrue = getNodeByElement(
					Statement.skipScope(stm.getTrue()));
			StatementNode nodeFalse = getNodeByElement(
					Statement.skipScope(stm.getFalse()));
			
			if (nodeTrue == nodeFalse)
				node.setRefers(new StatementNode[]{nodeTrue});
			else
				node.setRefers(new StatementNode[]{nodeTrue, nodeFalse});
		}
		
	}
	
	/**
	 * Đánh dấu các nút đồ họa ứng với danh sách các câu lệnh
	 */
	@SuppressWarnings("unchecked")
	public void selectNodesByPath(ArrayList<Statement> stmList, 
			int flagTrue, int flagFalse, boolean addLabel){
		ArrayList<Statement> copy = (ArrayList<Statement>) stmList.clone();
		
		copy.removeIf(t -> t instanceof ScopeStatement);
		
		StatementNode[] path = new StatementNode[copy.size()];
		int i = 0;
		
		for (Statement stm: copy){
			path[i] = getNodeByElement(stm);
			if (addLabel)
				path[i].setLabel(i);
			i++;
		}
		
		for (i = 0; i < path.length - 1; i++){
			StatementNode[] refer = (StatementNode[]) path[i].getRefers();
			
			if (refer[0] == path[i+1])
				path[i].addFlag(flagTrue);
			else
				path[i].addFlag(flagFalse);
		}
	}
}