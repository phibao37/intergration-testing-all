package main;

import graph.swing.FileExplorer;
import graph.swing.FileExplorer.Config;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import java.awt.Toolkit;

public class WindowTest {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowTest window = new WindowTest();
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
	public WindowTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowTest.class.getResource("/image/computer.png")));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		FileExplorer tree = new FileExplorer();
		tree.setSelectedPath(new java.io.File("D:/Downloads/image"));
		scrollPane.setViewportView(tree);
		
		Config cf = new Config();
		cf.showRoot = false;
		tree.setConfig(cf);
	}

}
