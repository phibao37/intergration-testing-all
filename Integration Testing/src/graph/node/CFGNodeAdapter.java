package graph.node;

import java.lang.reflect.Array;
import api.models.ICFG;
import api.models.IStatement;

public class CFGNodeAdapter extends NodeAdapter<IStatement> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public CFGNodeAdapter(ICFG cfg) {
		for (IStatement stm: cfg.getStatements())
			if (stm.shouldDisplay())
				add(new CFGNode(stm));
		
		for (Node<IStatement> node: this){
			IStatement stm = node.getElement();
			Node<IStatement> nodeTrue = getNodeByElement(nextNormal(stm.getTrue())),
					nodeFalse = getNodeByElement(nextNormal(stm.getFalse()));
			
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
		while (stm != null && !stm.shouldDisplay())
			stm = stm.getTrue();
		return stm;
	}
}
