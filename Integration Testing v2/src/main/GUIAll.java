package main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import cdt.CMainProcess;
import cdt.SelectFunction;
import core.GUI;
import core.MainProcess;
import core.MainProcess.Return;
import core.MainProcess.Returned;
import core.error.CoreException;
import core.error.MainNotFoundException;
import core.error.ThreadStateException;
import core.graph.CFGView;
import core.graph.FileView;
import core.graph.LightTabbedPane;
import core.graph.SettingDialog;
import core.graph.adapter.FunctionAdapter;
import core.graph.canvas.FunctionCanvas;
import core.graph.canvas.LoopCanvas;
import core.graph.canvas.LoopCanvas.OnApplyValue;
import core.graph.canvas.LoopCanvas.OnNodeSelect;
import core.graph.canvas.StatementCanvas;
import core.graph.node.LoopNode;
import core.models.Function;
import core.models.Statement;
import core.models.expression.NotNegativeExpression;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.solver.Solver.Result;
import core.unit.BasisPath;
import core.unit.ConstraintEquations;
import core.unit.LoopablePath;

import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jdt.JMainProcess;
import java.awt.Dimension;

/**
 * Lớp giao diện hiển thị của ứng dụng
 * 
 * @author ducvu
 */
public class GUIAll extends GUI {

	private JFrame frmMain;
	private FunctionCanvas fGraph;
	//private VCanvas vGraph;
	private JFileChooser fileChooserC, fileChooserJ;
	private LightTabbedPane infoTab;
	private LightTabbedPane tabbedCanvas;

	private Function preRoot;
	private DefaultTableModel detailModel;
	private DefaultTableCellRenderer centerRenderer;
	
	/** Tiến trình chính của ứng dụng */
	private MainProcess main;

