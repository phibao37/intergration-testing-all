package main;

import core.GUI;
import core.MainProcess;
import core.MainProcess.Return;
import core.MainProcess.Returned;
import core.S.SCREEN;
import core.Utils;
import core.error.CoreException;
import core.graph.DragScrollPane;
import core.graph.FileView;
import core.graph.LightTabbedPane;
import core.graph.canvas.LoopCanvas;
import core.graph.canvas.StatementCanvas;
import core.models.*;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.solver.Solver.Result;
import core.unit.BasisPath;
import core.unit.LoopablePath;
import javafx.util.Pair;
import jdt.JMainProcess;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Lớp giao diện hiển thị của ứng dụng
 * 
 * @author ducvu
 */
public class GUIAll extends GUI {

	/** Tiến trình chính của ứng dụng */
	private MainProcess main;
	private Function preRoot;
	private Function currentFunction;
	private boolean isWorking;
	private Timer timer;
	
	private JFrame frame_main;
	private JFileChooser fileChooserC, fileChooserJ;
	private JLabel lbl_loading;
	private JLabel lbl_status;
	private StoreLoopTable table_loop_path;
	private StorePathTable table_loop_result;
	private JTable table_testcase;
	private JTable table_path_details;

	private ArrayList<StorePathTable> listTable;
	private LightTabbedPane tab_info;
	private LightTabbedPane tab_canvas;
	private JTabbedPane tab_table;
	
	private enum ListTable{
		STATEMENT ("Phủ câu lệnh"), 
		CONDITION ("Phủ nhánh"), 
		SUBCONDITION ("Phủ điều kiện con"),
		ALLPATH ("Tất cả nhánh");
		
		private final String mValue;
		private Component mComponent;
		
		ListTable(String value){ mValue = value; }
		public String toString() { return mValue; }
		
		public void setComponent(Component c){ mComponent = c; }
		public Component getComponent(){ return mComponent; }
	}
	private static final Dimension SIZE_CHOOSER = 
			new Dimension(SCREEN.WIDTH/2, SCREEN.HEIGHT/2);
	
	private JScrollPane tb_path_details_wrap;
	private JScrollPane tb_testcase_wrap;
	private StatementCanvas canvas_12;
	private StatementCanvas canvas_3;
	private JLabel lbl_fn_name;
	
	/**
	 * Chạy ứng dụng
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
	 * Đặt hàm số cần kiểm thử, hiển thị ra đồ thị
	 */
	private void setSelectedFunction(Function fn){
		currentFunction = fn;
		lbl_fn_name.setText(Utils.html(fn.getHTMLContent()));
		for (JTable table: listTable)
			removeTableModel(table);
		
		canvas_12.setFunction(fn, false);
		canvas_3.setFunction(fn, true);
		openCanvas(tab_table.getSelectedComponent() 
				== ListTable.SUBCONDITION.getComponent());
		openFileView(fn.getSourceFile());
	}
	
