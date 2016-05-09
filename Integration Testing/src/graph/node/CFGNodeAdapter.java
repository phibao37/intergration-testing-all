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
		
		//Gán thứ tự và reset trạng thái
		for (IStatement stm: cfgStm){
			stm.setVisit(false);
			stm.setId(i++);
		}
		
		//Đếm số lần một câu lệnh nhận đích đến từ câu lệnh khác
		for (IStatement stm: cfgStm){
			if (stm.getTrue() != null)
				receiveCount[stm.getTrue().getId()]++;
			if (stm.isCondition() && stm.getFalse() != null)
				receiveCount[stm.getFalse().getId()]++;
		}
		
		//Tạo câu lệnh đồ họa đơn hoặc nhóm
		for (IStatement stm: cfgStm){
			if (stm.isVisited() || !stm.shouldDisplay()) continue;
			stm.setVisit(true);
			
			IStatement next = lastInBlock(stm);
			if (next == stm){
				add(new CFGNode(stm));
			} else {
				ArrayList<IStatement> stmList = new ArrayList<>();
				while (stm != next){
					if (stm.shouldDisplay())
						stmList.add(stm);
					stm = stm.getTrue();
					stm.setVisit(true);
				}
				if (next.shouldDisplay())
					stmList.add(next);
				add(new CFGNode(stmList));
			}
		}
		
		//Liên kết các câu lệnh với nhau
		for (Node<IStatement> node: this){
			IStatement stm = lastInBlock(node.getElement());
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
	private IStatement lastInBlock(IStatement stm){
		if (!stm.isNormal() || stm.isCondition())
			return stm;
		IStatement next = stm.getTrue();
		
		while (next != null 
				&& receiveCount[next.getId()] == 1 //chỉ nhận từ 1 câu lệnh
				&& (next.isNormal() || !next.shouldDisplay()) //câu lệnh thường,{}
				&& !next.isCondition()){ //bỏ qua câu lệnh điều kiện
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
