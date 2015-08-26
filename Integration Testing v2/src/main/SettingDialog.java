package main;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;

import java.awt.Color;

import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.io.File;
import java.util.Enumeration;

import core.S;
import core.Utils;
import core.graph.GQuery;
import core.graph.GQuery.Filter;
import core.solver.Solver;
import core.S.SCREEN;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JCheckBox;

public class SettingDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel entry_z3_dir;
	private JSpinner spinner_max_loop;
	private JLabel entry_tmp_dir;
	private JLabel entry_tmp_size;
	private JRadioButton entry_solver_z3;
	private ButtonGroup group_solver;
	private JSpinner entry_rand_loop;
	private JSpinner entry_rand_min;
	private JSpinner entry_rand_max;
	private JTabbedPane tabbed_main;
	
	private GQuery $Solver;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingDialog dialog = new SettingDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadSettings(){
		spinner_max_loop.setValue(S.MAX_LOOP_TEST);
		entry_z3_dir.setText(S.DIR_Z3_BIN.getAbsolutePath());
		entry_tmp_dir.setText(S.DIR_TEMP.getPath());
		if (S.DIR_TEMP.isDirectory()){
			setTmpSize(Utils.getFileSize(S.DIR_TEMP));
		}
		
		String solver = S.SOLVER.toString();
		Enumeration<AbstractButton> iter = group_solver.getElements();
		while (iter.hasMoreElements()){
			AbstractButton bt = iter.nextElement();
			
			if (bt.getActionCommand().equals(solver)){
				bt.doClick();
				//group_solver.setSelected(bt.getModel(), true);
				break;
			}
		}
		
		entry_rand_loop.setValue(S.RAND_LOOP);
		entry_rand_min.setValue(S.RAND_MIN);
		entry_rand_max.setValue(S.RAND_MAX);
		
		entry_show_canvas_toolbar.setSelected(S.CANVAS_SHOW_TOOLBAR);
		entry_draw_topdown.setSelected(S.CANVAS_DRAW_TOPDOWN);
	}
	
	private void applySettings(){
		S.MAX_LOOP_TEST = (int) spinner_max_loop.getValue();
		
		S.DIR_Z3_BIN = new File(entry_z3_dir.getText());
		S.DIR_TEMP = new File(entry_tmp_dir.getText());
		S.SOLVER = Solver.valueOf(group_solver.getSelection().getActionCommand());
		S.RAND_LOOP = (int) entry_rand_loop.getValue();
		S.RAND_MIN = (int) entry_rand_min.getValue();
		S.RAND_MAX = (int) entry_rand_max.getValue();
		
		S.CANVAS_SHOW_TOOLBAR = entry_show_canvas_toolbar.isSelected();
		S.CANVAS_DRAW_TOPDOWN = entry_draw_topdown.isSelected();
		
		S.save();
	}
	
	private void setTmpSize(long size){
		entry_tmp_size.setText(String.format("Kích thước: %d Kb", size/1024));
	}

	/**
	 * Create the dialog.
	 */
	public SettingDialog(JFrame parent) {
		super(parent, null, Dialog.ModalityType.DOCUMENT_MODAL);
		setIconImage(Toolkit.getDefaultToolkit().getImage(SettingDialog.class.getResource("/image/setting.png")));
		setTitle("Cài đặt");
		
		setBounds(100, 100, 700, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		tabbed_main = new JTabbedPane(JTabbedPane.TOP);
		tabbed_main.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (isVisible())
					LAST_INDEX = tabbed_main.getSelectedIndex();
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbed_main, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbed_main, GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
		);
		{
			JScrollPane scrollPane = new JScrollPane();
			tabbed_main.addTab("Cài đặt chung", null, scrollPane, null);
			{
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				scrollPane.setViewportView(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{30, 127, 384, 73, 30, 0};
				gbl_panel.rowHeights = new int[]{30, 30, 45, 30, 0};
				gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				
				JLabel lblThMcTm = new JLabel("Thư mục tạm thời");
				GridBagConstraints gbc_lblThMcTm = new GridBagConstraints();
				gbc_lblThMcTm.anchor = GridBagConstraints.SOUTHWEST;
				gbc_lblThMcTm.insets = new Insets(0, 0, 5, 5);
				gbc_lblThMcTm.gridx = 1;
				gbc_lblThMcTm.gridy = 1;
				panel.add(lblThMcTm, gbc_lblThMcTm);
				
				entry_tmp_dir = new JLabel("");
				entry_tmp_dir.setPreferredSize(new Dimension(384, 0));
				entry_tmp_dir.setVerticalAlignment(SwingConstants.BOTTOM);
				entry_tmp_dir.setHorizontalAlignment(SwingConstants.LEFT);
				GridBagConstraints gbc_entry_tmp_dir = new GridBagConstraints();
				gbc_entry_tmp_dir.fill = GridBagConstraints.BOTH;
				gbc_entry_tmp_dir.insets = new Insets(0, 0, 5, 5);
				gbc_entry_tmp_dir.gridx = 2;
				gbc_entry_tmp_dir.gridy = 1;
				panel.add(entry_tmp_dir, gbc_entry_tmp_dir);
				
				JButton btnChn_1 = new JButton("Chọn...");
				btnChn_1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser chooser = new JFileChooser(S.DIR_TEMP);
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						int status = chooser.showDialog(getContentPane(), "Chọn thư mục");
						if (status == JFileChooser.APPROVE_OPTION) {
							entry_tmp_dir.setText(chooser.getSelectedFile().getPath());
						}
					}
				});
				GridBagConstraints gbc_btnChn_1 = new GridBagConstraints();
				gbc_btnChn_1.anchor = GridBagConstraints.SOUTHWEST;
				gbc_btnChn_1.insets = new Insets(0, 0, 5, 5);
				gbc_btnChn_1.gridx = 3;
				gbc_btnChn_1.gridy = 1;
				panel.add(btnChn_1, gbc_btnChn_1);
				
				entry_tmp_size = new JLabel("");
				GridBagConstraints gbc_entry_tmp_size = new GridBagConstraints();
				gbc_entry_tmp_size.anchor = GridBagConstraints.EAST;
				gbc_entry_tmp_size.insets = new Insets(0, 0, 5, 5);
				gbc_entry_tmp_size.gridx = 2;
				gbc_entry_tmp_size.gridy = 2;
				panel.add(entry_tmp_size, gbc_entry_tmp_size);
				
				JButton btnXa = new JButton("Xóa");
				btnXa.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (S.DIR_TEMP.isDirectory()){
							for (File f: S.DIR_TEMP.listFiles())
								Utils.deleteFile(f);
							setTmpSize(Utils.getFileSize(S.DIR_TEMP));
						}
					}
				});
				GridBagConstraints gbc_btnXa = new GridBagConstraints();
				gbc_btnXa.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnXa.insets = new Insets(0, 0, 5, 5);
				gbc_btnXa.gridx = 3;
				gbc_btnXa.gridy = 2;
				panel.add(btnXa, gbc_btnXa);
				
				JLabel lblSLnKim = new JLabel("Số lần kiểm thử lặp");
				GridBagConstraints gbc_lblSLnKim = new GridBagConstraints();
				gbc_lblSLnKim.anchor = GridBagConstraints.SOUTHWEST;
				gbc_lblSLnKim.insets = new Insets(0, 0, 0, 5);
				gbc_lblSLnKim.gridx = 1;
				gbc_lblSLnKim.gridy = 3;
				panel.add(lblSLnKim, gbc_lblSLnKim);
				
				spinner_max_loop = new JSpinner();
				spinner_max_loop.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				spinner_max_loop.setPreferredSize(new Dimension(40, 20));
				GridBagConstraints gbc_spinner_max_loop = new GridBagConstraints();
				gbc_spinner_max_loop.gridwidth = 2;
				gbc_spinner_max_loop.anchor = GridBagConstraints.SOUTHWEST;
				gbc_spinner_max_loop.insets = new Insets(0, 0, 0, 5);
				gbc_spinner_max_loop.gridx = 2;
				gbc_spinner_max_loop.gridy = 3;
				panel.add(spinner_max_loop, gbc_spinner_max_loop);
			}
		}
		
		JScrollPane scrollPane = new JScrollPane();
		tabbed_main.addTab("Giải hệ ràng buộc", null, scrollPane, null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{30, 107, 217, 87, 30, 0};
		gbl_panel.rowHeights = new int[]{23, 35, 35, 30, 37, 25, 25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		$Solver = GQuery.root(panel);
		
		JLabel lblBGii = new JLabel("Bộ giải");
		GridBagConstraints gbc_lblBGii = new GridBagConstraints();
		gbc_lblBGii.anchor = GridBagConstraints.LINE_START;
		gbc_lblBGii.insets = new Insets(0, 0, 5, 5);
		gbc_lblBGii.gridx = 1;
		gbc_lblBGii.gridy = 1;
		panel.add(lblBGii, gbc_lblBGii);
		
		JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panel.add(panel_1, gbc);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		entry_solver_z3 = new JRadioButton("Z3");
		entry_solver_z3.setSelected(true);
		panel_1.add(entry_solver_z3);
		contentPanel.setLayout(gl_contentPanel);
		
		group_solver = new ButtonGroup();
		group_solver.add(entry_solver_z3);
		entry_solver_z3.setActionCommand("Z3");
		
		JRadioButton entry_solver_random = new JRadioButton("Random");
		panel_1.add(entry_solver_random);
		group_solver.add(entry_solver_random);
		entry_solver_random.setActionCommand("Random");
		
		JLabel lblBGiiZ = new JLabel("Bộ giải Z3");
		lblBGiiZ.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblBGiiZ.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblBGiiZ = new GridBagConstraints();
		gbc_lblBGiiZ.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblBGiiZ.insets = new Insets(0, 0, 5, 5);
		gbc_lblBGiiZ.gridx = 1;
		gbc_lblBGiiZ.gridy = 2;
		panel.add(lblBGiiZ, gbc_lblBGiiZ);
		
		JLabel lblngDn = new JLabel("Đường dẫn");
		GridBagConstraints gbc_lblngDn = new GridBagConstraints();
		gbc_lblngDn.anchor = GridBagConstraints.LINE_START;
		gbc_lblngDn.insets = new Insets(0, 0, 5, 5);
		gbc_lblngDn.gridx = 1;
		gbc_lblngDn.gridy = 3;
		panel.add(lblngDn, gbc_lblngDn);
		
		entry_z3_dir = new JLabel("");
		entry_z3_dir.setPreferredSize(new Dimension(217, 0));
		GridBagConstraints gbc_entry_z3_dir = new GridBagConstraints();
		gbc_entry_z3_dir.fill = GridBagConstraints.BOTH;
		gbc_entry_z3_dir.insets = new Insets(0, 0, 5, 5);
		gbc_entry_z3_dir.gridx = 2;
		gbc_entry_z3_dir.gridy = 3;
		panel.add(entry_z3_dir, gbc_entry_z3_dir);
		
		JButton btnChn = new JButton("Chọn...");
		btnChn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(S.DIR_Z3_BIN);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Tập tin thực thi (z3.exe)";
					}
					
					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						return f.getName().equals("z3.exe")
								&& f.getParentFile().getName().equals("bin");
					}
				});

				int status = chooser.showDialog(getContentPane(), "Chọn tập tin");
				if (status == JFileChooser.APPROVE_OPTION) {
					entry_z3_dir.setText(chooser.getSelectedFile().getParent());
				}
			}
		});
		GridBagConstraints gbc_btnChn = new GridBagConstraints();
		gbc_btnChn.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnChn.insets = new Insets(0, 0, 5, 5);
		gbc_btnChn.gridx = 3;
		gbc_btnChn.gridy = 3;
		panel.add(btnChn, gbc_btnChn);
		
		GQuery.from(entry_z3_dir, btnChn).group("Z3");
		
		JLabel lblBGiiRandom = new JLabel("Bộ giải Random");
		lblBGiiRandom.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblBGiiRandom = new GridBagConstraints();
		gbc_lblBGiiRandom.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblBGiiRandom.insets = new Insets(0, 0, 5, 5);
		gbc_lblBGiiRandom.gridx = 1;
		gbc_lblBGiiRandom.gridy = 4;
		panel.add(lblBGiiRandom, gbc_lblBGiiRandom);
		
		JLabel lblSLnLp = new JLabel("Số lần lặp");
		GridBagConstraints gbc_lblSLnLp = new GridBagConstraints();
		gbc_lblSLnLp.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblSLnLp.insets = new Insets(0, 0, 5, 5);
		gbc_lblSLnLp.gridx = 1;
		gbc_lblSLnLp.gridy = 5;
		panel.add(lblSLnLp, gbc_lblSLnLp);
		
		entry_rand_loop = new JSpinner();
		entry_rand_loop.setModel(new SpinnerNumberModel(1, 1, 1000000, 100));
		GridBagConstraints gbc_entry_random_loop = new GridBagConstraints();
		gbc_entry_random_loop.anchor = GridBagConstraints.SOUTHWEST;
		gbc_entry_random_loop.insets = new Insets(0, 5, 5, 5);
		gbc_entry_random_loop.gridx = 2;
		gbc_entry_random_loop.gridy = 5;
		panel.add(entry_rand_loop, gbc_entry_random_loop);
		
		JLabel lblCnNguNhin = new JLabel("Cận ngẫu nhiên");
		GridBagConstraints gbc_lblCnNguNhin = new GridBagConstraints();
		gbc_lblCnNguNhin.anchor = GridBagConstraints.WEST;
		gbc_lblCnNguNhin.insets = new Insets(0, 0, 0, 5);
		gbc_lblCnNguNhin.gridx = 1;
		gbc_lblCnNguNhin.gridy = 6;
		panel.add(lblCnNguNhin, gbc_lblCnNguNhin);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_2.setBackground(Color.WHITE);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 2;
		gbc_panel_2.gridy = 6;
		panel.add(panel_2, gbc_panel_2);
		
		JLabel lblcnDi = new JLabel("[Cận dưới: ");
		panel_2.add(lblcnDi);
		
		entry_rand_min = new JSpinner();
		entry_rand_min.setPreferredSize(new Dimension(50, 20));
		entry_rand_min.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
		panel_2.add(entry_rand_min);
		
		JLabel lblCnTrn = new JLabel(",   Cận trên: ");
		panel_2.add(lblCnTrn);
		
		entry_rand_max = new JSpinner();
		entry_rand_max.setPreferredSize(new Dimension(50, 20));
		panel_2.add(entry_rand_max);
		
		JLabel label = new JLabel("]");
		panel_2.add(label);
		
		GQuery.from(entry_rand_loop, entry_rand_min, entry_rand_max).group("Random");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		tabbed_main.addTab("Cài đặt khác", null, scrollPane_1, null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		scrollPane_1.setViewportView(panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{30, 216, 100, 30, 0};
		gbl_panel_3.rowHeights = new int[]{25, 25, 25, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		entry_show_canvas_toolbar = new JCheckBox("Hiện toobar trong canvas");
		GridBagConstraints gbc_entry_show_canvas_toolbar = new GridBagConstraints();
		gbc_entry_show_canvas_toolbar.anchor = GridBagConstraints.WEST;
		gbc_entry_show_canvas_toolbar.insets = new Insets(0, 0, 5, 5);
		gbc_entry_show_canvas_toolbar.gridx = 1;
		gbc_entry_show_canvas_toolbar.gridy = 1;
		panel_3.add(entry_show_canvas_toolbar, gbc_entry_show_canvas_toolbar);
		
		entry_draw_topdown = new JCheckBox("Vẽ nút theo cấp độ cha con");
		GridBagConstraints gbc_entry_draw_topdown = new GridBagConstraints();
		gbc_entry_draw_topdown.anchor = GridBagConstraints.WEST;
		gbc_entry_draw_topdown.insets = new Insets(0, 0, 0, 5);
		gbc_entry_draw_topdown.gridx = 1;
		gbc_entry_draw_topdown.gridy = 2;
		panel_3.add(entry_draw_topdown, gbc_entry_draw_topdown);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						applySettings();
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Hủy bỏ");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		Enumeration<AbstractButton> iter = group_solver.getElements();
		while (iter.hasMoreElements()){
			iter.nextElement().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					$Solver.find("." + e.getActionCommand()).enabled(true);
					$Solver.find(new Filter(){

						@Override
						public boolean withGroup(String group) {
							return group != null && !group.equals(e.getActionCommand());
						}
						
					}).enabled(false);
				}
			});
		}
		
		int x = (SCREEN.WIDTH - getWidth())/2;
	    int y = (SCREEN.HEIGHT - getHeight())/2;
	    setLocation(x, y);
	}
	
	private static int LAST_INDEX = 0;
	private JCheckBox entry_show_canvas_toolbar;
	private JCheckBox entry_draw_topdown;

	@Override
	public void setVisible(boolean b) {
		if (b){
			try{
				loadSettings();
			} catch (Exception e){
				javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
		tabbed_main.setSelectedIndex(LAST_INDEX);
		super.setVisible(b);
	}
}
