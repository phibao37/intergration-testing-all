package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
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
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import cdt.CMainProcess;
import core.GUI;
import core.MainProcess;
import core.MainProcess.Return;
import core.MainProcess.Returned;
import core.S.SCREEN;
import core.Utils;
import core.error.CoreException;
import core.error.MainNotFoundException;
import core.graph.CFGView;
import core.graph.DragScrollPane;
import core.graph.FileView;
import core.graph.LightTabbedPane;
import core.graph.adapter.FunctionAdapter;
import core.graph.canvas.FunctionCanvas;
import core.graph.canvas.FunctionPairCanvas;
import core.graph.canvas.LoopCanvas;
import core.graph.canvas.LoopCanvas.OnApplyValue;
import core.graph.canvas.LoopCanvas.OnNodeSelect;
import core.graph.canvas.StatementCanvas;
import core.graph.node.FunctionPairNode;
import core.graph.node.LoopNode;
import core.inte.FunctionCallGraph;
import core.inte.FunctionPair;
import core.models.Expression;
import core.models.Function;
import core.models.Statement;
import core.models.Variable;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.solver.Solver.Result;
import core.unit.BasisPath;
import core.unit.LoopablePath;
import javafx.util.Pair;

import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jdt.JMainProcess;

import java.awt.Dimension;

import javax.swing.JRadioButton;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

/**
 * Lớp giao diện hiển thị của ứng dụng
 * 
 * @author ducvu
 */
public class GUIAll extends GUI {

	private JFrame frame_main;
	private FunctionCanvas canvas_fn_call;
	private JFileChooser fileChooserC, fileChooserJ;
	private JLabel lbl_loading;
	private JLabel lbl_status;
	private StoreLoopTable table_loop_path;
	private StorePathTable table_loop_result;
	private FunctionPairCanvas panel_function_pair;
	private ButtonGroup group_inte_type;
	private StorePathTable table_test_pair;
	private JTable table_testcase;
	private JTable table_path_details;
	private DefaultTableCellRenderer centerRenderer;
	private JLabel lbl_fn_name;
	private JLabel lbl_number_of_testcase;
	private JLabel lbl_number_of_childs;
	
	private LightTabbedPane tab_info;
	private LightTabbedPane tab_canvas;
	private JTabbedPane tab_table;

	private ArrayList<StorePathTable> listTable;
	private static enum ListTable{
		STATEMENT, CONDITION, SUBCONDITION;
	}
	
	/** Tiến trình chính của ứng dụng */
	private MainProcess main;
	private Function preRoot;
	private Function currentFunction, selectFunction;
	private boolean isWorking;
	
