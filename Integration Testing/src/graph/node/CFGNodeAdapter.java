package graph.node;

import java.lang.reflect.Array;
import java.util.ArrayList;

import api.models.ICFG;
import api.models.IStatement;

public class CFGNodeAdapter extends NodeAdapter<IStatement> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public CFGNodeAdapter(ICFG cfg) {
		IStatement[] cfgStm = cfg.getStatements();
		for (IStatement stm: cfgStm)
			stm.setVisit(false);
		
		for (IStatement stm: cfgStm){
			if (stm.isVisited() || !stm.shouldDisplay()) continue;
			stm.setVisit(true);
			
			IStatement next = nextNormal(stm);
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
			IStatement stm = nextNormal(node.getElement());
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
	
	private static IStatement nextNormal(IStatement stm){
		if (!stm.isNormal() || stm.isCondition())
			return stm;
		IStatement next = stm.getTrue();
		
		while (next != null && next.isNormal() && !next.isCondition()){
			stm = next;
			next = stm.getTrue();
		}
		return stm;
	}
	
	private static IStatement next(IStatement stm){
		while (stm != null && !stm.shouldDisplay())
			stm = stm.getTrue();
		return stm;
	}
}
