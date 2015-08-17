package core.graph.canvas;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import core.graph.node.FunctionPairNode;
import core.inte.FunctionPair;
import core.models.Function;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FunctionPairCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int MARGIN_X = 5;
	private static final int MARGIN_Y = 10;
	
	private OnItemSelected mOnItemSelected = OnItemSelected.DEFAULT;
	private FunctionPairNode mPreviousItem;
	
	/**
	 * Create the panel.
	 */
	public FunctionPairCanvas() {
		setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN_X, MARGIN_Y));
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (mPreviousItem != null){
					mPreviousItem.selectFocus(false);
					mPreviousItem = null;
				}
			}
			
		});
	}
	
	/**
	 * Đặt danh sách các cặp hàm gọi hàm
	 */
	public void setFunctionPairList(ArrayList<FunctionPair> pairList){
		this.removeAll();
		mPreviousItem = null;
		
		this.setPreferredSize(new Dimension(
				MARGIN_X + FunctionPairNode.WIDTH, 
				MARGIN_Y + (MARGIN_Y + FunctionPairNode.HEIGHT)*pairList.size()
		));
		
		for (FunctionPair pair: pairList){
			FunctionPairNode node = new FunctionPairNode(pair);
			
			node.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					FunctionPairNode node = (FunctionPairNode) e.getComponent();
					
					selectItem(node, false);
					mOnItemSelected.selected(node, e.getClickCount() == 2);
				}
				
			});
			this.add(node);
		}
		
		this.revalidate();
		this.repaint();
	}
	
	private void selectItem(FunctionPairNode item, boolean scroll){
		if (mPreviousItem != null)
			mPreviousItem.selectFocus(false);
		mPreviousItem = item;
		mPreviousItem.selectFocus(true);
		
		if (!scroll) return;
		int index = 0;
		JScrollPane parent = (JScrollPane)getParent().getParent();
		
		while (getComponent(index) != item)
			index++;
		parent.getVerticalScrollBar().setValue(index*(MARGIN_Y + 
				(MARGIN_Y + FunctionPairNode.HEIGHT))
				+ item.getHeight()/2 - parent.getViewport().getHeight()/2);
		parent.revalidate();
	}
	
	public void setOnItemSelected(OnItemSelected listener){
		mOnItemSelected = listener;
	}
	
	public FunctionPair selectPair(Function source, Function target){
		for (Component c: getComponents()){
			if (c instanceof FunctionPairNode){
				FunctionPairNode n = (FunctionPairNode) c;
				FunctionPair p = n.getFunctionPair();
				if (p.getCaller() == source && p.getCalling() == target){
					mOnItemSelected.selected(n, false);
					selectItem(n, true);
					return p;
				}
			}
		}
		return null;
	}
	
	public static interface OnItemSelected{
		public void selected(FunctionPairNode node, boolean dbClick);
		
		static final OnItemSelected DEFAULT = new OnItemSelected() {
			public void selected(FunctionPairNode node, boolean dbClick) {}
		};
	}
}
