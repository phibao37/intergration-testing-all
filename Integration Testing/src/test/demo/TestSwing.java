package test.demo;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import graph.swing.tablelayout.TableLayout;

public class TestSwing {

	private JFrame frame;
	private JPanel root;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestSwing window = new TestSwing();
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
	public TestSwing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JPanel();
		frame.getContentPane().add(root, BorderLayout.CENTER);
		
		TableLayout layout = new TableLayout(root, new double[][]{
			{0.25, 0.25, 0.5},
			{}
		});
		root.setLayout(layout);
		
		for (int i = 0; i < 6; i++){
			JButton b = new JButton("Delete me");
			TableLayout.TableRow row = layout.insertRow(0, 40, false, null,
					new JLabel(i * 2 + ""), new JLabel(i * 2 + 1 + ""), b);
			b.addActionListener(e -> layout.deleteRow(row));
		}
	}

}
