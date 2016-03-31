package graph.node;

import java.util.ArrayList;

import api.models.IElement;

public class NodeAdapter<E extends IElement> extends ArrayList<Node<E>> {
	private static final long serialVersionUID = 1L;

	protected Node<E> getNodeByElement(E element){
		return getNodeByElement(element, this);
	}
	
	protected Node<E> getNodeByElement(E element, Iterable<Node<E>> iter){
		for (Node<E> n: iter)
			if (n != null && n.getElement() == element)
				return n;
		return null;
	}
}
