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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FunctionPairNode extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 237;
	public static final int HEIGHT = 50;
	
	private static final LineBorder WHITE = new LineBorder(Color.WHITE, 1, true);
	private static final LineBorder BLACK = new LineBorder(Color.BLACK, 1, true);
	
	private FunctionPair mPair;
	
	/**
	 * Create the panel.
	 */
	public FunctionPairNode(FunctionPair pair) {
		mPair = pair;
		
		MouseAdapter onClick = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		};
		
		addMouseListener(onClick);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				setBorder(BLACK);
			}
			@Override
			public void focusLost(FocusEvent e) {
				setBorder(WHITE);
			}
		});
		setBorder(WHITE);
		setPreferredSize(new Dimension(237, 50));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		MouseAdapter buble = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				e.getComponent().getParent().dispatchEvent(e);
			}
		};
		
		JLabel lblL = new JLabel(pair.getCaller().getName());
		lblL.addMouseListener(buble);
		lblL.setToolTipText(lblL.getText());
		lblL.setOpaque(true);
		lblL.setBackground(Color.WHITE);
		lblL.setHorizontalAlignment(SwingConstants.CENTER);
		lblL.setPreferredSize(new Dimension(100, 40));
		add(lblL);
		
		JLabel label = new JLabel("->");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(20, 40));
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		label.addMouseListener(buble);
		add(label);
		
		JLabel lblL_1 = new JLabel(pair.getCalling().getName());
		lblL_1.setToolTipText(lblL_1.getText());
		lblL_1.setBackground(Color.WHITE);
		lblL_1.setOpaque(true);
		lblL_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblL_1.setPreferredSize(new Dimension(100, 40));
		lblL_1.addMouseListener(buble);
		add(lblL_1);

	}
	
	public FunctionPair getFunctionPair(){
		return mPair;
	}
	
}
