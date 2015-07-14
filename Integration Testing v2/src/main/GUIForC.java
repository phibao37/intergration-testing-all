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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import cdt.CMainProcess;
import cdt.SelectFunction;
import core.GUI;
import core.error.MainNotFoundException;
import core.graph.CFGView;
import core.graph.FileView;
import core.graph.LightTabbedPane;
import core.graph.adapter.FunctionAdapter;
import core.graph.canvas.FunctionCanvas;
import core.models.Function;
import core.solver.Solver.Result;
import core.unit.BasisPath;

import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Lớp giao diện hiển thị của ứng dụng
 * 
 * @author ducvu
 */
public class GUIForC extends GUI {

	private JFrame frmMain;
	private FunctionCanvas fGraph;
	//private VCanvas vGraph;
	private JFileChooser fileChooser;
	private LightTabbedPane infoTab;
	private LightTabbedPane tabbedCanvas;

	private DefaultTableModel pathModel;
	private JTable table;
	private Function preRoot;
	
	/** Tiến trình chính của ứng dụng */
	private CMainProcess main;

	/**
	 * Chạy ứng dụng
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIForC window = new GUIForC();
					window.frmMain.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Mở hộp thoại để chọn tập tin */
	private void openCFiles() {
		int status = fileChooser.showDialog(frmMain, "Mở tập tin C/thư mục");

		if (status == JFileChooser.APPROVE_OPTION) {
			tabbedCanvas.setSelectedIndex(0);
			try {
				main.setWorkingFiles(fileChooser.getSelectedFiles(), true);
				main.run();
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
	public void openFuntionView(Function fn, boolean subCondition){
		try {
			tabbedCanvas.openTab(fn.getName(), null, fn.getNameAndFile(),
					CFGView.class.getConstructor(Function.class, boolean.class),
					fn, subCondition);
		} catch (Exception e) {}
	}

	@Override
	public void beginTestFunction(Function func) {
		ArrayList<BasisPath> paths = main.beginTestFunction(func);
		int i;
		
		for (i = pathModel.getRowCount()-1;i>=0;i--)
			pathModel.removeRow(i);
		
		i = 0;
		for (BasisPath path: paths){
			Result result = path.getSolveResult();
			pathModel.addRow(new Object[]{
				i,
				path.toStringSkipMarkdown(),
				result.getSolutionMessage(),
				result.getReturnValue()
			});
			i++;
		}
		pathModel.addRow(new Object[]{null, null, null, null});
		
		openFileView(func.getSourceFile());
		openFuntionView(func, true);
	}

	@Override
	public int getDefaultCanvasWidth() {
		return fGraph.getWidth();
	}

	public GUIForC() {
		initialize();
	}

	/**
	 * Khởi tạo nội dung của khung ứng dụng.
	 */
	private void initialize() {
		main = new CMainProcess();
		
		frmMain = new JFrame();
		frmMain.setTitle("Kiểm thử tích hơp cho C");
		frmMain.setBounds(50, 50, 1266, 630);
		frmMain.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel_toolbar = new JPanel();
		panel_toolbar.setBorder(new MatteBorder(0, 0, 1, 0, (Color) Color.LIGHT_GRAY));

		JSplitPane splitPane_main = new JSplitPane();
		splitPane_main.setBorder(null);
		splitPane_main.setDividerSize(3);

		JSplitPane detailWrap = new JSplitPane();
		detailWrap.setBorder(null);
		detailWrap.setDividerSize(2);
		detailWrap.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_main.setRightComponent(detailWrap);

		JScrollPane extraWrap = new JScrollPane();
		extraWrap.setBorder(null);
		detailWrap.setLeftComponent(extraWrap);
		
		table = new JTable();
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

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int x = 0; x < table.getColumnCount(); x++) {
			table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
		pathModel = (DefaultTableModel) table.getModel();

		infoTab = new LightTabbedPane(JTabbedPane.TOP);
		infoTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		infoTab.setBorder(null);
		detailWrap.setRightComponent(infoTab);

		JScrollPane infoWrap = new JScrollPane();
		infoWrap.setBorder(null);
		infoTab.addTab("Chi tiết", null, infoWrap, null);
		infoTab.setTabCloseableAt(0, false);

		JPanel infoView = new JPanel();
		infoView.setBorder(null);
		infoView.setBackground(Color.WHITE);
		infoWrap.setViewportView(infoView);

		detailWrap.setDividerLocation(250);
		splitPane_main.setDividerLocation(600);

		JLabel lbl_open_file = new JLabel();
		lbl_open_file.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_open_file.setBounds(0, 5, 80, 30);
		lbl_open_file.setToolTipText("Mở tập tin C/thư mục");
		lbl_open_file.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_open_file.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		lbl_open_file.setOpaque(true);
		lbl_open_file.setBackground(SystemColor.controlHighlight);
		lbl_open_file.setText("Mở ...");
		lbl_open_file.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openCFiles();
			}
		});
		lbl_open_file.setIcon(new ImageIcon(GUIForC.class
				.getResource("/image/file.png")));
		GroupLayout groupLayout = new GroupLayout(frmMain.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_toolbar, GroupLayout.PREFERRED_SIZE, 1230, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(splitPane_main, GroupLayout.DEFAULT_SIZE, 1342, Short.MAX_VALUE)
							.addGap(10))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel_toolbar, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(splitPane_main, GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
					.addGap(11))
		);
		
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
		//tabbedCanvas.setTabCloseableAt(1, false);

		JLabel lbl_set_root = new JLabel();
		lbl_set_root.setBounds(90, 5, 100, 30);
		lbl_set_root.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openSelectFunction();
			}
		});
		lbl_set_root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_set_root.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_set_root.setIcon(new ImageIcon(GUIForC.class
				.getResource("/image/root.png")));
		lbl_set_root.setToolTipText("Đặt hàm số gốc tùy chỉnh");
		lbl_set_root.setText("Đặt gốc...");
		lbl_set_root.setOpaque(true);
		lbl_set_root.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		lbl_set_root.setBackground(SystemColor.controlHighlight);
		panel_toolbar.setLayout(null);
		panel_toolbar.add(lbl_open_file);
		panel_toolbar.add(lbl_set_root);
		frmMain.getContentPane().setLayout(groupLayout);

		fileChooser = new JFileChooser();
		FileNameExtensionFilter cFilter = new FileNameExtensionFilter(
				"Mã nguồn C (*.c; *.cpp)", new String[] { "C", "CPP" });
		fileChooser.setMultiSelectionEnabled(true);
		// fileChooser.setFileHidingEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(cFilter);
	}
	
	/*----------------- LOG AND ERROR -----------------*/
	
	/** Bật thông báo lỗi với nội dung chỉ định */
	public void alertError(String s) {
		JOptionPane.showMessageDialog(frmMain, s, "Errors",
				JOptionPane.ERROR_MESSAGE);
	}
	
}
