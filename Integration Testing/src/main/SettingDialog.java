package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import core.BaseProject;
import core.Config;
import graph.swing.SelectList;
import java.awt.Font;

public class SettingDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JTabbedPane tabPanel = new JTabbedPane(JTabbedPane.TOP);
	private JSpinner entry_max_loop;
	private SelectList<String> entry_solver_order;
	private JLabel entry_z3_dir;
	private JSpinner entry_rand_loop;
	private JSpinner entry_rand_min;
	private JSpinner entry_rand_max;
	
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
	
	private void loadSettings() {
		entry_max_loop.setValue(Config.MAX_LOOP_TEST);
		entry_solver_order.setModel(BaseProject.BASE_LIST_SOLVER, Config.LIST_SOLVER);
		
		entry_z3_dir.setText(Config.DIR_Z3_BIN.getAbsolutePath());
		entry_z3_dir.setToolTipText(Config.DIR_Z3_BIN.getAbsolutePath());
		
		entry_rand_loop.setValue(Config.RAND_LOOP);
		entry_rand_min.setValue(Config.RAND_MIN);
		entry_rand_max.setValue(Config.RAND_MAX);
	}
	
	private void validateSettings() throws Exception {
		{
			int min = (int) entry_rand_min.getValue();
			int max = (int) entry_rand_max.getValue();
			if (min > max)
				throw new Exception(String.format("Illegal random range: [%d, %d]", min, max));
		}
	}
	
	private void applySettings(){
		Config.MAX_LOOP_TEST = (int) entry_max_loop.getValue();
		Config.LIST_SOLVER = entry_solver_order.getSelectList()
				.toArray(new String[0]);

		Config.DIR_Z3_BIN = new File(entry_z3_dir.getText());
		
		Config.RAND_LOOP = (int) entry_rand_loop.getValue();
		Config.RAND_MIN = (int) entry_rand_min.getValue();
		Config.RAND_MAX = (int) entry_rand_max.getValue();
		Config.save();
	}

	/**
	 * Create the dialog.
	 */
	public SettingDialog(Frame owner) {
		super(owner, "Settings", true);
		
		ArrayList<JComponent> group_z3 = new ArrayList<>(),
				group_rand = new ArrayList<>();
		
		setBounds(100, 100, 600, 450);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			tabPanel.addTab("General", null, scrollPane, null);
			{
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				scrollPane.setViewportView(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{30, 123, 0, 30, 0};
				gbl_panel.rowHeights = new int[]{30, 30, 0};
				gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					JLabel lblMaxLoop = new JLabel("Max loop");
					GridBagConstraints gbc_lblMaxLoop = new GridBagConstraints();
					gbc_lblMaxLoop.anchor = GridBagConstraints.WEST;
					gbc_lblMaxLoop.insets = new Insets(0, 0, 0, 5);
					gbc_lblMaxLoop.gridx = 1;
					gbc_lblMaxLoop.gridy = 1;
					panel.add(lblMaxLoop, gbc_lblMaxLoop);
				}
				{
					entry_max_loop = new JSpinner();
					entry_max_loop.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
					
					resizeComponentWidth(entry_max_loop, 50);
					
					GridBagConstraints gbc_entry_max_loop = new GridBagConstraints();
					gbc_entry_max_loop.anchor = GridBagConstraints.WEST;
					gbc_entry_max_loop.insets = new Insets(0, 0, 0, 5);
					gbc_entry_max_loop.gridx = 2;
					gbc_entry_max_loop.gridy = 1;
					panel.add(entry_max_loop, gbc_entry_max_loop);
				}
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			tabPanel.addTab("Solver", null, scrollPane, null);
			{
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				scrollPane.setViewportView(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{30, 120, 0, 0, 30, 0};
				gbl_panel.rowHeights = new int[]{30, 30, 48, 30, 48, 30, 30, 0};
				gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					JLabel lblSolverOrder = new JLabel("Solver order");
					GridBagConstraints gbc_lblSolverOrder = new GridBagConstraints();
					gbc_lblSolverOrder.anchor = GridBagConstraints.WEST;
					gbc_lblSolverOrder.insets = new Insets(0, 0, 5, 5);
					gbc_lblSolverOrder.gridx = 1;
					gbc_lblSolverOrder.gridy = 1;
					panel.add(lblSolverOrder, gbc_lblSolverOrder);
				}
				{
					entry_solver_order = new SelectList<String>();
					entry_solver_order.setHeight(30);
					GridBagConstraints gbc_entry_solver_order = new GridBagConstraints();
					gbc_entry_solver_order.anchor = GridBagConstraints.WEST;
					gbc_entry_solver_order.insets = new Insets(0, 0, 5, 5);
					gbc_entry_solver_order.gridx = 2;
					gbc_entry_solver_order.gridy = 1;
					panel.add(entry_solver_order, gbc_entry_solver_order);
					
					entry_solver_order.setItemStateChangeListener((item, enable) -> {
						ArrayList<JComponent> group = null;
						
						if (item.equals("Z3"))
							group = group_z3;
						else
							group = group_rand;
						
						group.forEach(c -> c.setEnabled(enable));
					});
				}
				{
					JLabel lblZSolver = new JLabel("Z3 Solver");
					lblZSolver.setFont(lblZSolver.getFont().deriveFont(lblZSolver.getFont().getStyle() | Font.BOLD, lblZSolver.getFont().getSize() + 1f));
					GridBagConstraints gbc_lblZSolver = new GridBagConstraints();
					gbc_lblZSolver.anchor = GridBagConstraints.SOUTHWEST;
					gbc_lblZSolver.insets = new Insets(0, 0, 5, 5);
					gbc_lblZSolver.gridx = 1;
					gbc_lblZSolver.gridy = 2;
					panel.add(lblZSolver, gbc_lblZSolver);
				}
				{
					JLabel lblPath = new JLabel("Path");
					GridBagConstraints gbc_lblPath = new GridBagConstraints();
					gbc_lblPath.anchor = GridBagConstraints.WEST;
					gbc_lblPath.insets = new Insets(0, 0, 5, 5);
					gbc_lblPath.gridx = 1;
					gbc_lblPath.gridy = 3;
					panel.add(lblPath, gbc_lblPath);
				}
				{
					entry_z3_dir = new JLabel("");
					entry_z3_dir.setPreferredSize(new Dimension(0, 0));
					GridBagConstraints gbc_entry_z3_dir = new GridBagConstraints();
					gbc_entry_z3_dir.fill = GridBagConstraints.BOTH;
					gbc_entry_z3_dir.insets = new Insets(0, 0, 5, 5);
					gbc_entry_z3_dir.gridx = 2;
					gbc_entry_z3_dir.gridy = 3;
					panel.add(entry_z3_dir, gbc_entry_z3_dir);
					group_z3.add(entry_z3_dir);
				}
				{
					JButton btnSelect = new JButton("Select...");
					btnSelect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JFileChooser chooser = new JFileChooser(Config.DIR_Z3_BIN);
				            chooser.setFileFilter(new FileFilter() {
				                @Override
				                public String getDescription() {
				                    return "Execution file (z3.exe)";
				                }

				                @Override
				                public boolean accept(File f) {
				                    return f.isDirectory() ||
				                            f.getName().equals("z3.exe") && f.getParentFile().getName().equals("bin");
				                }
				            });

				            int status = chooser.showDialog(getContentPane(), "Choose file");
				            if (status == JFileChooser.APPROVE_OPTION) {
				                entry_z3_dir.setText(chooser.getSelectedFile().getParent());
				            }
						}
					});
					GridBagConstraints gbc_btnSelect = new GridBagConstraints();
					gbc_btnSelect.insets = new Insets(0, 0, 5, 5);
					gbc_btnSelect.gridx = 3;
					gbc_btnSelect.gridy = 3;
					panel.add(btnSelect, gbc_btnSelect);
					group_z3.add(btnSelect);
				}
				{
					JLabel lblRandomSolver = new JLabel("Random Solver");
					lblRandomSolver.setFont(lblRandomSolver.getFont().deriveFont(lblRandomSolver.getFont().getStyle() | Font.BOLD, lblRandomSolver.getFont().getSize() + 1f));
					GridBagConstraints gbc_lblRandomSolver = new GridBagConstraints();
					gbc_lblRandomSolver.anchor = GridBagConstraints.SOUTHWEST;
					gbc_lblRandomSolver.insets = new Insets(0, 0, 5, 5);
					gbc_lblRandomSolver.gridx = 1;
					gbc_lblRandomSolver.gridy = 4;
					panel.add(lblRandomSolver, gbc_lblRandomSolver);
				}
				{
					JLabel lblMaxLoop_1 = new JLabel("Max iter count");
					GridBagConstraints gbc_lblMaxLoop_1 = new GridBagConstraints();
					gbc_lblMaxLoop_1.anchor = GridBagConstraints.WEST;
					gbc_lblMaxLoop_1.insets = new Insets(0, 0, 5, 5);
					gbc_lblMaxLoop_1.gridx = 1;
					gbc_lblMaxLoop_1.gridy = 5;
					panel.add(lblMaxLoop_1, gbc_lblMaxLoop_1);
				}
				{
					entry_rand_loop = new JSpinner();
					resizeComponentWidth(entry_rand_loop, 100);
					entry_rand_loop.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(100)));
					GridBagConstraints gbc_entry_random_loop = new GridBagConstraints();
					gbc_entry_random_loop.anchor = GridBagConstraints.WEST;
					gbc_entry_random_loop.insets = new Insets(0, 0, 5, 5);
					gbc_entry_random_loop.gridx = 2;
					gbc_entry_random_loop.gridy = 5;
					panel.add(entry_rand_loop, gbc_entry_random_loop);
					group_rand.add(entry_rand_loop);
				}
				{
					JLabel lblRange = new JLabel("Range");
					GridBagConstraints gbc_lblRange = new GridBagConstraints();
					gbc_lblRange.anchor = GridBagConstraints.WEST;
					gbc_lblRange.insets = new Insets(0, 0, 0, 5);
					gbc_lblRange.gridx = 1;
					gbc_lblRange.gridy = 6;
					panel.add(lblRange, gbc_lblRange);
				}
				{
					JPanel panel_1 = new JPanel();
					panel_1.setOpaque(false);
					GridBagConstraints gbc_panel_1 = new GridBagConstraints();
					gbc_panel_1.insets = new Insets(0, 0, 0, 5);
					gbc_panel_1.fill = GridBagConstraints.BOTH;
					gbc_panel_1.gridx = 2;
					gbc_panel_1.gridy = 6;
					panel.add(panel_1, gbc_panel_1);
					panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
					{
						JLabel lblmin = new JLabel("[Min: ");
						panel_1.add(lblmin);
					}
					{
						entry_rand_min = new JSpinner();
						resizeComponentWidth(entry_rand_min, 72);
						entry_rand_min.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(10)));
						panel_1.add(entry_rand_min);
						group_rand.add(entry_rand_min);
					}
					{
						JLabel lblMax = new JLabel(", Max: ");
						panel_1.add(lblMax);
					}
					{
						entry_rand_max = new JSpinner();
						resizeComponentWidth(entry_rand_max, 72);
						entry_rand_max.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(10)));
						panel_1.add(entry_rand_max);
						group_rand.add(entry_rand_max);
					}
					{
						JLabel label = new JLabel("]");
						panel_1.add(label);
					}
				}
			}
		}
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							validateSettings();
							applySettings();
							dispose();
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(SettingDialog.this, e1.getMessage(), 
									"Validating error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(
				(size.width - getWidth())/2, 
				(size.height - getHeight())/2
		);
	}


	@Override
	public void setVisible(boolean b) {
		if (b){
			try{
				loadSettings();
			} catch (Exception e){
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
		super.setVisible(b);
	}

	private void resizeComponentWidth(Component c, int width){
		Dimension d = c.getPreferredSize();
		d.width = width;
		c.setPreferredSize(d);
	}

}