	/**
	 * Chạy ứng dụng
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIAll window = new GUIAll();
					window.frmMain.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Mở hộp thoại để chọn tập tin */
	private void openCFiles() {
		int status = fileChooserC.showDialog(frmMain, "Mở tập tin C/thư mục");

		if (status == JFileChooser.APPROVE_OPTION) {
			tabbedCanvas.setSelectedIndex(0);
			try {
				main = new CMainProcess();
				main.setWorkingFiles(fileChooserC.getSelectedFiles(), true);
				main.loadFunctionFromFiles();
				if (main.isEmptyFunction())
					return;
				fGraph.setAdapter(new FunctionAdapter(main.getFunctions()));
				preRoot = null;
			} 
			catch (MainNotFoundException e) {
				if (main.getFunctions().size() == 1)
					fGraph.setAdapter(new FunctionAdapter(
							main.getFunctions().get(0)));
				else
					this.openSelectFunction();
			} 
			catch (Exception e) {
				alertError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/** Mở hộp thoại để chọn tập tin */
	private void openJavaFiles() {
		int status = fileChooserJ.showDialog(frmMain, "Mở tập tin Java");

		if (status == JFileChooser.APPROVE_OPTION) {
			tabbedCanvas.setSelectedIndex(0);
			try {
				main = new JMainProcess();
				main.setWorkingFiles(fileChooserJ.getSelectedFiles(), true);
				main.loadFunctionFromFiles();
				if (main.isEmptyFunction())
					return;
				fGraph.setAdapter(new FunctionAdapter(main.getFunctions()));
				preRoot = null;
			} 
			catch (MainNotFoundException e) {
				if (main.getFunctions().size() == 1)
					fGraph.setAdapter(new FunctionAdapter(
							main.getFunctions().get(0)));
				else
					this.openSelectFunction();
			} 
			catch (Exception e) {
				alertError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/** Mở hộp thoại để chọn hàm gốc */
	private void openSelectFunction() {
		if (main == null || main.isEmptyFunction())
			return;
		ArrayList<Function> funcs = main.getFunctions();
		SelectFunction select = new SelectFunction(frmMain, funcs, preRoot);
		Function selected = select.showDialog();

		if (selected == null)
			return;
		preRoot = selected;
		fGraph.setAdapter(new FunctionAdapter(selected));
	}

	/**
	 * Mở ra một tab mới hoặc chuyển sang tab chứa nội dung của tập tin chỉ định
	 * 
	 * @param file tập tin muốn mở
	 */
	public void openFileView(File file) {
		try {
			infoTab.openTab(file.getName(), null, file.getAbsolutePath(), 
					FileView.class.getConstructor(File.class), file);
		} catch (Exception e) {}
	}
	
	@Override
	public CFGView openFuntionView(Function fn, boolean subCondition){
		try {
			return (CFGView) tabbedCanvas.openTab(
					fn.getName() + (subCondition ? "-3" : "-1,2"),
					null, 
					fn.getNameAndFile(),
					CFGView.class.getConstructor(Function.class, boolean.class),
					fn, subCondition);
		} catch (Exception e) {
			return null;
		}
	}
	
	private JLabel lbl_loading;
	private JLabel lbl_status;
	private Function currentFunction;
	private int currentindex;
	
	private ArrayList<StorePathTable> listTable;
	private StoreLoopTable table_loop_path;
	private StorePathTable table_loop_result;

	@Override
	public void beginTestFunction(Function func) {
		try {
			main.beginTestFunction(func, new Returned() {
				
				@Override
				public void error(CoreException e) {
					alertError(e.getMessage());
				}

				@Override
				public void receive() {
					currentFunction = func;
					
					setStatus("Đang tìm các nhánh dựa trên các cấp độ phủ");
					ArrayList<BasisPath> base = func.getCFG(false).getBasisPaths();
					
					listTable.get(0).setBasisPaths(
							func.getCFG(false).getCoverStatementPaths());
					listTable.get(1).setBasisPaths(
							func.getCFG(false).getCoverBranchPaths());
					listTable.get(2).setBasisPaths(
							func.getCFG(true).getCoverBranchPaths());
					listTable.get(3).setBasisPaths(base);
					
					ArrayList<LoopablePath> loopablePath = new ArrayList<>();
					ArrayList<BasisPath> loopPath = new ArrayList<>();

					setStatus("Đang tìm các đường chứa vòng lặp");
					for (BasisPath path: base){
						LoopablePath l = new LoopablePath(path);
						if (l.getLoops().size() > 0){
							loopablePath.add(l);
							loopPath.add(path);
						}
					}
					table_loop_path.setBasisPaths(loopPath);
					table_loop_path.setLoopPaths(loopablePath);
					table_loop_result.setBasisPaths(null);
					
					for (StorePathTable table: listTable){
						DefaultTableModel pathModel = (DefaultTableModel) 
								table.getModel();
						removeTableModel(pathModel);
						if (table.getBasisPaths() == null)
							continue;
						
						int j = 1;
						for (BasisPath path: table.getBasisPaths()){
							Result r = path.getSolveResult();
							pathModel.addRow(new Object[]{
								j++,
								path.toStringSkipMarkdown(),
								r.getSolutionMessage(),
								r.getReturnValue()
							});
						}
						pathModel.addRow(new Object[]{null, null, null, null});
					}
					
					openFileView(func.getSourceFile());
					openFuntionView(func, currentindex == 2);
					setStatus(null);
				}
			});
		} catch (ThreadStateException e) {
			alertError("Việc xử lý chưa hoàn thành");
		}
	}
	
	private void beginTestLoop(LoopablePath path, ArrayList<Integer> indexes) {
		try {
			main.beginTestLoopPath(path, indexes, currentFunction, new Return<ArrayList<BasisPath>>() {
				@Override
				public void error(CoreException e) {
					alertError(e.getMessage());
				}
				
				@Override
				public void receive(ArrayList<BasisPath> result) {
					DefaultTableModel pathModel = (DefaultTableModel) 
							table_loop_result.getModel();
					removeTableModel(pathModel);
					table_loop_result.setBasisPaths(result);
					
					int j = 1;
					for (BasisPath path: result){
						Result r = path.getSolveResult();
						pathModel.addRow(new Object[]{
							j++,
							path.toStringSkipMarkdown(),
							r.getSolutionMessage(),
							r.getReturnValue()
						});
					}
					pathModel.addRow(new Object[]{null, null, null, null});
				}
			});
		} catch (ThreadStateException e) {
			alertError("Việc xử lý chưa hoàn thành");
		}
	}
	
	/**
	 * Có sự thay đổi các tab table
	 */
	private void tableTabChanged(int index){
		currentindex = index;
		if (currentFunction == null) return;
		openFuntionView(currentFunction, index == 2);
	}
	
	private void selectPathByIndex(BasisPath path, boolean subCondition){
		StatementCanvas canvas = openFuntionView(
				currentFunction, 
				subCondition
				).getCanvas();
		
		if (path == null){
			canvas.resetSelectingPath();
			return;
		}
		
		ConstraintEquations constraint = path.getConstraint();
		int i = 0, j = 0;
		
		canvas.setSelectedPath(path);
		removeTableModel(detailModel);
		for (Statement stm: path){
			if (stm instanceof ScopeStatement)
				continue;
			else if (stm instanceof FlagStatement){
				i++;
				continue;
			}
			while (j < constraint.size() && 
					constraint.get(j) instanceof NotNegativeExpression)
				j++;
			
			detailModel.addRow(new Object[]{
					i++,
					stm,
					stm.isCondition() ? constraint.get(j++) : ""
			});
		}
		
		infoTab.setSelectedIndex(0);
	}

	@Override
	public int getDefaultCanvasWidth() {
		return fGraph.getWidth();
	}
	
	@Override
	public void setStatus(String status, Object... args){
		if (status == null || status.isEmpty()){
			lbl_loading.setVisible(false);
			lbl_status.setVisible(false);
		}
		else {
			lbl_loading.setVisible(true);
			lbl_status.setVisible(true);
			lbl_status.setText(String.format(status, args));
		}
	}
	
	/**
	 * Xóa hết các ô trong một bảng
	 */
	private static void removeTableModel(DefaultTableModel model){
		for (int j = model.getRowCount()-1;j>=0;j--)
			model.removeRow(j);
	}

	public GUIAll() {
		initialize();
	}

	/**
	 * Khởi tạo nội dung của khung ứng dụng.
	 */
	private void initialize() {
		//main = new CMainProcess();
		listTable = new ArrayList<StorePathTable>();
		centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		frmMain = new JFrame();
		frmMain.setTitle("Kiểm thử tích hơp cho C");
		frmMain.setBounds(50, 50, 1266, 630);
		frmMain.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane splitPane_main = new JSplitPane();
		splitPane_main.setBorder(null);
		splitPane_main.setDividerSize(3);

		JSplitPane detailWrap = new JSplitPane();
		detailWrap.setBorder(null);
		detailWrap.setDividerSize(2);
		detailWrap.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_main.setRightComponent(detailWrap);

		infoTab = new LightTabbedPane(JTabbedPane.TOP);
		infoTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		infoTab.setBorder(null);
		detailWrap.setRightComponent(infoTab);

		JScrollPane infoWrap = new JScrollPane();
		infoWrap.setBorder(null);
		infoTab.addTab("Chi tiết", null, infoWrap, null);
		
		JTable table_1 = new JTable();
		table_1.setModel(detailModel = new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"STT", "C\u00E2u l\u1EC7nh", "C\u00E1c r\u00E0ng bu\u1ED9c"
			}
		));
		table_1.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_1.getColumnModel().getColumn(0).setMinWidth(30);
		table_1.getColumnModel().getColumn(0).setMaxWidth(30);
		infoWrap.setViewportView(table_1);
		infoWrap.getViewport().setBackground(Color.WHITE);
		infoTab.setTabCloseableAt(0, false);
		
		for (int x = 0; x < table_1.getColumnCount(); x++) {
			table_1.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
		
		JTabbedPane tab_table = new JTabbedPane(JTabbedPane.TOP);
		tab_table.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tab = (JTabbedPane) e.getSource();
				tableTabChanged(tab.getSelectedIndex());
			}
		});
		detailWrap.setLeftComponent(tab_table);
		
		
		/*---------BEGIN ADD TABLES-----------*/
		
		String[] tab_names = {"Phủ câu lệnh", "Phủ nhánh", "Phủ điều kiện con", 
				"Tất cả nhánh"};
		int index = 0;
		for (String tab_name: tab_names){
			JScrollPane extraWrap = new JScrollPane();
			extraWrap.setBorder(null);
			
			StorePathTable table = new StorePathTable(index++ == 2);
			table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"STT", "\u0110\u01B0\u1EDDng d\u1EABn", "Testcases", "Return"
				}
			));
			table.getColumnModel().getColumn(0).setPreferredWidth(30);
			table.getColumnModel().getColumn(0).setMinWidth(30);
			table.getColumnModel().getColumn(0).setMaxWidth(30);
			table.getColumnModel().getColumn(3).setPreferredWidth(70);
			table.getColumnModel().getColumn(3).setMaxWidth(70);
			extraWrap.setViewportView(table);
			extraWrap.getViewport().setBackground(Color.WHITE);
			
			tab_table.add(tab_name, extraWrap);
			listTable.add(table);
		}
		
