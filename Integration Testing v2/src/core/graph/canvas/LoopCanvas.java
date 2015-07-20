package core.graph.canvas;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import core.graph.node.LoopNode;
import core.unit.LoopStatement;
import core.unit.LoopablePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoopCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	private LoopNode mRoot;
	private LoopablePath mPath;

	/**
	 * Create the panel.
	 */
	public LoopCanvas() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		});
		setLayout(null);
	}
	
	private int maxX, maxY;
	
	public void setLoopPath(LoopablePath path){
		mPath = path;
		this.removeAll();
		maxX = maxY = 0;
		mRoot = new LoopNode(null);
		
		int y = 0;
		for (LoopStatement loop: path.getLoops()){
			y += generateNode(loop, 0, y, mRoot);
		}
		this.setPreferredSize(new Dimension(
				(maxX + 1) * LoopNode.WIDTH, 
				(maxY + 1) * LoopNode.HEIGHT));
		this.revalidate();
		this.repaint();
	}
	
	private int generateNode(LoopStatement loop, int x, int y, LoopNode parent){
		if (x > maxX)
			maxX = x;
		if (y > maxY)
			maxY = y;
		
		LoopNode n = new LoopNode(loop);
		n.setLocation(x*LoopNode.WIDTH, y*LoopNode.HEIGHT);
		n.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				LoopNode instance = (LoopNode) e.getComponent();
				mOnNodeSelect.selected(instance);
			}
		});
		n.setOnValueChanged(new LoopNode.OnValueChanged() {
			@Override
			public void valueChange(int value, int oldValue, LoopNode node) {
				if (value == 0)
					node.setChildEnable(false);
				else if (oldValue == 0)
					node.setChildEnable(true);
				
				if (value == -1){
					disableAllOther(node);
					
					ArrayList<Integer> indexes = new ArrayList<Integer>();
					for (LoopNode c: mRoot.getChilds())
						traceValue(c, indexes);
					mOnApplyValue.applied(indexes, mPath);
				}
			}
		});
		add(n);
		parent.addChild(n);
		
		ArrayList<LoopStatement> childs = loop.getBody().getLoops();
		if (childs.size() == 0)
			return 1;
		int size = 0;
		
		for (LoopStatement child: childs)
			size += generateNode(child, x+1, y + size, n);
		
		return size;
	}
	
	private void disableAllOther(LoopNode remain){
		for (Component c: getComponents()){
			if (c != remain)
				((LoopNode)c).setNormalValue();
		}
	}
	
	private void traceValue(LoopNode node, ArrayList<Integer> indexes){
		indexes.add(node.getValue());
		for (LoopNode child: node.getChilds())
			traceValue(child, indexes);
	}
	
	private OnNodeSelect mOnNodeSelect = OnNodeSelect.DEFAULT;
	private OnApplyValue mOnApplyValue = OnApplyValue.DEFAULT;
	
	public void setOnNodeSelect(OnNodeSelect listener){
		if (listener == null)
			mOnNodeSelect = OnNodeSelect.DEFAULT;
		else
			mOnNodeSelect = listener;
	}
	
	public void setOnApplyValue(OnApplyValue listener){
		if (listener == null)
			mOnApplyValue = OnApplyValue.DEFAULT;
		else
			mOnApplyValue = listener;
	}
	
	public static interface OnNodeSelect{
		public void selected(LoopNode node);
		
		static final OnNodeSelect DEFAULT = new OnNodeSelect() {
			public void selected(LoopNode node) {}
		};
	}
	
	public static interface OnApplyValue{
		public void applied(ArrayList<Integer> indexes, LoopablePath path);
		
		static final OnApplyValue DEFAULT = new OnApplyValue() {
			public void applied(ArrayList<Integer> indexes, LoopablePath path) {}
		};
	}
}
