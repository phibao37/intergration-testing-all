package main;

import core.models.Function;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Hộp thoại giúp người dùng chọn một hàm làm hàm gốc
 * @author ducvu
 */
public class SelectFunction extends JDialog {
	private static final long serialVersionUID = -6580651976816704089L;
	private static final int marginX = 10;
	private static final int marginY = 7;
	private static final int width = 426;
	private static final int height = 23;
	private static final int paddingY = 26;
	private static class LightRadioButton extends JRadioButton{
		private static final long serialVersionUID = -4009413089006338915L;
		private Function func;
		private SelectFunction p;
		
		public LightRadioButton(Function f, SelectFunction parent){
			super(f.getNameAndFile());
			p = parent;
			this.setToolTipText(
				String.format("<html><body>%s<br/>File: %s</body></html>",
					f.getHTMLContent(), f.getSourceFile()));
			func = f;
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					LightRadioButton.this.setBackground(SystemColor.controlHighlight);
				}
				@Override
				public void mouseExited(MouseEvent e) {
					LightRadioButton.this.setBackground(null);
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2)
						p.applyFunction();
				}
			});
			/*this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int code = e.getKeyCode();
					
					if (code == KeyEvent.VK_DOWN){
						LightRadioButton radio = (LightRadioButton) e.getComponent();
						
						LightRadioButton next = (LightRadioButton)
							radio.focus
					}
				}
			});*/
		}
	}

	private Function func;
	private ButtonGroup group;
	private int scrollY;

	/**
	 * Tạo một hộp thoại chọn hàm mới
	 * @param parent khung nền của hộp thoại
	 * @param funcs danh sách các hàm dùng để chọn
	 * @param preRoot hàm số được lựa chọn sẵn trước
	 */
	public SelectFunction(JFrame parent, ArrayList<Function> funcs, Function preRoot) {
		super(parent, null, Dialog.ModalityType.DOCUMENT_MODAL);
		setIconImage(Toolkit.getDefaultToolkit().getImage(SelectFunction.class.getResource("/image/root.png")));
		setTitle("Chọn hàm số");
		setResizable(false);
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 444, 238);
		contentPanel.add(scrollPane);
		
		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(null);
		
		group = new ButtonGroup();
		int currentY = marginY;
		LightRadioButton selected = null;
		
		for (Function f: funcs){
			LightRadioButton rd = new LightRadioButton(f, this);
			
			rd.setBounds(marginX, currentY, width, height);
			group.add(rd);
			if (selected == null &&(
					(preRoot == null && f.getName().equals("main"))
					|| f == preRoot
				)){
				group.setSelected(rd.getModel(), true);
				selected = rd;
			}
			panel.add(rd);
			currentY = currentY + paddingY;
		}
		if (selected == null){
			group.setSelected(group.getElements().nextElement().getModel(), true);
		} else
			scrollY = selected.getY() + height/2 - scrollPane.getHeight()/2;
		
		Dimension size = panel.getPreferredSize();
		size.setSize(size.getWidth(), currentY);
		panel.setPreferredSize(size);
		panel.scrollRectToVisible(new Rectangle(0, scrollY, 1, 1));
		//scrollPane.getVerticalScrollBar().setValue(scrollY);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(e -> applyFunction());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Hủy bỏ");
				cancelButton.addActionListener(e -> {
                    func = null;
                    setVisible(false);
                    dispose();
                });
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Mở ra hộp thoại và trả về hàm số khi nó đã đóng lại
	 */
	public Function showDialog(){
		setVisible(true);
		return func;
	}
	
	/** Thiết đặt hàm được chọn*/
	private void applyFunction(){
		Enumeration<AbstractButton> iter = group.getElements();
		LightRadioButton rd;
		
		while (iter.hasMoreElements()){
			rd = (LightRadioButton) iter.nextElement();
			if (group.isSelected(rd.getModel())){
				func = rd.func;
				break;
			}
		}
		setVisible(false);
		dispose();
	}
}