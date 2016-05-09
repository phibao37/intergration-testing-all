package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import api.IProject;
import api.graph.IFileInfo;
import api.graph.IProjectNode;
import api.models.ITestpath;
import api.parser.IExporter;
import api.models.ICFG;
import api.models.IFunction;
import api.models.IFunctionTestResult;
import api.solver.ISolution;
import cdt.CProject;
import cdt.models.CProjectNode;
import core.Config;
import core.Utils;
import core.export.ExcelExporter;
import core.process.ProcessManager;
import core.process.TestProcess;
import graph.node.CFGNode;
import graph.swing.CFGView;
import graph.swing.FileView;
import graph.swing.LightTabbedPane;
import graph.swing.ProjectExplorer;
import graph.swing.tablelayout.TableLayout;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GUIMain {
	
	private static final ImageIcon 
			ICON_CLOSE = new ImageIcon(GUIMain.class.getResource(
					"/image/close.png")),
			ICON_DETAILS = new ImageIcon(GUIMain.class.getResource(
					"/image/details.png")),
			ICON_TEST = new ImageIcon(GUIMain.class.getResource(
					"/image/run-test-sm.png")),
			ICON_LOADING = new ImageIcon(GUIMain.class.getResource(
					"/image/loading.gif")),
			ICON_COMPLETE = new ImageIcon(GUIMain.class.getResource(
					"/image/complete.png"));
	
	private IProject currentProject;
	private IFunction currentFunction;
	private IExporter currentExport;
	private ProcessManager processMgr; 
	private TableLayout layout_process_mgr;
	private Hashtable<IFunction, TableLayout.TableRow> mapProcess;
	private SettingDialog settingDialog;
	
	private JFrame frmCProjectTesting;
	private JScrollPane scroll_project_tree;
	private ProjectExplorer tree_project;
	private JFileChooser chooserProject;
	private LightTabbedPane tab_source_view, tab_graph;
	private JLabel lblFunctionName;
	private JTable table_simple_result;

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
		File example = new File("example");
		if (example.exists()){
			chooserProject.setSelectedFile(example.getAbsoluteFile());
		}
		
		processMgr = new ProcessManager();
		mapProcess = new Hashtable<>();
		
		if (Config.PINNED_PROJECT != null){
			toggle_pin.setSelected(true);
			openProject(Config.PINNED_PROJECT);
		}
	}
	
	CFGView openCFGView(IFunction fn, int cover){
		try {
			CFGView v = (CFGView) tab_graph.openTab("CFG: " + fn.getName(), 
					null, fn.toString(),  CFGView.class.getConstructor(
							IFunction.class, int.class, MouseListener.class),
					fn, cover, cfgMouseAdapter);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	private MouseAdapter cfgMouseAdapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2){
				CFGNode node = (CFGNode) e.getComponent();
				IFileInfo info = node.getElement().getSourceInfo();
				if (info == null) return;
				
				FileView fv = openSourceView(info.getFile());
				fv.setHightLight(info);
			}
		}
		
	};
	private JToggleButton toggle_pin;

	void openProjectFolder(){
		int result = chooserProject.showDialog(frmCProjectTesting, "Open project folder");
		
		if (result == JFileChooser.APPROVE_OPTION){
			File root = chooserProject.getSelectedFile();
			toggle_pin.setSelected(false);
			Config.PINNED_PROJECT = null;
			Config.save();
			openProject(root);
	 	}
	}
	
	void openProject(File root){
		CProject project = new CProject(root);
		currentProject = project;
		
		if (currentExport != null)
			currentExport.close();
		currentExport = new ExcelExporter(currentProject);
		
		CProjectNode rootNode = new CProjectNode(root, 
				CProjectNode.TYPE_PROJECT, project); 
		
		tree_project = new GUIProjectExplorer(rootNode);
		scroll_project_tree.setViewportView(tree_project);
		clearAllView();
	}
	
	void openFunctionDetails(IFunction fn){
		FunctionView fv = new FunctionView(frmCProjectTesting, fn, currentExport);
		fv.setVisible(true);
	}
	
	void clearAllView(){
		//Clear Call Graph and CFG
		tab_graph.closeAllTab();
		
		//Clear function overview
		lblFunctionName.setText(null);
		
		//Clear process manager
		tab_source_view.closeAllTab();
		((DefaultTableModel) table_simple_result.getModel()).setRowCount(0);
		layout_process_mgr.clearRows();
	}
	
	FileView openSourceView(File file){
		try {
			return (FileView) tab_source_view.openTab(file.getName(), null, 
					file.getAbsolutePath(), 
					FileView.class.getConstructor(File.class), file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	void openSourceView(IFunction fn){
		IFileInfo info = fn.getSourceInfo();
		FileView fv = openSourceView(info.getFile());
		
		fv.setHightLight(info);
	}
	
	void removeActionListener(AbstractButton btn){
		for (ActionListener a: btn.getActionListeners())
			btn.removeActionListener(a);
	}
	
	void updateProcessView(IFunction fn, JLabel status, JButton action, 
			TestProcess p){
		if (fn.isTesting()){
			status.setIcon(ICON_LOADING);
			action.setIcon(ICON_CLOSE);
			action.setToolTipText("Stop test");
			
			removeActionListener(action);
			action.addActionListener(e -> {
				processMgr.stopTest(p);
			});
		}
		
		else if (fn.getStatus() == IFunction.TESTED){
			status.setIcon(ICON_COMPLETE);
			action.setIcon(ICON_DETAILS);
			action.setToolTipText("View details");
			
			removeActionListener(action);
			action.addActionListener(e -> openFunctionDetails(fn));
		}
		
		else if (fn.getStatus() == IFunction.LOADED){
			status.setIcon(null);
			action.setIcon(ICON_TEST);
			action.setToolTipText("Start test");
			
			removeActionListener(action);
			action.addActionListener(e -> {
				testFunction(fn);
			});
		}
	}
	
	void hightLightTestpath(ITestpath tp){
		if (currentFunction == null) return;
		CFGView cv = openCFGView(currentFunction, ICFG.COVER_BRANCH);
		cv.setHightLightTestpath(tp);
	}
	
	void testFunction(IFunction fn){
		if (fn.isTesting() || 
				fn.getStatus() == IFunction.UNSUPPORT)
			return;
		
		TestProcess p = new TestProcess() {	
	private TableLayout.TableRow row;
		
	@Override
	public void testStart() {
		lblFunctionName.setText(Utils.html(fn.getHTML()));
		tab_source_view.setSelectedIndex(0);
		fn.setTesting(true);
		row = mapProcess.get(fn);
		
		JLabel status = null;
		JButton action = null;
		
		if (row == null){
			action = new JButton();
			action.setBorder(null);
			action.setContentAreaFilled(false);
			status = new JLabel();
			
			String name = Utils.relative(fn.getSourceInfo().getFile(), 
					currentProject.getRoot())
					+ "::" + fn.getName();
			
			updateProcessView(fn, status, action, this);
			row = layout_process_mgr.insertRow(0, 40, true, "c c l c c c l c",
					null, new JLabel(name), status, action);
			mapProcess.put(fn, row);
		} else {
			status = (JLabel) row.getComponent(2);
			action = (JButton) row.getComponent(3);
			updateProcessView(fn, status, action, this);
		}
		
	}

	@Override
	public void test() throws InterruptedException {
		//Thread.sleep(5000);
		
		IFunctionTestResult r = currentProject.testFunction(fn);
		ArrayList<ITestpath> show = new ArrayList<>();
		DefaultTableModel model = (DefaultTableModel) table_simple_result.getModel();
		
		show.addAll(r.getTestpaths(IFunctionTestResult.BRANCH));
		show.addAll(r.getTestpaths(IFunctionTestResult.ERROR));
		
		checkStop();
		model.setRowCount(0);
		for (int i = 0; i < show.size(); i++){
			ITestpath path = show.get(i);
			ISolution sr = path.getSolution();
			
			model.addRow(new Object[]{
				i+1,
				path,
				sr.getMessage(),
				sr.getReturnValue()
			});
		}
		model.addRow(new Object[]{});
		currentFunction = fn;
	}
			
	@Override
	public void testEnd(boolean finish) {
		fn.setTesting(false);
		if (finish)
			fn.setStatus(IFunction.TESTED);
		
		JLabel status = (JLabel) row.getComponent(2);
		JButton action = (JButton) row.getComponent(3);
		
		updateProcessView(fn, status, action, this);
	}
			
		};
		processMgr.runTest(p);
		
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
		frmCProjectTesting.setIconImage(Toolkit.getDefaultToolkit().getImage(GUIMain.class.getResource("/image/project_test.png")));
		frmCProjectTesting.setTitle("C Project Testing");
		frmCProjectTesting.setBounds(margin, margin, sc.width - 2*margin, sc.height - 2*margin);
		frmCProjectTesting.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCProjectTesting.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel panel = new JPanel();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(5);
		splitPane.setBorder(null);
		GroupLayout groupLayout = new GroupLayout(frmCProjectTesting.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 1354, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 1342, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
					.addContainerGap())
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
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
					.addContainerGap())
				.addComponent(scroll_project_tree, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addComponent(scroll_project_tree, GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
		);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		scroll_project_tree.setViewportView(panel_3);
		
		JButton btn_refresh = new JButton("");
		btn_refresh.setToolTipText("Refresh project");
		btn_refresh.setIcon(new ImageIcon(GUIMain.class.getResource("/image/refresh.png")));
		toolBar.add(btn_refresh);
		
		JToggleButton toggle_filter = new JToggleButton("");
		toggle_filter.setToolTipText("Filter source file");
		toggle_filter.setIcon(new ImageIcon(GUIMain.class.getResource("/image/filter.png")));
		toolBar.add(toggle_filter);
		
		toggle_pin = new JToggleButton("");
		toggle_pin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentProject == null){
					toggle_pin.setSelected(false);
					return;
				}
				
				if (toggle_pin.isSelected())
					Config.PINNED_PROJECT = currentProject.getRoot();
				else
					Config.PINNED_PROJECT = null;
				Config.save();
			}
		});
		toggle_pin.setToolTipText("Pin project");
		toggle_pin.setIcon(new ImageIcon(GUIMain.class.getResource("/image/pin.png")));
		toolBar.add(toggle_pin);
		panel_1.setLayout(gl_panel_1);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerSize(5);
		splitPane.setRightComponent(splitPane_1);
		
		tab_graph = new LightTabbedPane(JTabbedPane.TOP);
		splitPane_1.setLeftComponent(tab_graph);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		tab_graph.addTab("Call graph", null, panel_4, null);
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setDividerSize(5);
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setRightComponent(splitPane_2);
		
		JPanel panel_2 = new JPanel();
		splitPane_2.setLeftComponent(panel_2);
		
		lblFunctionName = new JLabel("");
		lblFunctionName.setFont(lblFunctionName.getFont().deriveFont(18f));
		lblFunctionName.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(null);
		scrollPane_1.getViewport().setBackground(Color.WHITE);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addComponent(lblFunctionName, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(lblFunctionName, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
		);
		
		table_simple_result = new JTable();
		table_simple_result.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Test path", "Input", "Output"
			}
		){
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
		});
		table_simple_result.getColumnModel().getColumn(0).setResizable(false);
		table_simple_result.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_simple_result.getColumnModel().getColumn(0).setMaxWidth(30);
		table_simple_result.getColumnModel().getColumn(1).setPreferredWidth(200);
		table_simple_result.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_simple_result.getColumnModel().getColumn(3).setPreferredWidth(50);
		
		table_simple_result.getSelectionModel().addListSelectionListener(e -> {
			int row = table_simple_result.getSelectedRow();
			if (row >= 0)
				hightLightTestpath((ITestpath) table_simple_result
					.getValueAt(row, 1));
		});
		
		scrollPane_1.setViewportView(table_simple_result);
		panel_2.setLayout(gl_panel_2);
		
		tab_source_view = new LightTabbedPane(JTabbedPane.TOP);
		splitPane_2.setRightComponent(tab_source_view);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		tab_source_view.addTab("Process Manager", null, scrollPane, null);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		
		layout_process_mgr = new TableLayout(panel_5, new double[][]{
			{10, TableLayout.FILL, 40, 40, 10}, {}
		});
		panel_5.setLayout(layout_process_mgr);
		
		scrollPane.setViewportView(panel_5);
		splitPane_2.setDividerLocation(370);
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
		btnExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentProject == null) return;
				ArrayList<IFunction> listTested = new ArrayList<>(
						currentProject.getFunctions());
				listTested.removeIf(f -> (f.getTestResult() == null));
				if (listTested.size() == 0) return;
				
				for (IFunction fn: listTested)
					currentExport.addFunction(fn);
				
				try {
					currentExport.export();
					JOptionPane.showMessageDialog(frmCProjectTesting, 
							"Export success: " + listTested.size(), "Message", 
							JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frmCProjectTesting, 
							e1.getMessage(), "Error", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.insets = new Insets(0, 0, 0, 5);
		gbc_btnExport.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnExport.gridx = 2;
		gbc_btnExport.gridy = 0;
		panel.add(btnExport, gbc_btnExport);
		
		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tree_project == null || tree_project.getSelectionCount() == 0)
					return;
				CProjectNode node = (CProjectNode) tree_project.getSelectedItem();
				
				if (node.getType() == CProjectNode.TYPE_FUNCTION){
					testFunction(node.getFunction());
				}
			}
		});
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
		btnSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (settingDialog == null)
					settingDialog = new SettingDialog(frmCProjectTesting);
				settingDialog.setVisible(true);
			}
		});
		
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
	
	class GUIProjectExplorer extends ProjectExplorer{
		private static final long serialVersionUID = 1L;
		
		ArrayList<CProjectNode> retainSelect(int type){
			ArrayList<CProjectNode> list = new ArrayList<>();
			
			for (IProjectNode n: getSelectedItems()){
				CProjectNode node = (CProjectNode) n;
				if (node.getType() == type)
					list.add(node);
			}
			
			return list;
		}
		
		public GUIProjectExplorer(IProjectNode root) {
			super(root);
			
			addItemClickListener((item, count) -> {
				if (count == 2){
					CProjectNode node = (CProjectNode) item;
					int type = node.getType();
					
					if (type == CProjectNode.TYPE_FILE){
						openSourceView(node.getFile());
					}
					
					else if (type == CProjectNode.TYPE_FUNCTION){
						openCFGView(node.getFunction(), ICFG.COVER_BRANCH);
					}
				}
			});
			
			setMenuHandle(new MenuHandle<IProjectNode>() {

				JMenuItem openCFG, openCFG3, viewSource, viewTestdata;
				
				@Override
				public void accept(JPopupMenu t) {
					
					openCFG = new JMenuItem("Open CFG");
					openCFG.addActionListener(e -> {
						retainSelect(CProjectNode.TYPE_FUNCTION).forEach(n -> 
						openCFGView(n.getFunction(), ICFG.COVER_BRANCH));
					});
					
					openCFG3 = new JMenuItem("Open CFG3");
					openCFG3.addActionListener(e -> {
						retainSelect(CProjectNode.TYPE_FUNCTION).forEach(n -> 
						openCFGView(n.getFunction(), ICFG.COVER_SUBCONDITION));
					});
					
					viewSource = new JMenuItem("View source");
					viewSource.addActionListener(e -> {
						ArrayList<CProjectNode> fns = retainSelect(
								CProjectNode.TYPE_FUNCTION);
						
						if (fns.size() == 1)
							openSourceView(fns.get(0).getFunction());
						else
							retainSelect(CProjectNode.TYPE_FILE).forEach(n -> 
							openSourceView(n.getFile()));
					});
					
					viewTestdata = new JMenuItem("View test data");
					viewTestdata.addActionListener(e -> {
						openFunctionDetails(retainSelect(
								CProjectNode.TYPE_FUNCTION).get(0)
								.getFunction());
					});
					
					t.add(openCFG);
					t.add(openCFG3);
					t.add(viewSource);
					t.add(viewTestdata);
				}

				@Override
				public void acceptList(List<IProjectNode> items) {
					openCFG.setVisible(false);
					openCFG3.setVisible(false);
					viewSource.setVisible(false);
					viewTestdata.setVisible(false);
					
					for (IProjectNode n: items){
						int type = ((CProjectNode) n).getType();
						if (type == CProjectNode.TYPE_FUNCTION){
							openCFG.setVisible(true);
							openCFG3.setVisible(true);
							viewSource.setVisible(true);
							viewTestdata.setVisible(true);
						}
						
						else if (type == CProjectNode.TYPE_FILE){
							viewSource.setVisible(true);
						}
					}
				}
				
			});
		}
	}
}