	/**
	 * Chạy ứng dụng
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIAll window = new GUIAll();
					window.frame_main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Mở hộp thoại chọn tập tin C/C++
	 */
	private void openCFiles() {
		int status = fileChooserC.showDialog(frame_main, "Mở tập tin C/thư mục");

		if (status == JFileChooser.APPROVE_OPTION) {
			tab_canvas.setSelectedComponent(cv_fn_call_wrap);
			try {
				main = new CMainProcess();
				main.setWorkingFiles(fileChooserC.getSelectedFiles(), true);
				main.loadFunctionFromFiles();
				if (main.isEmptyFunction())
					return;
				
				FunctionCallGraph fcg = main.getFunctionCallGraph();
				try{
					fcg.setByMain();
					setIntegration();
					preRoot = null;
				} catch (MainNotFoundException e1){
					if (fcg.size() == 1)
						fcg.setRoot(fcg.get(0));
					else {
						openSelectFunction();
						return;
					}
				}
				canvas_fn_call.setAdapter(new FunctionAdapter(fcg));
			} 
			catch (Exception e) {
				alertError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Mở hộp thoại chọn tập tin Java
	 */
	private void openJavaFiles() {
		int status = fileChooserJ.showDialog(frame_main, "Mở tập tin Java");

		if (status == JFileChooser.APPROVE_OPTION) {
			tab_canvas.setSelectedComponent(cv_fn_call_wrap);
			try {
				main = new JMainProcess();
				main.setWorkingFiles(fileChooserJ.getSelectedFiles(), true);
				main.loadFunctionFromFiles();
				if (main.isEmptyFunction())
					return;
				
				FunctionCallGraph fcg = main.getFunctionCallGraph();
				try{
					fcg.setByMain();
					setIntegration();
					preRoot = null;
				} catch (MainNotFoundException e1){
					if (fcg.size() == 1)
						fcg.setRoot(fcg.get(0));
					else {
						openSelectFunction();
						return;
					}
				}
				canvas_fn_call.setAdapter(new FunctionAdapter(fcg));
			} 
			catch (Exception e) {
				alertError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Mở hộp thoại để chọn hàm gốc
	 */
	private void openSelectFunction() {
		if (main == null || main.isEmptyFunction())
			return;
		ArrayList<Function> funcs = main.getFunctionCallGraph();
		SelectFunction select = new SelectFunction(frame_main, funcs, preRoot);
		Function selected = select.showDialog();

		if (selected == null)
			return;
		
		FunctionCallGraph fcg = main.getFunctionCallGraph().setRoot(selected);
		preRoot = selected;
		
		canvas_fn_call.setAdapter(new FunctionAdapter(fcg));
		setIntegration();
	}

	/*-------------- BASE GUI IMPLEMENT -------------*/
	
	@Override
	public void openFileView(File file) {
		try {
			tab_info.openTab(file.getName(), null, file.getAbsolutePath(), 
					FileView.class.getConstructor(File.class), file);
		} catch (Exception e) {}
	}
	
	@Override
	public CFGView openFuntionView(Function fn, boolean subCondition){
		try {
			return (CFGView) tab_canvas.openTab(
					fn.getName() + (subCondition ? "-3" : "-1,2"),
					null, 
					fn.getNameAndFile(),
					CFGView.class.getConstructor(Function.class, boolean.class),
					fn, subCondition);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void beginTestFunction(Function func) {
		if (isWorking){
			alertError("Việc xử lý chưa hoàn thành");
			return;
		}
		
		isWorking = true;
		main.beginTestUnit(func, new Returned() {	
			@Override
			public void error(CoreException e) {
				alertError(e.getMessage());
				isWorking = false;
				e.printStackTrace();
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
					removeTableModel(table);
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
				openFuntionView(func, shouldOpenSubCondition());
				
				setStatus(null);
				isWorking = false;
			}
		});
	
	}
	
	@Override
	public int getDefaultCanvasWidth() {
		return canvas_fn_call.getWidth();
	}
	
	@Override
	public void openFunctionDetails(Function fn) {
		selectFunction = fn;
		lbl_fn_name.setText(Utils.html(fn.getHTMLContent()));
		lbl_number_of_testcase.setText(fn.getTestcaseManager().size() + "");
		
		int count = fn.getRefers().size();
		lbl_number_of_childs.setText(count == 0 ? "0 (Hàm đơn vị)" : count + "");
	}
	
	@Override
	public void notifyFunctionTestcaseChanged(Function fn, int count) {
		if (fn == selectFunction){
			lbl_number_of_testcase.setText(count + "");
		}
	}
	
	@Override
	public void openFunctionTestcaseManager(Function fn) {
		new TestcaseManageDialog(frame_main, fn.getTestcaseManager())
			.setVisible(true);
	}

	@Override
	public void functionPairClicked(Function source, Function target, boolean dbClick) {
		tab_table.setSelectedComponent(panel_inte);
		FunctionPair p = panel_function_pair.selectPair(source, target);
		
		if (dbClick){
			beginTestFunctionPair(p);
		}
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
	
	/*-------------- ----------------- -------------*/
	
	private void beginTestLoop(LoopablePath path, ArrayList<Integer> indexes) {
		if (isWorking){
			alertError("Việc xử lý chưa hoàn thành");
			return;
		}
		
		isWorking = true;
		main.beginTestLoopPath(path, indexes, currentFunction, 
				new Return<ArrayList<BasisPath>>() {
			@Override
			public void error(CoreException e) {
				alertError(e.getMessage());
				isWorking = false;
				e.printStackTrace();
			}
			
			@Override
			public void receive(ArrayList<BasisPath> result) {
				DefaultTableModel pathModel = (DefaultTableModel) 
						table_loop_result.getModel();
				removeTableModel(table_loop_result);
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
				isWorking = false;
			}
		});
	
	}
	
	/**
	 * Áp dụng các giao diện tích hợp
	 */
	private void setIntegration(){
		if (main == null)
			return;
		FunctionCallGraph fcg = main.getFunctionCallGraph();
		int type = Integer.valueOf(group_inte_type.getSelection().getActionCommand());
		panel_function_pair.setFunctionPairList(fcg.getList(type));
	}
	
	private void beginTestFunctionPair(FunctionPair pair){
		if (isWorking){
			alertError("Việc xử lý chưa hoàn thành");
			return;
		}
		
		isWorking = true;
		
		currentFunction = pair.getCaller();
		main.beginTestFunctionPair(pair, new Return<ArrayList<BasisPath>>() {
			
			@Override
			public void error(CoreException e) {
				alertError(e.getMessage());
				isWorking = false;
				e.printStackTrace();
			}

			@Override
			public void receive(ArrayList<BasisPath> result) {
				DefaultTableModel pathModel = (DefaultTableModel) 
						table_test_pair.getModel();
				removeTableModel(table_test_pair);
				table_test_pair.setBasisPaths(result);
				
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
				isWorking = false;
			}
		});
	}
	
	/**
	 * Có sự thay đổi các tab table
	 */
	private void tableTabChanged(int index){
		if (currentFunction == null) return;
		openFuntionView(currentFunction, shouldOpenSubCondition());
	}
	
	private boolean shouldOpenSubCondition(){
		return tab_table.getSelectedComponent() == listTable
				.get(ListTable.SUBCONDITION.ordinal())
				.getParent()
				.getParent();
	}
	
	/**
	 * Hiển thị kết quả chi tiết của một đường thi hành
	 */
	private void displayPathDetails(BasisPath path, boolean subCondition){
		StatementCanvas canvas = openFuntionView(
				currentFunction, 
				subCondition
				).getCanvas();
		
		if (path == null){
			canvas.resetSelectingPath();
			return;
		}
		
		DefaultTableModel detailModel = (DefaultTableModel) table_path_details.getModel();
		DefaultTableModel testcaseModel = (DefaultTableModel) table_testcase.getModel();
		ArrayList<Pair<Statement, ArrayList<Expression>>> al = path.getAnalyzic();
		int i = 0, j = 0;
		
		canvas.setSelectedPath(path);
		removeTableModel(table_path_details);
		removeTableModel(table_testcase);
		
		for (Statement stm: path){
			if (stm instanceof ScopeStatement)
				continue;
			else if (stm instanceof FlagStatement){
				i++;
				continue;
			}
			String cs = "";
			if (al != null && j < al.size() && al.get(j).getKey() == stm)
				cs = Utils.merge(", ", al.get(j++).getValue());
			
			detailModel.addRow(new Object[]{i++, stm, cs });
		}
		
		Result r = path.getSolveResult();
		
		if (r.getSolutionCode() == Result.SUCCESS)
			for (Variable testcase: r.getSolution()){
				testcaseModel.addRow(new Object[]{
						testcase.getType(),
						testcase.getName(),
						testcase.getValueString()
				});
			}
		
		Component c = tab_info.getSelectedComponent();
		
		if (c != tb_path_details_wrap && c != tb_testcase_wrap)
			tab_info.setSelectedComponent(tb_path_details_wrap);
	}

	public GUIAll() {
		initialize();
	}

	/**
	 * Khởi tạo nội dung của khung ứng dụng.
	 */
	private void initialize() {
		listTable = new ArrayList<StorePathTable>();
		centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		frame_main = new JFrame();
		frame_main.setIconImage(Toolkit.getDefaultToolkit().getImage(GUIAll.class.getResource("/image/testing.png")));
		frame_main.setTitle("Kiểm thử tích hơp");
		frame_main.setBounds(50, 50, 1266, 630);
		frame_main.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane split_main = new JSplitPane();
		split_main.setBorder(null);
		split_main.setDividerSize(3);

		JSplitPane split_details = new JSplitPane();
		split_details.setBorder(null);
		split_details.setDividerSize(2);
		split_details.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split_main.setRightComponent(split_details);

		tab_info = new LightTabbedPane(JTabbedPane.TOP);
		tab_info.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tab_info.setBorder(null);
		split_details.setRightComponent(tab_info);
		
		panel_function_details = new JPanel();
		panel_function_details.setBackground(Color.WHITE);
		tab_info.addTab("Hàm số", null, panel_function_details, null);
		GridBagLayout gbl_panel_function_details = new GridBagLayout();
		gbl_panel_function_details.columnWidths = new int[]{23, 137, 283, 227, 0};
		gbl_panel_function_details.rowHeights = new int[]{50, 37, 35, 0};
		gbl_panel_function_details.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_function_details.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_function_details.setLayout(gbl_panel_function_details);
		
		lbl_fn_name = new JLabel("");
		lbl_fn_name.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GridBagConstraints gbc_lbl_fn_name = new GridBagConstraints();
		gbc_lbl_fn_name.insets = new Insets(0, 0, 5, 0);
		gbc_lbl_fn_name.gridwidth = 3;
		gbc_lbl_fn_name.fill = GridBagConstraints.BOTH;
		gbc_lbl_fn_name.gridx = 1;
		gbc_lbl_fn_name.gridy = 0;
		panel_function_details.add(lbl_fn_name, gbc_lbl_fn_name);
		
		JLabel lblSTestcase = new JLabel("Số testcase");
		GridBagConstraints gbc_lblSTestcase = new GridBagConstraints();
		gbc_lblSTestcase.anchor = GridBagConstraints.WEST;
		gbc_lblSTestcase.insets = new Insets(0, 0, 5, 5);
		gbc_lblSTestcase.gridx = 1;
		gbc_lblSTestcase.gridy = 1;
		panel_function_details.add(lblSTestcase, gbc_lblSTestcase);
		
		lbl_number_of_testcase = new JLabel("0");
		GridBagConstraints gbc_lbl_number_of_testcase = new GridBagConstraints();
		gbc_lbl_number_of_testcase.fill = GridBagConstraints.BOTH;
		gbc_lbl_number_of_testcase.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_number_of_testcase.gridx = 2;
		gbc_lbl_number_of_testcase.gridy = 1;
		panel_function_details.add(lbl_number_of_testcase, gbc_lbl_number_of_testcase);
		
		JButton btnQunL = new JButton("Quản lý...");
		btnQunL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectFunction == null)
					return;
				openFunctionTestcaseManager(selectFunction);
			}
		});
		GridBagConstraints gbc_btnQunL = new GridBagConstraints();
		gbc_btnQunL.insets = new Insets(0, 0, 5, 0);
		gbc_btnQunL.anchor = GridBagConstraints.WEST;
		gbc_btnQunL.gridx = 3;
		gbc_btnQunL.gridy = 1;
		panel_function_details.add(btnQunL, gbc_btnQunL);
		
		JLabel lblSHmPh = new JLabel("Số hàm phụ thuộc");
		GridBagConstraints gbc_lblSHmPh = new GridBagConstraints();
		gbc_lblSHmPh.anchor = GridBagConstraints.WEST;
		gbc_lblSHmPh.insets = new Insets(0, 0, 0, 5);
		gbc_lblSHmPh.gridx = 1;
		gbc_lblSHmPh.gridy = 2;
		panel_function_details.add(lblSHmPh, gbc_lblSHmPh);
		
		lbl_number_of_childs = new JLabel("0");
		GridBagConstraints gbc_lbl_number_of_childs = new GridBagConstraints();
		gbc_lbl_number_of_childs.fill = GridBagConstraints.BOTH;
		gbc_lbl_number_of_childs.insets = new Insets(0, 0, 0, 5);
		gbc_lbl_number_of_childs.gridx = 2;
		gbc_lbl_number_of_childs.gridy = 2;
		panel_function_details.add(lbl_number_of_childs, gbc_lbl_number_of_childs);

		tb_path_details_wrap = new JScrollPane();
		tb_path_details_wrap.setBorder(null);
		tab_info.addTab("Chi tiết đường đi", null, tb_path_details_wrap, null);
		
		table_path_details = new JTable();
		table_path_details.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"STT", "C\u00E2u l\u1EC7nh", "C\u00E1c r\u00E0ng bu\u1ED9c"
			}
		));
		table_path_details.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_path_details.getColumnModel().getColumn(0).setMinWidth(30);
		table_path_details.getColumnModel().getColumn(0).setMaxWidth(30);
		tb_path_details_wrap.setViewportView(table_path_details);
		
		tb_testcase_wrap = new JScrollPane();
		tab_info.addTab("Chi tiết nghiệm", null, tb_testcase_wrap, null);
		
		table_testcase = new JTable();
		table_testcase.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Ki\u1EC3u", "T\u00EAn bi\u1EBFn", "Gi\u00E1 tr\u1ECB"
			}
		));
		table_testcase.getColumnModel().getColumn(0).setMinWidth(75);
		table_testcase.getColumnModel().getColumn(0).setMaxWidth(75);
		table_testcase.getColumnModel().getColumn(1).setPreferredWidth(200);
		table_testcase.getColumnModel().getColumn(1).setMinWidth(50);
		table_testcase.getColumnModel().getColumn(1).setMaxWidth(400);
		for (int x = 0; x < table_testcase.getColumnCount(); x++) {
			table_testcase.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
		tb_testcase_wrap.setViewportView(table_testcase);
		tb_testcase_wrap.getViewport().setBackground(Color.WHITE);
		tb_path_details_wrap.getViewport().setBackground(Color.WHITE);
		
		for (int x = 0; x < table_path_details.getColumnCount(); x++) {
			table_path_details.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
		
		tab_table = new JTabbedPane(JTabbedPane.TOP);
		tab_table.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tab = (JTabbedPane) e.getSource();
				tableTabChanged(tab.getSelectedIndex());
			}
		});
		split_details.setLeftComponent(tab_table);
		
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
		
		JPanel panel_loop = new JPanel();
		panel_loop.setBorder(null);
		panel_loop.setBackground(Color.WHITE);
		tab_table.addTab("Các vòng lặp", null, panel_loop, null);
		
		JScrollPane tb_loop_path_wrap = new JScrollPane();
		tb_loop_path_wrap.setBorder(null);
		tb_loop_path_wrap.getViewport().setBackground(Color.WHITE);
		
		JScrollPane cv_loop_wrap = new JScrollPane();
		cv_loop_wrap.setBorder(null);
		cv_loop_wrap.getVerticalScrollBar().setUnitIncrement(16);
		
		JScrollPane tb_loop_result_wrap = new JScrollPane();
		tb_loop_result_wrap.setBorder(null);
		GroupLayout gl_panel_loop = new GroupLayout(panel_loop);
		gl_panel_loop.setHorizontalGroup(
			gl_panel_loop.createParallelGroup(Alignment.LEADING)
				.addComponent(tb_loop_path_wrap, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
				.addComponent(cv_loop_wrap, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
				.addComponent(tb_loop_result_wrap, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
		);
		gl_panel_loop.setVerticalGroup(
			gl_panel_loop.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_loop.createSequentialGroup()
					.addComponent(tb_loop_path_wrap, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cv_loop_wrap, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tb_loop_result_wrap, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
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
		tb_loop_result_wrap.setViewportView(table_loop_result);
		
		tb_loop_result_wrap.getViewport().setBackground(Color.WHITE);
		
		LoopCanvas canvas_loop = new LoopCanvas();
		canvas_loop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (currentFunction == null) return;
				StatementCanvas canvas = openFuntionView(
						currentFunction, false).getCanvas();
				canvas.resetSelectingExtraPath();
			}
		});
		canvas_loop.setBackground(Color.WHITE);
		cv_loop_wrap.setViewportView(canvas_loop);
		
		canvas_loop.setOnNodeSelect(new OnNodeSelect() {
			@Override
			public void selected(LoopNode node) {
				StatementCanvas canvas = openFuntionView(
						currentFunction, false
						).getCanvas();
				canvas.setSelectedExtraPath(node.getStatement().getOriginList());
			}
		});
		canvas_loop.setOnApplyValue(new OnApplyValue() {
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
		tb_loop_path_wrap.setViewportView(table_loop_path);
		
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
				removeTableModel(table_loop_result);
				
				Object index = table.getValueAt(table.getSelectedRow(), 0);
				
				if (index != null){
					int i = Integer.valueOf(index + "") - 1;
					canvas_loop.setLoopPath(table.getLoopPaths().get(i));
				}
			}
		});
		
		panel_loop.setLayout(gl_panel_loop);
		
		panel_inte = new JPanel();
		panel_inte.setBackground(Color.WHITE);
		tab_table.addTab("Tích hợp", null, panel_inte, null);
		
		JScrollPane pn_function_pair_wrap = new JScrollPane();
		pn_function_pair_wrap.setBorder(null);
		pn_function_pair_wrap.getVerticalScrollBar().setUnitIncrement(16);
		
		JPanel panel_inte_type = new JPanel();
		panel_inte_type.setBackground(Color.WHITE);
		
		JScrollPane tb_test_pair_wrap = new JScrollPane();
		tb_test_pair_wrap.getViewport().setBackground(Color.WHITE);
		GroupLayout gl_panel_inte = new GroupLayout(panel_inte);
		gl_panel_inte.setHorizontalGroup(
			gl_panel_inte.createParallelGroup(Alignment.LEADING)
				.addComponent(panel_inte_type, GroupLayout.DEFAULT_SIZE, 754, Short.MAX_VALUE)
				.addGroup(gl_panel_inte.createSequentialGroup()
					.addComponent(pn_function_pair_wrap, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE)
					//.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tb_test_pair_wrap, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE))
		);
		gl_panel_inte.setVerticalGroup(
			gl_panel_inte.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_inte.createSequentialGroup()
					.addComponent(panel_inte_type, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					//.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_inte.createParallelGroup(Alignment.LEADING)
						.addComponent(tb_test_pair_wrap, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
						.addComponent(pn_function_pair_wrap, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)))
		);
		
		table_test_pair = new StorePathTable(false);
		table_test_pair.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"STT", "\u0110\u01B0\u1EDDng d\u1EABn", "Testcases", "Return"
				}
			));
		table_test_pair.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_test_pair.getColumnModel().getColumn(0).setMinWidth(30);
		table_test_pair.getColumnModel().getColumn(0).setMaxWidth(30);
		table_test_pair.getColumnModel().getColumn(3).setPreferredWidth(70);
		table_test_pair.getColumnModel().getColumn(3).setMaxWidth(70);
		tb_test_pair_wrap.setViewportView(table_test_pair);
		
		listTable.add(table_test_pair);
		
		panel_function_pair = new FunctionPairCanvas();
		panel_function_pair.setOnItemSelected(new FunctionPairCanvas.OnItemSelected() {
			@Override
			public void selected(FunctionPairNode node, boolean dbClick) {
				canvas_fn_call.setSelectFunctionPair(node.getFunctionPair());
				tab_canvas.setSelectedComponent(cv_fn_call_wrap);
				if (dbClick)
					beginTestFunctionPair(node.getFunctionPair());
			}
		});
		panel_function_pair.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				canvas_fn_call.clearAllSelectedFunctionPair();
			}
			
		});
		pn_function_pair_wrap.setViewportView(panel_function_pair);
		panel_inte_type.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		
		JRadioButton rd_bottom_up = new JRadioButton("Bottom-up");
		rd_bottom_up.setSelected(true);
		rd_bottom_up.setActionCommand("0");
		panel_inte_type.add(rd_bottom_up);
		
		JRadioButton rd_top_down = new JRadioButton("Top-down");
		rd_top_down.setActionCommand("1");
		panel_inte_type.add(rd_top_down);
		panel_inte.setLayout(gl_panel_inte);
		
		group_inte_type = new ButtonGroup();
		group_inte_type.add(rd_bottom_up);
		group_inte_type.add(rd_top_down);

		split_details.setDividerLocation(400);
		split_main.setDividerLocation(600);
		
		JPanel panel_tray = new JPanel();
		panel_tray.setBackground(Color.WHITE);
		
		JPanel panel_toolbar = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame_main.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addComponent(panel_tray, GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
				.addComponent(panel_toolbar, GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
				.addComponent(split_main, GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel_toolbar, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(split_main, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_tray, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
		);
		panel_toolbar.setBorder(new MatteBorder(0, 0, 1, 0, (Color) Color.LIGHT_GRAY));
		
				JPanel panel_toolbar_left = new JPanel();
				
						JButton btn_open_c = new JButton();
						btn_open_c.setMargin(new Insets(2, 5, 2, 5));
						btn_open_c.setPreferredSize(new Dimension(90, 30));
						btn_open_c.setToolTipText("Mở tập tin C/thư mục");
						btn_open_c.setText("Mở C");
						btn_open_c.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								openCFiles();
							}
						});
						btn_open_c.setIcon(new ImageIcon(GUIAll.class.getResource("/image/c.png")));

						JButton btn_set_root = new JButton();
						btn_set_root.setMargin(new Insets(2, 5, 2, 5));
						btn_set_root.setPreferredSize(new Dimension(100, 30));
						btn_set_root.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								openSelectFunction();
							}
						});
						btn_set_root.setIcon(new ImageIcon(GUIAll.class.getResource("/image/root.png")));
						btn_set_root.setToolTipText("Đặt hàm số gốc tùy chỉnh");
						btn_set_root.setText("Đặt gốc");
						panel_toolbar_left.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
						panel_toolbar_left.add(btn_open_c);
						
						JButton btn_open_j = new JButton();
						btn_open_j.setMargin(new Insets(2, 5, 2, 5));
						btn_open_j.setPreferredSize(new Dimension(100, 30));
						btn_open_j.setIcon(new ImageIcon(GUIAll.class.getResource("/image/java.png")));
						btn_open_j.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								openJavaFiles();
							}
						});
						btn_open_j.setToolTipText("Mở tập tin C/thư mục");
						btn_open_j.setText("Mở Java");
						panel_toolbar_left.add(btn_open_j);
						panel_toolbar_left.add(btn_set_root);
						
						JPanel panel_toolbar_right = new JPanel();
						GroupLayout gl_panel_toolbar = new GroupLayout(panel_toolbar);
						gl_panel_toolbar.setHorizontalGroup(
							gl_panel_toolbar.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_toolbar.createSequentialGroup()
									.addComponent(panel_toolbar_left, GroupLayout.PREFERRED_SIZE, 601, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
									.addComponent(panel_toolbar_right, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
						);
						gl_panel_toolbar.setVerticalGroup(
							gl_panel_toolbar.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_toolbar_left, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
								.addComponent(panel_toolbar_right, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
						);
						panel_toolbar_right.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
						
						JButton btn_setting = new JButton("Cài đặt");
						btn_setting.setMargin(new Insets(2, 5, 2, 5));
						btn_setting.setIcon(new ImageIcon(GUIAll.class.getResource("/image/setting.png")));
						btn_setting.setPreferredSize(new Dimension(90, 30));
						btn_setting.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								new SettingDialog(frame_main).setVisible(true);
							}
						});
						panel_toolbar_right.add(btn_setting);
						
						JButton btn_about = new JButton("");
						btn_about.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								new AboutDialog(frame_main).setVisible(true);
							}
						});
						btn_about.setIcon(new ImageIcon(GUIAll.class.getResource("/image/info.png")));
						btn_about.setHorizontalTextPosition(SwingConstants.LEFT);
						btn_about.setPreferredSize(new Dimension(30, 30));
						panel_toolbar_right.add(btn_about);
						panel_toolbar.setLayout(gl_panel_toolbar);
		panel_tray.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		
		lbl_status = new JLabel("");
		lbl_status.setVisible(false);
		panel_tray.add(lbl_status);
		
		lbl_loading = new JLabel("");
		lbl_loading.setVisible(false);
		lbl_loading.setIcon(new ImageIcon(GUIAll.class.getResource("/image/loading.gif")));
		panel_tray.add(lbl_loading);
		
		tab_canvas = new LightTabbedPane(JTabbedPane.TOP);
		tab_canvas.setBorder(null);
		split_main.setLeftComponent(tab_canvas);

		cv_fn_call_wrap = new DragScrollPane();
		cv_fn_call_wrap.setBorder(null);

		canvas_fn_call = new FunctionCanvas();
		canvas_fn_call.setBorder(null);
		cv_fn_call_wrap.setViewportView(canvas_fn_call);
		tab_canvas.addTab("Đồ thị gọi hàm", null, cv_fn_call_wrap, null);
		frame_main.getContentPane().setLayout(groupLayout);

		fileChooserC = new JFileChooser();
		FileNameExtensionFilter cFilter = new FileNameExtensionFilter(
				"Mã nguồn C (*.c; *.cpp)", new String[] { "C", "CPP" });
		fileChooserC.setMultiSelectionEnabled(true);
		fileChooserC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserC.setPreferredSize(SIZE_CHOOSER);
		fileChooserC.setFileFilter(cFilter);
		
		fileChooserJ = new JFileChooser();
		FileNameExtensionFilter jFilter = new FileNameExtensionFilter(
				"Mã nguồn Java (*.java)", new String[] { "JAVA" });
		fileChooserJ.setMultiSelectionEnabled(true);
		fileChooserJ.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserJ.setPreferredSize(SIZE_CHOOSER);
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
						displayPathDetails(null, table.isSubCondition());
					else{
						int i = Integer.valueOf(index + "") - 1;
						displayPathDetails(table.getBasisPaths().get(i), table.isSubCondition());
					}
				}
			});
		}
		
		Enumeration<AbstractButton> iter = group_inte_type.getElements();
		while (iter.hasMoreElements())
			iter.nextElement().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setIntegration();
				}
			});
		
		
	}
	
	/**
	 * Xóa hết các ô trong một bảng
	 */
	private static void removeTableModel(JTable table){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for (int j = model.getRowCount()-1;j>=0;j--)
			model.removeRow(j);
	}
	
	private static final Dimension SIZE_CHOOSER = 
			new Dimension(SCREEN.WIDTH/2, SCREEN.HEIGHT/2);
	private JScrollPane cv_fn_call_wrap;
	private JPanel panel_function_details;
	private JScrollPane tb_path_details_wrap;
	private JScrollPane tb_testcase_wrap;
	private JPanel panel_inte;
	
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
		JOptionPane.showMessageDialog(frame_main, s, "Errors",
				JOptionPane.ERROR_MESSAGE);
	}
}