		/*---------END ADD TABLES-----------*/
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		panel_1.setBackground(Color.WHITE);
		tab_table.addTab("Các vòng lặp", null, panel_1, null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(Color.WHITE);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(null);
		scrollPane_1.getVerticalScrollBar().setUnitIncrement(16);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBorder(null);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
				.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
				.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
		);
		
		table_loop_result = new StorePathTable(false);
		table_loop_result.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"STT", "\u0110\u01B0\u1EDDng d\u1EABn", "Testcases", "Return"
				}
			));
		table_loop_result.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_loop_result.getColumnModel().getColumn(0).setMinWidth(30);
		table_loop_result.getColumnModel().getColumn(0).setMaxWidth(30);
		table_loop_result.getColumnModel().getColumn(3).setPreferredWidth(70);
		table_loop_result.getColumnModel().getColumn(3).setMaxWidth(70);
		scrollPane_2.setViewportView(table_loop_result);
		
		scrollPane_2.getViewport().setBackground(Color.WHITE);
		
		LoopCanvas loop_canvas = new LoopCanvas();
		loop_canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				StatementCanvas canvas = openFuntionView(
						currentFunction, 
						currentindex == 2
						).getCanvas();
				canvas.resetSelectingExtraPath();
			}
		});
		loop_canvas.setBackground(Color.WHITE);
		scrollPane_1.setViewportView(loop_canvas);
		
		loop_canvas.setOnNodeSelect(new OnNodeSelect() {
			@Override
			public void selected(LoopNode node) {
				StatementCanvas canvas = openFuntionView(
						currentFunction, false
						).getCanvas();
				canvas.setSelectedExtraPath(node.getStatement().getOriginList());
			}
		});
		loop_canvas.setOnApplyValue(new OnApplyValue() {
			@Override
			public void applied(ArrayList<Integer> indexes, LoopablePath path) {
				beginTestLoop(path, indexes);
			}
		});
		
		table_loop_path = new StoreLoopTable();
		table_loop_path.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"STT", "\u0110\u01B0\u1EDDng thi h\u00E0nh"
			}
		));
		table_loop_path.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_loop_path.getColumnModel().getColumn(0).setMinWidth(30);
		table_loop_path.getColumnModel().getColumn(0).setMaxWidth(30);
		scrollPane.setViewportView(table_loop_path);
		
		listTable.add(table_loop_path);
		listTable.add(table_loop_result);
		table_loop_path.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				StoreLoopTable table = (StoreLoopTable) table_loop_path;
				if (e.getValueIsAdjusting() || table.getSelectedRow() == -1)
					return;
				openFuntionView(
						currentFunction, false
						).getCanvas().resetSelectingExtraPath();
				removeTableModel((DefaultTableModel) table_loop_result.getModel());
				
				Object index = table.getValueAt(table.getSelectedRow(), 0);
				
				if (index != null){
					int i = Integer.valueOf(index + "") - 1;
					loop_canvas.setLoopPath(table.getLoopPaths().get(i));
				}
			}
		});
		
		panel_1.setLayout(gl_panel_1);

		detailWrap.setDividerLocation(400);
		splitPane_main.setDividerLocation(600);
		
		JPanel panel_tray = new JPanel();
		panel_tray.setBackground(Color.WHITE);
		
		JPanel panel_toolbar = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frmMain.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addComponent(panel_tray, GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
				.addComponent(panel_toolbar, GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
				.addComponent(splitPane_main, GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel_toolbar, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(splitPane_main, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_tray, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
		);
		panel_toolbar.setBorder(new MatteBorder(0, 0, 1, 0, (Color) Color.LIGHT_GRAY));
		
				JPanel panel_toolbar_left = new JPanel();
				
						JLabel lbl_open_c = new JLabel();
						lbl_open_c.setPreferredSize(new Dimension(80, 30));
						lbl_open_c.setHorizontalAlignment(SwingConstants.CENTER);
						lbl_open_c.setToolTipText("Mở tập tin C/thư mục");
						lbl_open_c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						lbl_open_c.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
						lbl_open_c.setOpaque(true);
						lbl_open_c.setBackground(SystemColor.controlHighlight);
						lbl_open_c.setText("Mở C...");
						lbl_open_c.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								openCFiles();
							}
						});
						lbl_open_c.setIcon(new ImageIcon(GUIAll.class
								.getResource("/image/file.png")));
						//tabbedCanvas.setTabCloseableAt(1, false);

						JLabel lbl_set_root = new JLabel();
						lbl_set_root.setPreferredSize(new Dimension(100, 30));
						lbl_set_root.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								openSelectFunction();
							}
						});
						lbl_set_root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						lbl_set_root.setHorizontalAlignment(SwingConstants.CENTER);
						lbl_set_root.setIcon(new ImageIcon(GUIAll.class
								.getResource("/image/root.png")));
						lbl_set_root.setToolTipText("Đặt hàm số gốc tùy chỉnh");
						lbl_set_root.setText("Đặt gốc...");
						lbl_set_root.setOpaque(true);
						lbl_set_root.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
						lbl_set_root.setBackground(SystemColor.controlHighlight);
						panel_toolbar_left.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
						panel_toolbar_left.add(lbl_open_c);
						
						JLabel lbl_open_j = new JLabel();
						lbl_open_j.setPreferredSize(new Dimension(100, 30));
						lbl_open_j.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						lbl_open_j.setIcon(new ImageIcon(GUIAll.class.getResource("/image/file.png")));
						lbl_open_j.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								openJavaFiles();
							}
						});
						lbl_open_j.setToolTipText("Mở tập tin C/thư mục");
						lbl_open_j.setText("Mở Java");
						lbl_open_j.setOpaque(true);
						lbl_open_j.setHorizontalAlignment(SwingConstants.CENTER);
						lbl_open_j.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
						lbl_open_j.setBackground(SystemColor.controlHighlight);
						panel_toolbar_left.add(lbl_open_j);
						panel_toolbar_left.add(lbl_set_root);
						
						JPanel panel = new JPanel();
						GroupLayout gl_panel_toolbar = new GroupLayout(panel_toolbar);
						gl_panel_toolbar.setHorizontalGroup(
							gl_panel_toolbar.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_toolbar.createSequentialGroup()
									.addComponent(panel_toolbar_left, GroupLayout.PREFERRED_SIZE, 601, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
						);
						gl_panel_toolbar.setVerticalGroup(
							gl_panel_toolbar.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_toolbar_left, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
								.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
						);
						panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
						
						JButton btnCit = new JButton("Cài đặt");
						btnCit.setPreferredSize(new Dimension(120, 30));
						btnCit.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								new SettingDialog(frmMain).setVisible(true);
							}
						});
						btnCit.setIcon(new ImageIcon(GUIAll.class.getResource("/image/file.png")));
						panel.add(btnCit);
						panel_toolbar.setLayout(gl_panel_toolbar);
		panel_tray.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		
		lbl_status = new JLabel("");
		lbl_status.setVisible(false);
		panel_tray.add(lbl_status);
		
		lbl_loading = new JLabel("");
		lbl_loading.setVisible(false);
		lbl_loading.setIcon(new ImageIcon(GUIAll.class.getResource("/image/loading.gif")));
		panel_tray.add(lbl_loading);
		
		tabbedCanvas = new LightTabbedPane(JTabbedPane.TOP);
		tabbedCanvas.setBorder(null);
		splitPane_main.setLeftComponent(tabbedCanvas);

		JScrollPane fGraphWrap = new JScrollPane();
		fGraphWrap.setBorder(null);

		fGraph = new FunctionCanvas();
		fGraph.setBorder(null);
		fGraph.setBackground(Color.WHITE);
		fGraph.setParent(fGraphWrap);
		fGraphWrap.setViewportView(fGraph);

		fGraphWrap.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JViewport v = (JViewport) e.getSource();
				v.repaint();
			}
		});
		tabbedCanvas.addTab("Đồ thị gọi hàm", null, fGraphWrap, null);
		
