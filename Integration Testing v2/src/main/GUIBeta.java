package main;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import cdt.CMainProcess;
import core.Setting;
import core.graph.canvas.StatementCanvas;
import core.models.Function;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class GUIBeta {

	private JFrame frame;
	private StatementCanvas canvas;
	private JFileChooser fileChooser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIBeta window = new GUIBeta();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIBeta() {
		initialize();
	}
	
	/** Mở hộp thoại để chọn tập tin */
	private void openCFiles() {
		int status = fileChooser.showDialog(frame, "Open C File(s)");

		if (status == JFileChooser.APPROVE_OPTION) {
			try {
				CMainProcess main = new CMainProcess();
				main.setWorkingFiles(fileChooser.getSelectedFiles(), false);
				main.run();
				ArrayList<Function> fnList = main.getFunctions();
				
				if (fnList.size() > 0)
					canvas.setFunction(fnList.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 640, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openCFiles();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
						.addComponent(btnOpen))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnOpen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		canvas = new StatementCanvas();
		canvas.setBackground(Color.WHITE);
		canvas.setParent(scrollPane);
		scrollPane.setViewportView(canvas);
		frame.getContentPane().setLayout(groupLayout);
		
		fileChooser = new JFileChooser();
		FileNameExtensionFilter cFilter = new FileNameExtensionFilter(
				"C File (*.c; *.cpp)", new String[] { "C", "CPP" });
		fileChooser.setMultiSelectionEnabled(true);
		// fileChooser.setFileHidingEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(cFilter);
		Setting.loadSetting();
	}
}