	/**
	 * Mở hộp thoại chọn tập tin Java
	 */
	private void openJavaFiles() {
		int status = fileChooserJ.showDialog(frame_main, "Mở tập tin Java");

		if (status == JFileChooser.APPROVE_OPTION) {
			try {
				main.setWorkingFiles(fileChooserJ.getSelectedFiles(), true);
				main.loadFunctionFromFiles();
				if (main.isEmptyFunction()){
					JOptionPane.showMessageDialog(frame_main, "Không có hàm nào!");
					return;
				}
				
				ArrayList<Function> fnList = main.getFunctionList();
				if (fnList.size() == 1)
					setSelectedFunction(fnList.get(0));
				else
					openSelectDialog();
			} 
			catch (Exception e) {
				alertError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Mở hộp thoại để chọn hàm kiểm thử từ danh sách
	 */
	private void openSelectDialog() {
		if (main == null || main.isEmptyFunction())
			return;
		ArrayList<Function> funcs = main.getFunctionList();
		SelectFunction select = new SelectFunction(frame_main, funcs, preRoot);
		Function selected = select.showDialog();

		if (selected == null)
			return;
		
		preRoot = selected;
		setSelectedFunction(selected);
	}
	
	/**
	 * Chuyển sang và trả về canvas ứng với đồ thị phủ cấp 1,2 hoặc 3
	 */
	private StatementCanvas openCanvas(boolean subCondition){
		if (subCondition){
			tab_canvas.setSelectedIndex(1);
			return canvas_3;
		} else {
			tab_canvas.setSelectedIndex(0);
			return canvas_12;
		}
	}

	/*-------------- BASE GUI IMPLEMENT -------------*/
	
	@Override
	public void openFileView(File file) {
		try {
			tab_info.openTab(file.getName(), null, file.getAbsolutePath(),
					FileView.class.getConstructor(File.class), file);
		} catch (Exception ignored) {}
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
						boolean shouldAdd = true;
						
						for (int j = loopablePath.size() - 1; j >= 0; j--){
							LoopablePath l2 = loopablePath.get(j);
							
							if (l.isCover(l2)){
								loopablePath.remove(j);
								loopPath.remove(j);
							}
							else if (l2.isCover(l)){
								shouldAdd = false;
								break;
							}
						}
						
						if (shouldAdd){
							loopablePath.add(l);
							loopPath.add(path);
						}
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
							r.getReturnValue(),
							r.getSolver()
						});
					}
					pathModel.addRow(new Object[]{null, null, null, null, null});
				}
				setStatus(null);
				isWorking = false;
			}
		});
	
	}
	

	@Override
	public void setStatus(String status, Object... args){
		if (timer.isRunning()){
			timer.stop();
		}
		if (status == null || status.isEmpty()){
			lbl_loading.setVisible(false);
			lbl_status.setVisible(false);
		}
		else {
			lbl_loading.setVisible(true);
			lbl_status.setVisible(true);
			lbl_status.setText(Utils.format(status, args));
		}
	}
	
	@Override
	public void setStatus(int second, String status, Object... args) {
		lbl_loading.setVisible(false);
		lbl_status.setVisible(true);
		lbl_status.setText(Utils.format(status, args));
		
		if (timer.isRunning())
			timer.stop();
		timer.setInitialDelay(second * 1000);
		timer.start();
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
						r.getReturnValue(),
						r.getSolver()
					});
				}
				pathModel.addRow(new Object[]{null, null, null, null, null});
				isWorking = false;
			}
		});
	
	}
	
	/**
	 * Hiển thị kết quả chi tiết của một đường thi hành
	 */
	private void displayPathDetails(BasisPath path, boolean subCondition){
		StatementCanvas canvas = openCanvas(subCondition);
		
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
		
		if (r.getSolutionCode() == Result.SUCCESS){
			i = 0;
			Variable[] afters = r.getAfterSolution();
			
			for (Variable testcase: r.getSolution()){
				if (testcase instanceof ArrayVariable){
					testcaseModel.addRow(new Object[]{
							testcase.getType(),
							testcase.getName(), "{}", "{}"
					});
				
					Map<int[], Expression> 
							m1 = ((ArrayVariable)testcase).getAllValue(), 
							m2 = ((ArrayVariable)afters[i++]).getAllValue();
					int row = testcaseModel.getRowCount();	
					for (j = 0; j < Math.max(m1.size(), m2.size()); j++)
						testcaseModel.addRow(new Object[]{});
					
					j = 0;
					for (Entry<int[], Expression> entry: m1.entrySet())
						testcaseModel.setValueAt(
								String.format("[%s] => %s", 
										Utils.merge("][", entry.getKey()), 
										entry.getValue()), 
								row + j++, 2);
					
					j = 0;
					for (Entry<int[], Expression> entry: m2.entrySet())
						testcaseModel.setValueAt(
								String.format("[%s] => %s", 
										Utils.merge("][", entry.getKey()), 
										entry.getValue()), 
								row + j++, 3);
				} 
				else
					testcaseModel.addRow(new Object[]{
							testcase.getType(),
							testcase.getName(),
							testcase.getValueString(),
							afters[i++].getValueString()
					});
			}
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

		main = new JMainProcess();
		listTable = new ArrayList<>();
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		timer = new Timer(0, e -> setStatus(null));
		timer.setRepeats(false);
		
		frame_main = new JFrame();
		frame_main.setIconImage(Toolkit.getDefaultToolkit().getImage(GUIAll.class.getResource("/image/testing.png")));
		frame_main.setTitle("Kiểm thử đơn vị Java");
		frame_main.setBounds(50, 50, 1266, 630);
		frame_main.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame_main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
				"Ki\u1EC3u", "T\u00EAn bi\u1EBFn", "Gi\u00E1 tr\u1ECB", "Gi\u00E1 tr\u1ECB sau"
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
		split_details.setLeftComponent(tab_table);
		
		/*---------BEGIN ADD TABLES-----------*/
		
		
		int index = 0;
		for (ListTable entry: ListTable.values()){
			JScrollPane extraWrap = new JScrollPane();
			extraWrap.setBorder(null);
			
			StorePathTable table = new StorePathTable(index++ == 2);
			table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"STT", "Đường dẫn", "Testcases", "Return", "Bộ giải"
				}
			));
			table.getColumnModel().getColumn(0).setPreferredWidth(30);
			table.getColumnModel().getColumn(0).setMinWidth(30);
			table.getColumnModel().getColumn(0).setMaxWidth(30);
			table.getColumnModel().getColumn(3).setPreferredWidth(70);
			table.getColumnModel().getColumn(3).setMaxWidth(70);
			table.getColumnModel().getColumn(4).setMaxWidth(60);
			extraWrap.setViewportView(table);
			extraWrap.getViewport().setBackground(Color.WHITE);
			
			tab_table.add(entry.toString(), extraWrap);
			entry.setComponent(extraWrap);
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
					"STT", "Đường dẫn", "Testcases", "Return", "Bộ giải"
				}
			));
		table_loop_result.getColumnModel().getColumn(0).setPreferredWidth(30);
		table_loop_result.getColumnModel().getColumn(0).setMinWidth(30);
		table_loop_result.getColumnModel().getColumn(0).setMaxWidth(30);
		table_loop_result.getColumnModel().getColumn(3).setPreferredWidth(70);
		table_loop_result.getColumnModel().getColumn(3).setMaxWidth(70);
		table_loop_result.getColumnModel().getColumn(4).setMaxWidth(60);
		tb_loop_result_wrap.setViewportView(table_loop_result);
		
		tb_loop_result_wrap.getViewport().setBackground(Color.WHITE);
		
		LoopCanvas canvas_loop = new LoopCanvas();
		canvas_loop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (currentFunction == null) return;
				openCanvas(false).resetSelectingExtraPath();
			}
		});
		canvas_loop.setBackground(Color.WHITE);
		cv_loop_wrap.setViewportView(canvas_loop);
		
		canvas_loop.setOnNodeSelect(node -> {
            openCanvas(false).setSelectedExtraPath(node.getStatement().getOriginList());
        });
		canvas_loop.setOnApplyValue((indexes, path) -> beginTestLoop(path, indexes));
		
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
		table_loop_path.getSelectionModel().addListSelectionListener(e -> {
            StoreLoopTable table = table_loop_path;
            if (e.getValueIsAdjusting() || table.getSelectedRow() == -1)
                return;
            openCanvas(false).resetSelectingExtraPath();
            removeTableModel(table_loop_result);

            Object index1 = table.getValueAt(table.getSelectedRow(), 0);

            if (index1 != null){
                int i = Integer.valueOf(index1 + "") - 1;
                canvas_loop.setLoopPath(table.getLoopPaths().get(i));
            }
        });
		
		panel_loop.setLayout(gl_panel_loop);
		

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
		panel_toolbar.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		
				JPanel panel_toolbar_left = new JPanel();

						JButton btn_set_root = new JButton();
						btn_set_root.setMargin(new Insets(2, 5, 2, 5));
						btn_set_root.setPreferredSize(new Dimension(100, 30));
						btn_set_root.addActionListener(e -> openSelectDialog());
						btn_set_root.setIcon(new ImageIcon(GUIAll.class.getResource("/image/root.png")));
						btn_set_root.setToolTipText("Chọn hàm kiểm thử từ danh sách");
						btn_set_root.setText("Đặt unit");
						panel_toolbar_left.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
						
						JButton btn_open_j = new JButton();
						btn_open_j.setMargin(new Insets(2, 5, 2, 5));
						btn_open_j.setPreferredSize(new Dimension(100, 30));
						btn_open_j.setIcon(new ImageIcon(GUIAll.class.getResource("/image/java.png")));
						btn_open_j.addActionListener(e -> openJavaFiles());
						btn_open_j.setToolTipText("Mở tập tin C/thư mục");
						btn_open_j.setText("Mở tập tin");
						panel_toolbar_left.add(btn_open_j);
						panel_toolbar_left.add(btn_set_root);
						
						JPanel panel_toolbar_right = new JPanel();
						
						lbl_fn_name = new JLabel("");
						lbl_fn_name.setFont(new Font("Tahoma", Font.PLAIN, 22));
						lbl_fn_name.setHorizontalAlignment(SwingConstants.CENTER);
						GroupLayout gl_panel_toolbar = new GroupLayout(panel_toolbar);
						gl_panel_toolbar.setHorizontalGroup(
							gl_panel_toolbar.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel_toolbar.createSequentialGroup()
									.addComponent(panel_toolbar_left, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lbl_fn_name, GroupLayout.DEFAULT_SIZE, 898, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(panel_toolbar_right, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						);
						gl_panel_toolbar.setVerticalGroup(
							gl_panel_toolbar.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_toolbar_right, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
								.addComponent(panel_toolbar_left, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
								.addComponent(lbl_fn_name, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
						);
						
						JButton btn_run = new JButton();
						btn_run.addActionListener(e -> {
							if (currentFunction == null) return;
							beginTestFunction(currentFunction);
						});
						btn_run.setIcon(new ImageIcon(GUIAll.class.getResource("/image/run.png")));
						btn_run.setToolTipText("Kiểm thử hàm đơn vị");
						btn_run.setText("Kiểm thử");
						btn_run.setPreferredSize(new Dimension(100, 30));
						btn_run.setMargin(new Insets(2, 5, 2, 5));
						panel_toolbar_left.add(btn_run);
						panel_toolbar_right.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
						
						JButton btn_setting = new JButton("Cài đặt");
						btn_setting.setMargin(new Insets(2, 5, 2, 5));
						btn_setting.setIcon(new ImageIcon(GUIAll.class.getResource("/image/setting.png")));
						btn_setting.setPreferredSize(new Dimension(90, 30));
						btn_setting.addActionListener(e -> new SettingDialog(frame_main).setVisible(true));
						panel_toolbar_right.add(btn_setting);
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
		
		JScrollPane canvas_12_wrap = new DragScrollPane();
		tab_canvas.addTab("Đồ thị phủ cấp 1,2", null, canvas_12_wrap, null);
		
		canvas_12 = new StatementCanvas();
		canvas_12_wrap.setViewportView(canvas_12);
		
		JScrollPane canvas_3_wrap = new DragScrollPane();
		tab_canvas.addTab("Đồ thị phủ cấp 3", null, canvas_3_wrap, null);
		
		canvas_3 = new StatementCanvas();
		canvas_3_wrap.setViewportView(canvas_3);

		frame_main.getContentPane().setLayout(groupLayout);

		fileChooserC = new JFileChooser();
		FileNameExtensionFilter cFilter = new FileNameExtensionFilter(
				"Mã nguồn C (*.c; *.cpp)", "C", "CPP");
		fileChooserC.setMultiSelectionEnabled(true);
		fileChooserC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserC.setPreferredSize(SIZE_CHOOSER);
		fileChooserC.setFileFilter(cFilter);
		
		fileChooserJ = new JFileChooser();
		FileNameExtensionFilter jFilter = new FileNameExtensionFilter(
				"Mã nguồn Java (*.java)", "JAVA");
		fileChooserJ.setMultiSelectionEnabled(true);
		fileChooserJ.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserJ.setPreferredSize(SIZE_CHOOSER);
		fileChooserJ.setFileFilter(jFilter);
		
		for (StorePathTable table: listTable){
			for (int x = 0; x < table.getColumnCount(); x++) {
				table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
			}
			table.getSelectionModel().addListSelectionListener(e -> {
                if (e.getValueIsAdjusting() || table.getSelectedRow() == -1)
                    return;
                Object index1 = table.getValueAt(table.getSelectedRow(), 0);

                if (index1 == null)
                    displayPathDetails(null, table.isSubCondition());
                else{
                    int i = Integer.valueOf(index1 + "") - 1;
                    displayPathDetails(table.getBasisPaths().get(i), table.isSubCondition());
                }
            });
		}
		
		tab_table.addChangeListener(e -> {
			openCanvas(tab_table.getSelectedComponent() == ListTable.SUBCONDITION.getComponent());
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
