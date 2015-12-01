package main;

import graph.swing.FileExplorer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import java.awt.Toolkit;
import java.io.File;

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
		tree.setSelectedPath(new java.io.File("D:/Downloads/"));
		scrollPane.setViewportView(tree);
		
		tree.setMenuHandle(new FileExplorer.MenuHandle() {
			
			private JMenuItem openFile, openDir, openAll;
			
			@Override
			public void accept(JPopupMenu menu) {
				menu.add(openFile = new JMenuItem("Mở tập tin"));
				menu.add(openDir = new JMenuItem("Mở thư mục"));
				menu.add(openAll = new JMenuItem("Mở tất cả"));
			}

			@Override
			public void accept(File... files) {
				boolean isMore = files.length > 1,
						isOne = files.length == 1,
						isFile = isOne && files[0].isFile(),
						isDir = isOne && files[0].isDirectory();
				
				openFile.setVisible(isFile);
				openDir.setVisible(isDir);
				openAll.setVisible(isMore);
			}
		});
	}

}
