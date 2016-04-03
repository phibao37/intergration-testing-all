package graph.node;

import java.lang.reflect.Array;
import java.util.ArrayList;

import api.models.ICFG;
import api.models.IStatement;

public class CFGNodeAdapter extends NodeAdapter<IStatement> {
	private static final long serialVersionUID = 1L;

	private int[] receiveCount;
	
	@SuppressWarnings("unchecked")
	public CFGNodeAdapter(ICFG cfg) {
		IStatement[] cfgStm = cfg.getStatements();
		receiveCount = new int[cfgStm.length];
		int i = 0;
		for (IStatement stm: cfgStm){
			stm.setVisit(false);
			stm.setId(i++);
		}
		
		for (IStatement stm: cfgStm){
			if (stm.getTrue() != null)
				receiveCount[stm.getTrue().getId()]++;
			if (stm.isCondition() && stm.getFalse() != null)
				receiveCount[stm.getFalse().getId()]++;
		}
		
		for (IStatement stm: cfgStm){
			if (stm.isVisited() || !stm.shouldDisplay()) continue;
			stm.setVisit(true);
			
			IStatement next = nextInBlock(stm);
			if (next == stm){
				add(new CFGNode(stm));
			} else {
				ArrayList<IStatement> stmList = new ArrayList<>();
				while (stm != next){
					stmList.add(stm);
					stm = stm.getTrue();
					stm.setVisit(true);
				}
				stmList.add(next);
				add(new CFGNode(stmList));
			}
		}
		
		for (Node<IStatement> node: this){
			IStatement stm = nextInBlock(node.getElement());
			Node<IStatement> nodeTrue = getNodeByElement(next(stm.getTrue())),
					nodeFalse = getNodeByElement(next(stm.getFalse()));
			
			boolean isCondition = nodeTrue != nodeFalse;
			Node<IStatement>[] refer = (Node<IStatement>[]) 
					Array.newInstance(node.getClass(), isCondition ? 2 : 1);
			refer[0] = nodeTrue;
			if (isCondition)
				refer[1] = nodeFalse;
			node.setRefer(refer);
		}
	}
	
	/**
	 * Trả về câu lệnh cuối cùng trong khối câu lệnh nếu có thể nhóm lại
	 */
	private IStatement nextInBlock(IStatement stm){
		if (!stm.isNormal() || stm.isCondition())
			return stm;
		IStatement next = stm.getTrue();
		
		while (next != null && receiveCount[next.getId()] == 1
				&& next.isNormal() && !next.isCondition()){
			stm = next;
			next = stm.getTrue();
		}
		return stm;
	}
	
	/**
	 * Trả về câu lệnh tiếp theo cần được hiển thị, bỏ qua các dấu ngoặc {, }
	 */
	private IStatement next(IStatement stm){
		while (stm != null && !stm.shouldDisplay())
			stm = stm.getTrue();
		return stm;
	}
}
