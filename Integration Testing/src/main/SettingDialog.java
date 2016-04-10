package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.awt.Color;

public class SettingDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JTabbedPane tabPanel = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingDialog dialog = new SettingDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadSettings() {
		
	}
	
	private void applySettings(){
		
	}

	/**
	 * Create the dialog.
	 */
	public SettingDialog(Frame owner) {
		super(owner, "Settings", true);
		
		setBounds(100, 100, 600, 450);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			tabPanel.addTab("General", null, scrollPane, null);
			{
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				scrollPane.setViewportView(panel);
			}
		}
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						applySettings();
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(
				(size.width - getWidth())/2, 
				(size.height - getHeight())/2
		);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b){
			try{
				loadSettings();
			} catch (Exception e){
				javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
		super.setVisible(b);
	}

	

}
