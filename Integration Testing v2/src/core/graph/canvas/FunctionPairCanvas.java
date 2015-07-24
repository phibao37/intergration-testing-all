package core.graph.canvas;

import javax.swing.JPanel;

import core.graph.node.FunctionPairNode;
import core.inte.FunctionPair;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class FunctionPairCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int MARGIN_X = 5;
	private static final int MARGIN_Y = 10;
	
	private OnItemSelected mOnItemSelected = OnItemSelected.DEFAULT;
	
	/**
	 * Create the panel.
	 */
	public FunctionPairCanvas() {
		setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN_X, MARGIN_Y));

	}
	
	/**
	 * Đặt danh sách các cặp hàm gọi hàm
	 */
	public void setFunctionPairList(ArrayList<FunctionPair> pairList){
		this.removeAll();
		this.setPreferredSize(new Dimension(
				MARGIN_X + FunctionPairNode.WIDTH, 
				MARGIN_Y + (MARGIN_Y + FunctionPairNode.HEIGHT)*pairList.size()
		));
		
		for (FunctionPair pair: pairList){
			FunctionPairNode node = new FunctionPairNode(pair);
			node.addFocusListener(new FocusAdapter() {

				@Override
				public void focusGained(FocusEvent e) {
					FunctionPairNode node = (FunctionPairNode) e.getComponent();
					mOnItemSelected.selected(node);
				}
				
			});
			this.add(node);
		}
		
		this.revalidate();
		this.repaint();
	}
	
	public void setOnItemSelected(OnItemSelected listener){
		mOnItemSelected = listener;
	}
	
	public static interface OnItemSelected{
		public void selected(FunctionPairNode node);
		
		static final OnItemSelected DEFAULT = new OnItemSelected() {
			public void selected(FunctionPairNode node) {}
		};
	}
}
