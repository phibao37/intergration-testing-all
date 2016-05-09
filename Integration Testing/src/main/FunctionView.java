package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

import api.models.ICFG;
import api.models.IFunction;
import api.models.IFunctionTestResult;
import api.models.ITestpath;
import core.Config;
import core.Utils;
import graph.swing.CFGView;
import graph.swing.CoverageView;
import graph.swing.CoverageView.TableSelectionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class FunctionView extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JSplitPane split_main;
	private JTabbedPane tab_coverage;
	private IFunction fn;
	private CFGView cfg, cfg3, current_cfg;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
							 "javax.swing.plaf.nimbus.NimbusLookAndFeel");
					FunctionView dialog = new FunctionView(null, null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void toggleType(int coverage){
		
		if (coverage == ICFG.COVER_BRANCH){
			if (cfg == null)
				cfg = new CFGView(fn, coverage, null);
			if (current_cfg != cfg){
				current_cfg = cfg;
				int loc = split_main.getDividerLocation();
				split_main.setLeftComponent(current_cfg);
				split_main.setDividerLocation(loc);
			}
		}
		else if (coverage == ICFG.COVER_SUBCONDITION){
			if (cfg3 == null)
				cfg3 = new CFGView(fn, coverage, null);
			if (current_cfg != cfg3){
				current_cfg = cfg3;
				int loc = split_main.getDividerLocation();
				split_main.setLeftComponent(current_cfg);
				split_main.setDividerLocation(loc);
			}
		}
		
	}
	
	private void toggleCFGView(boolean show){
		if (show){
			split_main.setDividerSize(4);
			split_main.setDividerLocation(370);
			
			toggleType(getCoverageTypeFromSelected());
		} else {
			split_main.setDividerSize(0);
			split_main.setDividerLocation(0);
			
			current_cfg = null;
			split_main.setLeftComponent(null);
		}
	}
	
	private void init2(){
		toggleCFGView(Config.SHOW_CFG_DETAILS);

		String[] COVER_NAMES = {"Statement coverage", "Branch coverage",
				"Sub condition coverage", "All path"};
		IFunctionTestResult r = fn.getTestResult();
		TableSelectionListener listener = (t, row) -> {
			if (row == -1 || current_cfg == null) return;
			current_cfg.setHightLightTestpath((ITestpath) t.getValueAt(row, 1));
		};
		
		for (int i = IFunctionTestResult.STATEMENT;
				i <= IFunctionTestResult.ALLPATH; i++){
			
			CoverageView cv = new CoverageView();
			String name = COVER_NAMES[i];
			
			if (r != null){
				name = Utils.htmlCenter(name + "<br/>("
						+ r.getPercent(i)
						+ "%)");
				cv.setModel(r.getTestpaths(i));
			}
			
			tab_coverage.add(name, cv);
			cv.setTableSelectListener(listener);
		}
		
		{
			CoverageView cv = new CoverageView();
			String name = "Error path";
			
			if (r != null){
				List<ITestpath> list_path = r.getTestpaths(
						IFunctionTestResult.ERROR);
				name = Utils.htmlCenter(name + "<br/>("
				+ list_path.size() + ")");
				cv.setModel(list_path);
			}
			
			tab_coverage.add(name, cv);
			cv.setTableSelectListener(listener);
		}
		
		{
			CoverageView cv = new CoverageView();
			String name = "Loop path";
			
			if (r != null){
				List<ITestpath> list_path = r.getTestpaths(
						IFunctionTestResult.LOOP);
				name = Utils.htmlCenter(name + "<br/>("
				+ list_path.size() + ")");
				cv.setModel(list_path);
			}
			
			tab_coverage.add(name, cv);
			cv.setTableSelectListener(listener);
		}
		
	}
	
	private int getCoverageTypeFromSelected(){
		int selected = tab_coverage.getSelectedIndex();
		
		if (selected == IFunctionTestResult.SUBCONDITION)
			return ICFG.COVER_SUBCONDITION;
		else
			return ICFG.COVER_BRANCH;
	}

	/**
	 * Create the dialog.
	 */
	public FunctionView(JFrame owner, IFunction fn) {
		super(owner, fn.getContent(), true);
		this.fn = fn;
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int margin = 40;
		setBounds(margin,
				margin,
				size.width - 2*margin, 
				size.height - 2*margin 
		);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		
		split_main = new JSplitPane();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					//.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(split_main, GroupLayout.DEFAULT_SIZE, 1217, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(split_main, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
						.addComponent(toolBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		tab_coverage = new JTabbedPane(JTabbedPane.TOP);
		tab_coverage.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!Config.SHOW_CFG_DETAILS) return;
				toggleType(getCoverageTypeFromSelected());
			}
		});
		
		split_main.setRightComponent(tab_coverage);
		
		JToggleButton toggleButton = new JToggleButton("");
		toggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.SHOW_CFG_DETAILS = toggleButton.isSelected();
				Config.save();
				toggleCFGView(toggleButton.isSelected());
			}
		});
		toggleButton.setToolTipText("Show/hide CFG");
		toggleButton.setSelected(Config.SHOW_CFG_DETAILS);
		toggleButton.setIcon(new ImageIcon(FunctionView.class.getResource("/image/cfg.png")));
		toolBar.add(toggleButton);
		
		JButton button = new JButton("");
		button.setToolTipText("Export test data");
		button.setIcon(new ImageIcon(FunctionView.class.getResource("/image/export.png")));
		toolBar.add(button);
		getContentPane().setLayout(groupLayout);

		init2();
	}
}