//		JScrollPane vGraphWrap = new JScrollPane();
//		vGraphWrap.setBorder(null);
//		tabbedCanvas.addTab("Global Variable", null, vGraphWrap, null);
//		
//		vGraph = new VCanvas();
//		vGraph.setBackground(Color.WHITE);
//		vGraph.setBorder(null);
//		vGraph.setParent(vGraphWrap);
//		vGraphWrap.setViewportView(vGraph);
		tabbedCanvas.setTabCloseableAt(0, false);
		frmMain.getContentPane().setLayout(groupLayout);

		fileChooserC = new JFileChooser();
		FileNameExtensionFilter cFilter = new FileNameExtensionFilter(
				"Mã nguồn C (*.c; *.cpp)", new String[] { "C", "CPP" });
		fileChooserC.setMultiSelectionEnabled(true);
		// fileChooser.setFileHidingEnabled(false);
		fileChooserC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserC.setFileFilter(cFilter);
		
		fileChooserJ = new JFileChooser();
		FileNameExtensionFilter jFilter = new FileNameExtensionFilter(
				"Mã nguồn Java (*.java)", new String[] { "JAVA" });
		fileChooserJ.setMultiSelectionEnabled(true);
		// fileChooser.setFileHidingEnabled(false);
		fileChooserJ.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserJ.setFileFilter(jFilter);
		
		for (StorePathTable table: listTable){
			for (int x = 0; x < table.getColumnCount(); x++) {
				table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
			}
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() || table.getSelectedRow() == -1)
						return;
					Object index = table.getValueAt(table.getSelectedRow(), 0);
					
					if (index == null)
						selectPathByIndex(null, table.isSubCondition());
					else{
						int i = Integer.valueOf(index + "") - 1;
						selectPathByIndex(table.getBasisPaths().get(i), table.isSubCondition());
					}
				}
			});
		}
	}
	
	private static class StorePathTable extends JTable{
		private static final long serialVersionUID = 1L;
		private ArrayList<BasisPath> mPath;
		private boolean mSubcondition;
		
		public StorePathTable(boolean subCondition){
			mSubcondition = subCondition;
		}
		
		public boolean isSubCondition(){
			return mSubcondition;
		}

		public ArrayList<BasisPath> getBasisPaths() {
			return mPath;
		}

		public void setBasisPaths(ArrayList<BasisPath> mPath) {
			this.mPath = mPath;
		}
	}
	
	private static class StoreLoopTable extends StorePathTable{
		private static final long serialVersionUID = 1L;
		private ArrayList<LoopablePath> mLoop;
		
		public StoreLoopTable() {
			super(false);
		}

		public ArrayList<LoopablePath> getLoopPaths() {
			return mLoop;
		}

		public void setLoopPaths(ArrayList<LoopablePath> mLoop) {
			this.mLoop = mLoop;
		}
		
	}
	
	/*----------------- LOG AND ERROR -----------------*/

	/** Bật thông báo lỗi với nội dung chỉ định */
	public void alertError(String s) {
		JOptionPane.showMessageDialog(frmMain, s, "Errors",
				JOptionPane.ERROR_MESSAGE);
	}
}
