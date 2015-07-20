package core.graph.node;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;

import javax.swing.JSpinner;
import javax.swing.JCheckBox;

import core.unit.LoopStatement;

import java.awt.Dimension;

import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LoopNode extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 180;
	public static final int HEIGHT = 90;
	private static final Border WHITE = new LineBorder(Color.WHITE, 1, true);
	private static final Border BLACK = new LineBorder(Color.BLACK, 1, true);
	
	private LoopStatement mLoop;
	private JSpinner spinner;
	
	private int mValue = 1;
	private OnValueChanged mOnValueChanged = OnValueChanged.DEFAULT;
	private JCheckBox chckbxChnKimTh;
	private ArrayList<LoopNode> childs;
	
	/**
	 * Create the panel.
	 */
	public LoopNode(LoopStatement loop) {
		childs = new ArrayList<LoopNode>();
		mLoop = loop;
		
		MouseAdapter onMouseClick = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		};
		FocusAdapter onFocus = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				setBorder(BLACK);
			}
			@Override
			public void focusLost(FocusEvent e) {
				setBorder(WHITE);
			}
		};
		
		addMouseListener(onMouseClick);
		addFocusListener(onFocus);
		setBackground(Color.WHITE);
		setBorder(WHITE);
		setSize(new Dimension(WIDTH, HEIGHT));
		
		JLabel lblI = new JLabel(loop == null ? 
				"Condition" : loop.getCondition().getContent());
		lblI.addMouseListener(onMouseClick);
		lblI.setBounds(10, 9, 160, 32);
		lblI.setPreferredSize(new Dimension(160, 32));
		lblI.setOpaque(true);
		lblI.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel = new JPanel();
		panel.addMouseListener(onMouseClick);
		panel.setBounds(10, 46, 160, 30);
		panel.setPreferredSize(new Dimension(160, 30));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doValueChanged((int) spinner.getValue());
			}
		});
		spinner.setPreferredSize(new Dimension(34, 20));
		spinner.setModel(new SpinnerNumberModel(new Integer(mValue),
				new Integer(0), null, new Integer(1)));
		panel.add(spinner);
		
		chckbxChnKimTh = new JCheckBox("Chọn kiểm thử");
		chckbxChnKimTh.addFocusListener(onFocus);
		chckbxChnKimTh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxChnKimTh.isSelected())
					doValueChanged(-1);
				else
					doValueChanged((int) spinner.getValue());
			}
		});
		panel.add(chckbxChnKimTh);
		setLayout(null);
		add(lblI);
		add(panel);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (enabled){
			spinner.setEnabled(mValue != -1);
		} else {
			spinner.setEnabled(false);
		}
		chckbxChnKimTh.setEnabled(enabled);
		setFocusable(enabled);
		super.setEnabled(enabled);
		
		setChildEnable(enabled);
	}
	
	public void setChildEnable(boolean enabled){
		for (LoopNode child: childs)
			child.setEnabled(enabled);
	}

	private void doValueChanged(int value){
		if (mValue != value){
			int oldValue = mValue;
			mValue = value;
			spinner.setEnabled(value != -1);
			mOnValueChanged.valueChange(mValue, oldValue, this);
		}
	}
	
	public void setNormalValue(){
		if (chckbxChnKimTh.isSelected())
			chckbxChnKimTh.doClick();
		//doValueChanged((int) spinner.getValue());
	}
	
	public int getValue(){
		return mValue;
	}
	
	public void setOnValueChanged(OnValueChanged listener){
		if (listener == null)
			mOnValueChanged = OnValueChanged.DEFAULT;
		else
			mOnValueChanged = listener;
	}
	
	public LoopStatement getStatement(){
		return mLoop;
	}
	
	public void addChild(LoopNode child){
		childs.add(child);
	}
	
	public ArrayList<LoopNode> getChilds(){
		return childs;
	}
	
	public static interface OnValueChanged{
		public void valueChange(int value, int oldValue, LoopNode node);
		
		static final OnValueChanged DEFAULT = new OnValueChanged() {
			@Override
			public void valueChange(int value, int oldValue, LoopNode node) {}
		};
	}
}
