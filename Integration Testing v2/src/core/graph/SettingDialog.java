package core.graph;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

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

import core.S;

public class SettingDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel entry_z3_dir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			S.load();
			SettingDialog dialog = new SettingDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadSettings(){
		entry_z3_dir.setText(S.Z3_BIN_DIR);
	}
	
	private void applySettings(){
		S.Z3_BIN_DIR = entry_z3_dir.getText();
		S.save();
	}

	/**
	 * Create the dialog.
	 */
	public SettingDialog(JFrame parent) {
		super(parent, null, Dialog.ModalityType.DOCUMENT_MODAL);
		setTitle("Cài đặt");
		
		setBounds(100, 100, 700, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
		);
		{
			JScrollPane scrollPane = new JScrollPane();
			tabbedPane.addTab("Cài đặt chung", null, scrollPane, null);
			{
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				scrollPane.setViewportView(panel);
			}
		}
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Giải hệ ràng buộc", null, scrollPane, null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{30, 107, 217, 87, 30, 0};
		gbl_panel.rowHeights = new int[]{23, 35, 35, 35, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
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
		
		JRadioButton rdbtnZ = new JRadioButton("Z3");
		rdbtnZ.setSelected(true);
		panel_1.add(rdbtnZ);
		
		JRadioButton rdbtnRandom = new JRadioButton("Random");
		panel_1.add(rdbtnRandom);
		contentPanel.setLayout(gl_contentPanel);
		
		ButtonGroup group_solver = new ButtonGroup();
		group_solver.add(rdbtnZ);
		group_solver.add(rdbtnRandom);
		
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
		gbc_lblngDn.insets = new Insets(0, 0, 0, 5);
		gbc_lblngDn.gridx = 1;
		gbc_lblngDn.gridy = 3;
		panel.add(lblngDn, gbc_lblngDn);
		
		entry_z3_dir = new JLabel("");
		GridBagConstraints gbc_entry_z3_dir = new GridBagConstraints();
		gbc_entry_z3_dir.anchor = GridBagConstraints.WEST;
		gbc_entry_z3_dir.fill = GridBagConstraints.VERTICAL;
		gbc_entry_z3_dir.insets = new Insets(0, 0, 0, 5);
		gbc_entry_z3_dir.gridx = 2;
		gbc_entry_z3_dir.gridy = 3;
		panel.add(entry_z3_dir, gbc_entry_z3_dir);
		
		JButton btnChn = new JButton("Chọn...");
		btnChn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(S.Z3_BIN_DIR);
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
		gbc_btnChn.insets = new Insets(0, 0, 0, 5);
		gbc_btnChn.gridx = 3;
		gbc_btnChn.gridy = 3;
		panel.add(btnChn, gbc_btnChn);
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
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
	    setLocation(x, y);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			loadSettings();
		}
		super.setVisible(b);
	}
	
	
}
