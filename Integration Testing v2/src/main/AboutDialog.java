package main;

import core.S;
import core.S.SCREEN;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AboutDialog dialog = new AboutDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog(JFrame parent) {
		super(parent, null, Dialog.ModalityType.DOCUMENT_MODAL);
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(AboutDialog.class.getResource("/image/info.png")));
		setTitle("Giới thiệu ứng dụng");
		
		setBounds(100, 100, 700, 400);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("Control Flow Testing for Unit & Integration");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
		JLabel lblNewLabel_1 = new JLabel("Kiểm thử dòng điều khiển đơn vị và tích hợp cho C, Java");
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.ITALIC, 20));
		
		JLabel lblPhinBn = new JLabel("Phiên bản");
		lblPhinBn.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel label = new JLabel(S.VERSION);
		label.setFont(new Font("Arial", Font.BOLD, 25));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblLpTrnh = new JLabel("Nhóm phát triển");
		lblLpTrnh.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(15);
		panel.setBorder(null);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(label, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(lblNewLabel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(lblPhinBn, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(lblLpTrnh, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblPhinBn)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblLpTrnh)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(71, Short.MAX_VALUE))
		);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(191, 205, 219), 1, true));
		panel_1.setBackground(SystemColor.controlHighlight);
		panel_1.setPreferredSize(new Dimension(200, 90));
		panel.add(panel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Sầm Đức Vũ");
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD, 21));
		
		JLabel lblUetvnu = new JLabel("UET-VNU");
		lblUetvnu.setFont(new Font("Times New Roman", Font.ITALIC, 14));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblUetvnu, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
						.addComponent(lblNewLabel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblUetvnu)
					.addContainerGap(28, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(200, 90));
		panel_2.setBorder(new LineBorder(new Color(191, 205, 219), 1, true));
		panel_2.setBackground(SystemColor.controlHighlight);
		panel.add(panel_2);
		
		JLabel label_1 = new JLabel("UET-VNU");
		label_1.setFont(new Font("Times New Roman", Font.ITALIC, 14));
		
		JLabel lblDngTunAnh = new JLabel("Dương Tuấn Anh");
		lblDngTunAnh.setFont(new Font("Times New Roman", Font.BOLD, 21));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 200, Short.MAX_VALUE)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(label_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
						.addComponent(lblDngTunAnh, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGap(0, 90, Short.MAX_VALUE)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDngTunAnh, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label_1)
					.addContainerGap(25, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
			fl_buttonPane.setVgap(10);
			fl_buttonPane.setHgap(10);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Đồng ý");
				okButton.addActionListener(e -> dispose());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
	    int x = (SCREEN.WIDTH - getWidth())/2;
	    int y = (SCREEN.HEIGHT - getHeight())/2;
	    setLocation(x, y);
	}
}
