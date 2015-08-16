package core.graph.node;

import javax.swing.JPanel;

import java.awt.FlowLayout;

import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.SwingConstants;

import core.inte.FunctionPair;

import java.awt.Color;
import javax.swing.border.LineBorder;

public class FunctionPairNode extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 237;
	public static final int HEIGHT = 50;
	
	private static final LineBorder WHITE = new LineBorder(Color.WHITE, 1, true);
	private static final LineBorder BLACK = new LineBorder(Color.BLACK, 1, true);
	
	private FunctionPair mPair;
	
	public void selectFocus(boolean focus){
		setBorder(focus ? BLACK : WHITE);
	}
	
	/**
	 * Create the panel.
	 */
	public FunctionPairNode(FunctionPair pair) {
		mPair = pair;
		
		selectFocus(false);
		setPreferredSize(new Dimension(237, 50));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblL = new JLabel(pair.getCaller().getName());
		lblL.setOpaque(true);
		lblL.setBackground(Color.WHITE);
		lblL.setHorizontalAlignment(SwingConstants.CENTER);
		lblL.setPreferredSize(new Dimension(100, 40));
		add(lblL);
		
		JLabel label = new JLabel("->");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(20, 40));
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		add(label);
		
		JLabel lblL_1 = new JLabel(pair.getCalling().getName());
		lblL_1.setBackground(Color.WHITE);
		lblL_1.setOpaque(true);
		lblL_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblL_1.setPreferredSize(new Dimension(100, 40));
		add(lblL_1);
		
		setToolTipText(pair.toString());
	}
	
	public FunctionPair getFunctionPair(){
		return mPair;
	}
	
}
