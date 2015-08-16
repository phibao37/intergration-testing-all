package core.graph;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.S.SCREEN;
import core.Utils;
import core.error.CoreException;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Function;
import core.models.Function.TestcaseManager;
import core.models.Testcase;
import core.models.Variable;
import core.models.expression.IDExpression;
import core.models.type.ArrayType;
import core.models.type.BasicType;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import java.awt.Dimension;
import javax.swing.border.EtchedBorder;

public class TestcaseManageDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private Variable[] mParas;
	private JTextField txt_return_value;
	private GridBagLayout gbl_current;
	private JTextField[] list_value;
	private core.models.Type rtnType;
	private TestcaseManager tm;
	private int currentEdit = -1;

	private boolean isVoid(){
		return rtnType == BasicType.VOID;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Variable[] v = new Variable[]{
					new Variable("x", BasicType.INT),
					new ArrayVariable("y", new ArrayType(BasicType.INT, 0))
			};
			Function f = new Function("test", v, "", BasicType.INT);
			
			TestcaseManageDialog dialog = new TestcaseManageDialog(
					null, f.getTestcaseManager());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public TestcaseManageDialog(JFrame parent, TestcaseManager tm) {
		super(parent, null, Dialog.ModalityType.DOCUMENT_MODAL);
		this.tm = tm;
		setTitle("Quản lý testcase - " + tm.getFunction().getName());
		setIconImage(Toolkit.getDefaultToolkit().getImage(TestcaseManageDialog.class.getResource("/image/testcase.png")));
		setBounds(100, 100, 800, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblThmMichnhSa = new JLabel("Thêm mới/Chỉnh sửa");
		lblThmMichnhSa.setHorizontalAlignment(SwingConstants.CENTER);
		lblThmMichnhSa.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JLabel lblDanhSchHin = new JLabel("Danh sách hiện thời");
		lblDanhSchHin.setHorizontalAlignment(SwingConstants.CENTER);
		lblDanhSchHin.setVerticalAlignment(SwingConstants.BOTTOM);
		lblDanhSchHin.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_3.setBackground(Color.WHITE);
		
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
						.addComponent(lblThmMichnhSa, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
						.addComponent(panel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
						.addComponent(lblDanhSchHin, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblThmMichnhSa, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDanhSchHin, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
		);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{15, 300, 100, 150, 15, 0};
		gbl_panel_3.rowHeights = new int[]{25, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JLabel lblaBX = new JLabel(tm.getSummaryName());
		lblaBX.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblaBX = new GridBagConstraints();
		gbc_lblaBX.anchor = GridBagConstraints.SOUTH;
		gbc_lblaBX.insets = new Insets(0, 0, 5, 5);
		gbc_lblaBX.gridx = 1;
		gbc_lblaBX.gridy = 0;
		panel_3.add(lblaBX, gbc_lblaBX);
		
		JLabel lblTrV = new JLabel("Trả về");
		lblTrV.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTrV = new GridBagConstraints();
		gbc_lblTrV.anchor = GridBagConstraints.SOUTH;
		gbc_lblTrV.insets = new Insets(0, 0, 5, 5);
		gbc_lblTrV.gridx = 2;
		gbc_lblTrV.gridy = 0;
		panel_3.add(lblTrV, gbc_lblTrV);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.LIGHT_GRAY);
		separator_1.setPreferredSize(new Dimension(0, 1));
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 3;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 1;
		gbc_separator_1.gridy = 1;
		panel_3.add(separator_1, gbc_separator_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 2;
		panel_3.add(scrollPane, gbc_scrollPane);
		
		panel_current = new JPanel();
		panel_current.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel_current);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{25, 180, 120, 200, 25, 0};
		gbl_panel.rowHeights = new int[]{25, 0, 0, 25, 32, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblTnBin = new JLabel("Tên biến");
		lblTnBin.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTnBin = new GridBagConstraints();
		gbc_lblTnBin.anchor = GridBagConstraints.SOUTH;
		gbc_lblTnBin.insets = new Insets(0, 0, 5, 5);
		gbc_lblTnBin.gridx = 1;
		gbc_lblTnBin.gridy = 0;
		panel.add(lblTnBin, gbc_lblTnBin);
		
		JLabel lblKiu = new JLabel("Kiểu");
		lblKiu.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblKiu = new GridBagConstraints();
		gbc_lblKiu.anchor = GridBagConstraints.SOUTH;
		gbc_lblKiu.insets = new Insets(0, 0, 5, 5);
		gbc_lblKiu.gridx = 2;
		gbc_lblKiu.gridy = 0;
		panel.add(lblKiu, gbc_lblKiu);
		
		JLabel lblGiTr = new JLabel("Giá trị");
		lblGiTr.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblGiTr = new GridBagConstraints();
		gbc_lblGiTr.anchor = GridBagConstraints.SOUTH;
		gbc_lblGiTr.insets = new Insets(0, 0, 5, 5);
		gbc_lblGiTr.gridx = 3;
		gbc_lblGiTr.gridy = 0;
		panel.add(lblGiTr, gbc_lblGiTr);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		separator.setPreferredSize(new Dimension(0, 1));
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 3;
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 1;
		gbc_separator.gridy = 1;
		panel.add(separator, gbc_separator);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBorder(null);
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridwidth = 3;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 1;
		gbc_scrollPane_2.gridy = 2;
		panel.add(scrollPane_2, gbc_scrollPane_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		scrollPane_2.setViewportView(panel_2);
		
		/*----------------------------------------------*/
		int len = (mParas = tm.getFunction().getParameters()).length;
		
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{180, 120, 200, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		
		gbl_panel_2.rowHeights = new int[len+1];
		Arrays.fill(gbl_panel_2.rowHeights, 0, len, 25);
		gbl_panel_2.rowWeights = new double[len+1];
		gbl_panel_2.rowWeights[len] = Double.MIN_VALUE;
		
		panel_2.setLayout(gbl_panel_2);
		list_value = new JTextField[len];
		
		for (int i = 0; i < len; i++){
			Variable v =  mParas[i];
			
			JLabel lblX = new JLabel(v.getName());
			GridBagConstraints gbc_lblX = new GridBagConstraints();
			gbc_lblX.insets = new Insets(0, 0, 5, 5);
			gbc_lblX.gridx = 0;
			gbc_lblX.gridy = i;
			panel_2.add(lblX, gbc_lblX);
			
			JLabel lblInt_1 = new JLabel(v.getType().getContent());
			GridBagConstraints gbc_lblInt_1 = new GridBagConstraints();
			gbc_lblInt_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblInt_1.gridx = 1;
			gbc_lblInt_1.gridy = i;
			panel_2.add(lblInt_1, gbc_lblInt_1);
			
			JTextField textField = new JTextField();
			textField.setHorizontalAlignment(SwingConstants.CENTER);
			textField.setBorder(null);
			textField.setBackground(BG_INPUT);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.insets = new Insets(0, 0, 5, 0);
			gbc_textField.gridx = 2;
			gbc_textField.gridy = i;
			panel_2.add(textField, gbc_textField);
			list_value[i] = textField;
		}
		
		/*----------------------------------------------*/
		
		JLabel lblGiTrTr = new JLabel("Giá trị trả về");
		lblGiTrTr.setFont(new Font("Tahoma", Font.ITALIC, 12));
		GridBagConstraints gbc_lblGiTrTr = new GridBagConstraints();
		gbc_lblGiTrTr.insets = new Insets(0, 0, 5, 5);
		gbc_lblGiTrTr.gridx = 1;
		gbc_lblGiTrTr.gridy = 3;
		panel.add(lblGiTrTr, gbc_lblGiTrTr);
		
		rtnType = tm.getFunction().getReturnType();
		JLabel lblInt = new JLabel(rtnType.getContent());
		lblInt.setFont(new Font("Tahoma", Font.ITALIC, 12));
		GridBagConstraints gbc_lblInt = new GridBagConstraints();
		gbc_lblInt.insets = new Insets(0, 0, 5, 5);
		gbc_lblInt.gridx = 2;
		gbc_lblInt.gridy = 3;
		panel.add(lblInt, gbc_lblInt);
		
		txt_return_value = new JTextField();
		txt_return_value.setFont(new Font("Tahoma", Font.ITALIC, 12));
		txt_return_value.setBorder(null);
		txt_return_value.setHorizontalAlignment(SwingConstants.CENTER);
		txt_return_value.setBackground(BG_INPUT);
		GridBagConstraints gbc_txt_return_value = new GridBagConstraints();
		gbc_txt_return_value.insets = new Insets(0, 0, 5, 5);
		gbc_txt_return_value.fill = GridBagConstraints.HORIZONTAL;
		gbc_txt_return_value.gridx = 3;
		gbc_txt_return_value.gridy = 3;
		panel.add(txt_return_value, gbc_txt_return_value);
		if (isVoid()){
			txt_return_value.setEnabled(false);
		}
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 3;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 4;
		panel.add(panel_1, gbc_panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btn_edit = new JButton("Chỉnh sửa");
		btn_edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					performEdit(currentEdit);
				} catch (Exception e1) {
					alert(e1.getMessage());
				}
			}
		});
		btn_edit.setEnabled(false);
		panel_1.add(btn_edit);
		
		JButton btnThmMi = new JButton("Thêm mới");
		btnThmMi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					addNewTestcase();
				} catch (Exception e1) {
					alert(e1.getMessage());
				}
			}
		});
		panel_1.add(btnThmMi);
		
		JButton btnXaB = new JButton("Xóa bỏ");
		btnXaB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JTextField t: list_value)
					t.setText(null);
				txt_return_value.setText(null);
				setCurrentEdit(-1);
				if (list_value.length > 0)
					list_value[0].requestFocusInWindow();
			}
		});
		panel_1.add(btnXaB);
		
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.WHITE);
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
			fl_buttonPane.setVgap(8);
			fl_buttonPane.setHgap(0);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Hoàn tất");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		updateByTestcaseManager(tm, false);

	    setSize(SCREEN.WIDTH * 3/5, SCREEN.HEIGHT * 4/5);
		int x = (SCREEN.WIDTH - getWidth())/2;
	    int y = (SCREEN.HEIGHT - getHeight())/2;
	    setLocation(x, y);
	}
	
	private Testcase generateTestcase() throws Exception{
		Variable[] inputs = new Variable[mParas.length];
		Expression rtnEp = null;
		
		for (int i = 0; i < inputs.length; i++){
			String txt = list_value[i].getText();
			Variable source = mParas[i];
			core.models.Type type = source.getType();
			
			if (type.isArrayType()){
				ArrayVariable array = new ArrayVariable(
						source.getName(), (ArrayType) type);
				inputs[i] = array;
				if (txt.isEmpty()) continue;
				core.models.Type dataType = array.getDataType();
				
				for (Entry<int[], String> entry: getValueMap(txt).entrySet()){
					array.setValueAt(parseExpression(entry.getValue(), dataType), 
									entry.getKey());
				}
			} else {
				if (txt.isEmpty()){
					throw new RuntimeException("Chưa nhập giá trị đầu vào: " 
							+ source.getName());
				}
				inputs[i] = new Variable(source.getName(), type, 
						parseExpression(txt, type));
			}
			
		}
		
		if (!isVoid()){
			String txt = txt_return_value.getText();
			if (txt.isEmpty()){
				throw new RuntimeException("Chưa nhập giá trị trả về");
			}
			rtnEp = parseExpression(txt, rtnType);
		}
		
		return new Testcase(inputs, rtnEp);
	}
	
	private void addNewTestcase() throws Exception {
		Testcase t = generateTestcase();
		
		if (tm.add(t)){
			updateByTestcaseManager(tm, true);
		} else {
			alert("Testcase "+ t.getSummaryInput() +" đã có sẵn");
		}
	}

	private void updateByTestcaseManager(TestcaseManager tm, boolean repaint) {
		setCurrentEdit(-1);
		panel_current.removeAll();
		int len = tm.size();
		
		gbl_current = new GridBagLayout();
		gbl_current.columnWidths = new int[]{300, 100, 50, 25, 25, 50, 0};
		gbl_current.columnWeights = new double[]{1, 0, 0, 0, 0, 0, Double.MIN_VALUE};
		
		gbl_current.rowHeights = new int[len+1];
		Arrays.fill(gbl_current.rowHeights, 0, len, 25);
		gbl_current.rowWeights = new double[len+1];
		gbl_current.rowWeights[len] = Double.MIN_VALUE;
		panel_current.setLayout(gbl_current);
		
		for (int i = 0; i < len; i++){
			Testcase t = tm.get(i);
			int x = i;
			
			JLabel label = new JLabel(t.getSummaryInput());
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = i;
			panel_current.add(label, gbc_label);

			JLabel label_1 = new JLabel(t.getReturnOutput().getContent());
			GridBagConstraints gbc_label_1 = new GridBagConstraints();
			gbc_label_1.insets = new Insets(0, 0, 5, 5);
			gbc_label_1.gridx = 1;
			gbc_label_1.gridy = i;
			panel_current.add(label_1, gbc_label_1);
			
			JButton btn_edit = new JButton("");
			btn_edit.setToolTipText("Chỉnh sửa");
			btn_edit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editPosition(x);
				}
			});
			btn_edit.setIcon(new ImageIcon(
					getClass().getResource("/image/edit.png")));
			btn_edit.setBorder(null);
			GridBagConstraints gbc_edit = new GridBagConstraints();
			gbc_edit.insets = new Insets(0, 0, 5, 5);
			gbc_edit.gridx = 3;
			gbc_edit.gridy = i;
			panel_current.add(btn_edit, gbc_edit);
			
			JButton btn_delete = new JButton("");
			btn_delete.setToolTipText("Xoá");
			btn_delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deletePosition(x);
				}
			});
			btn_delete.setIcon(new ImageIcon(
					getClass().getResource("/image/delete.png")));
			btn_delete.setBorder(null);
			GridBagConstraints gbc_delete = new GridBagConstraints();
			gbc_delete.insets = new Insets(0, 0, 5, 5);
			gbc_delete.gridx = 4;
			gbc_delete.gridy = i;
			panel_current.add(btn_delete, gbc_delete);
		}
		
		if (repaint){
			revalidate();
			repaint();
		}
	}
	
	private void editPosition(int position){
		Testcase t = tm.get(position);
		Variable[] inputs = t.getInputs();
		
		for (int i = 0; i < inputs.length; i++){
			if (inputs[i] instanceof ArrayVariable){
				String value = "";
				HashMap<int[], Expression> valueMap = 
						((ArrayVariable)inputs[i]).getAllValue();
				
				for (int[] indexs: valueMap.keySet()){
					value += ", ";
					for (int index: indexs)
						value +=index + " ";
					value += "=> " + valueMap.get(indexs);
				}
				if (!value.isEmpty())
					value = value.substring(2);
				list_value[i].setText(value);
			} else {
				list_value[i].setText(inputs[i].getValue().getContent());
			}
		}
		if (!isVoid())
			txt_return_value.setText(t.getReturnOutput().getContent());
		
		if (list_value.length > 0){
			list_value[0].requestFocusInWindow();
			list_value[0].selectAll();
		}
		setCurrentEdit(position);
	}
	
	private void performEdit(int position) throws Exception{
		tm.set(currentEdit, generateTestcase());
		updateByTestcaseManager(tm, true);
	}
	
	private void setCurrentEdit(int position){
		if (currentEdit != -1)
			setRowColor(currentEdit, null);
		currentEdit = position;
		
		btn_edit.setEnabled(position >= 0);
		if (position >= 0)
		setRowColor(currentEdit, Color.RED);
	}
	
	private void setRowColor(int row, Color color){
		Component[] c = panel_current.getComponents();
		c[4*row].setForeground(color);
		c[4*row+1].setForeground(color);
	}

	private void deletePosition(int position){
		tm.remove(position);
		updateByTestcaseManager(tm, true);
	}
	
	/**
	 * Trả về ánh xạ giữa chỉ số và chuỗi giá trị của phần tử
	 */
	private static LinkedHashMap<int[], String> getValueMap(String arrayValue){
		LinkedHashMap<int[], String> map = new LinkedHashMap<>();
		for (String elm: arrayValue.split(", ?")){
			String[] part = elm.split(" ?=> ?");
			String[] index = part[0].split(" ");
			int[] indexes = new int[index.length];
			
			for (int i = 0; i < index.length; i++)
				indexes[i] = Integer.valueOf(index[i]);
			map.put(indexes, part[1]);
		}
		return map;
	}
	
	private static Expression parseExpression(String content, core.models.Type type)
			throws CoreException{
		IDExpression r = new IDExpression(content, 
				Utils.basicTypeToFlag((BasicType) type));
		
		if (r.getType() != type)
			throw new CoreException("\"%s\" không phải là định dạng kiểu %s",
					content, type);
		return r;
	}
	
	private void alert(String error){
		javax.swing.JOptionPane.showMessageDialog(getContentPane(), error, 
				"Lỗi", JOptionPane.ERROR_MESSAGE);
	}

	private static Color BG_INPUT = new Color(250, 250, 250);
	private JPanel panel_current;
	private JButton btn_edit;
}
