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
import core.graph.canvas.StatementCanvas;
import core.models.Function;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import jdt.JMainProcess;

public class GUIBeta {

	private JFrame frame;
	private StatementCanvas canvas;
	private JFileChooser fileChooser_C;
	private JFileChooser fileChooser_J;

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
	
	/** Mở hộp thoại để chọn tập tin C */
	private void openCFiles() {
		int status = fileChooser_C.showDialog(frame, "Open C File(s)");

		if (status == JFileChooser.APPROVE_OPTION) {
			try {
				CMainProcess main = new CMainProcess();
				main.setWorkingFiles(fileChooser_C.getSelectedFiles(), false);
				main.loadFunctionFromFiles();
				ArrayList<Function> fnList = main.getFunctionCallGraph();
				
				if (fnList.size() > 0){
					Function fn = fnList.get(0);
					canvas.setFunction(fn);
					main.beginTestFunctionBeta(fn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Mở hộp thoại để chọn tập tin Java */
	private void openJFiles() {
		int status = fileChooser_J.showDialog(frame, "Open Java File(s)");

		if (status == JFileChooser.APPROVE_OPTION) {
			try {
				JMainProcess main = new JMainProcess();
				main.setWorkingFiles(fileChooser_J.getSelectedFiles(), false);
				main.loadFunctionFromFiles();
				ArrayList<Function> fnList = main.getFunctionCallGraph();
				
				if (fnList.size() > 0){
					Function fn = fnList.get(0);
					canvas.setFunction(fn);
					main.beginTestFunctionBeta(fn);
				}
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
		
		JButton btnOpen = new JButton("Open C");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openCFiles();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		
		JButton btnOpenJava = new JButton("Open Java");
		btnOpenJava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openJFiles();
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnOpen)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOpenJava)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOpen)
						.addComponent(btnOpenJava))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		canvas = new StatementCanvas();
		canvas.setBackground(Color.WHITE);
		scrollPane.setViewportView(canvas);
		frame.getContentPane().setLayout(groupLayout);
		
		fileChooser_C = new JFileChooser();
		FileNameExtensionFilter cFilter = new FileNameExtensionFilter(
				"C File (*.c; *.cpp)", new String[] { "C", "CPP" });
		fileChooser_C.setMultiSelectionEnabled(true);
		fileChooser_C.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser_C.setFileFilter(cFilter);
		
		fileChooser_J = new JFileChooser();
		FileNameExtensionFilter jFilter = new FileNameExtensionFilter(
				"Java File (*.java)", new String[] { "JAVA" });
		fileChooser_J.setMultiSelectionEnabled(true);
		fileChooser_J.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser_J.setFileFilter(jFilter);
	}
}
