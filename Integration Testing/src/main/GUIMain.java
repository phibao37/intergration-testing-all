package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import cdt.CProject;
import cdt.models.CProjectNode;
import graph.swing.ProjectExplorer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class GUIMain {

	private JFrame frmCProjectTesting;
	private JScrollPane scroll_project_tree;
	
	private JFileChooser chooserProject;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
							 "javax.swing.plaf.nimbus.NimbusLookAndFeel");
					
					GUIMain window = new GUIMain();
					window.frmCProjectTesting.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void init2(){
		chooserProject = new JFileChooser();
		chooserProject.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	void openProjectFolder(){
		int result = chooserProject.showDialog(frmCProjectTesting, "Open project folder");
		
		if (result == JFileChooser.APPROVE_OPTION){
			File root = chooserProject.getSelectedFile();
			
			CProject project = new CProject(root);
			project.loadProject();
			
			CProjectNode rootNode = new CProjectNode(root, 
					CProjectNode.TYPE_PROJECT, project); 
			
			ProjectExplorer tree_project = new ProjectExplorer(rootNode);
			scroll_project_tree.setViewportView(tree_project);
		}
	}
	
	/**
	 * Create the application.
	 */
	public GUIMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Dimension sc = Toolkit.getDefaultToolkit().getScreenSize();
		int margin = 50;
		
		frmCProjectTesting = new JFrame();
		frmCProjectTesting.setTitle("C Project Testing");
		frmCProjectTesting.setBounds(margin, margin, sc.width - 2*margin, sc.height - 2*margin);
		frmCProjectTesting.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCProjectTesting.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel panel = new JPanel();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(4);
		splitPane.setBorder(null);
		GroupLayout groupLayout = new GroupLayout(frmCProjectTesting.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 1354, Short.MAX_VALUE)
				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 1354, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE))
		);
		
		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		FlowLayout fl_toolBar = new FlowLayout(FlowLayout.CENTER);
		fl_toolBar.setVgap(0);
		toolBar.setLayout(fl_toolBar);
		
		scroll_project_tree = new JScrollPane();
		scroll_project_tree.setBorder(null);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(scroll_project_tree, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
						.addComponent(toolBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addComponent(scroll_project_tree, GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		scroll_project_tree.setViewportView(panel_3);
		
		JButton button = new JButton("");
		button.setToolTipText("Refresh project");
		button.setIcon(new ImageIcon(GUIMain.class.getResource("/image/refresh.png")));
		toolBar.add(button);
		
		JToggleButton toggleButton = new JToggleButton("");
		toggleButton.setToolTipText("Filter source file");
		toggleButton.setIcon(new ImageIcon(GUIMain.class.getResource("/image/filter.png")));
		toolBar.add(toggleButton);
		
		JToggleButton toggleButton_1 = new JToggleButton("");
		toggleButton_1.setToolTipText("Pin project");
		toggleButton_1.setIcon(new ImageIcon(GUIMain.class.getResource("/image/pin.png")));
		toolBar.add(toggleButton_1);
		panel_1.setLayout(gl_panel_1);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerSize(4);
		splitPane.setRightComponent(splitPane_1);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane_1.setLeftComponent(tabbedPane);
		
		JPanel panel_2 = new JPanel();
		splitPane_1.setRightComponent(panel_2);
		splitPane_1.setDividerLocation(500);
		splitPane.setDividerLocation(300);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{70, 80, 80, 87, 80, 80, 70, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openProjectFolder();
			}
		});
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.insets = new Insets(0, 0, 0, 5);
		gbc_btnOpen.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpen.gridx = 1;
		gbc_btnOpen.gridy = 0;
		panel.add(btnOpen, gbc_btnOpen);
		
		JButton btnExport = new JButton("Export");
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.insets = new Insets(0, 0, 0, 5);
		gbc_btnExport.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnExport.gridx = 2;
		gbc_btnExport.gridy = 0;
		panel.add(btnExport, gbc_btnExport);
		
		JButton btnTest = new JButton("Test");
		btnTest.setIcon(new ImageIcon(GUIMain.class.getResource("/image/run-test.png")));
		btnTest.setPreferredSize(new Dimension(120, 40));
		btnTest.setFont(btnTest.getFont().deriveFont(18f));
		GridBagConstraints gbc_btnTest = new GridBagConstraints();
		gbc_btnTest.insets = new Insets(0, 0, 0, 5);
		gbc_btnTest.gridx = 3;
		gbc_btnTest.gridy = 0;
		panel.add(btnTest, gbc_btnTest);
		
		JButton btnSetting = new JButton("Setting");
		GridBagConstraints gbc_btnSetting = new GridBagConstraints();
		gbc_btnSetting.insets = new Insets(0, 0, 0, 5);
		gbc_btnSetting.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSetting.gridx = 4;
		gbc_btnSetting.gridy = 0;
		panel.add(btnSetting, gbc_btnSetting);
		
		JButton btnAbout = new JButton("About");
		GridBagConstraints gbc_btnAbout = new GridBagConstraints();
		gbc_btnAbout.insets = new Insets(0, 0, 0, 5);
		gbc_btnAbout.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAbout.gridx = 5;
		gbc_btnAbout.gridy = 0;
		panel.add(btnAbout, gbc_btnAbout);
		frmCProjectTesting.getContentPane().setLayout(groupLayout);
		
		init2();
	}
}
