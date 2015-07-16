package core.graph.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import core.GUI;
import core.models.Function;

/**
 * Nút đồ họa tương ứng với một hàm số trong chương trình
 */
public class FunctionNode extends Node {
	private static final long serialVersionUID = -4883719306522826155L;
	
	/**
	 * Tạo một nút đồ họa từ hàm tương ứng
	 */
	public FunctionNode(Function fn){
		super(fn);
		
		this.setToolTipText(
			String.format("<html><body>%s<br/>File: %s</body></html>",
				fn.getHTMLContent(), 
				fn.getSourceFile()));
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2){
					beginTest();
				}
			}
		});
	}
	
	/**
	 * Xem nội dung mã nguồn chứa hàm số tương ứng
	 */
	protected void openViewSource(){
		GUI.instance.openFileView(getFunction().getSourceFile());
	}
	
	/**
	 * Mở ra đồ thị phủ cấp 1,2 tương ứng với thân hàm của nút này
	 */
	protected void openViewCFG(){
		GUI.instance.openFuntionView(getFunction(), false);
	}
	
	/**
	 * Mở ra đồ thị phủ cấp 3 tương ứng với thân hàm của nút này
	 */
	protected void openViewCFG3(){
		GUI.instance.openFuntionView(getFunction(), true);
	}
	
	protected void beginTest(){
		GUI.instance.beginTestFunction(getFunction());
	}
	
	/** Trả về hàm tương ứng với nút*/
	public Function getFunction(){
		return (Function)mElement;
	}
	
	protected void openMenu(MouseEvent e) {
		menu.openPopupNode(this, e);
	}

	private static PopupMenu menu = new PopupMenu();

	private static class PopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		private FunctionNode node;
		private JMenuItem viewSource, viewCFG, viewCFG3, seePath;

		private PopupMenu() {
			viewSource = new JMenuItem("Xem nguồn");
			viewSource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openViewSource();
				}
			});
			this.add(viewSource);

			viewCFG = new JMenuItem("Xem đồ thị CFG");
			viewCFG.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openViewCFG();
				}
			});
			this.add(viewCFG);

			viewCFG3 = new JMenuItem("Xem đồ thị CFG3");
			viewCFG3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openViewCFG3();
				}
			});
			this.add(viewCFG3);

			seePath = new JMenuItem("Kiểm thử đơn vị");
			seePath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.beginTest();
				}
			});
			this.add(seePath);
		}

		private void openPopupNode(FunctionNode n, MouseEvent e) {
			node = n;
			this.show(node, e.getX(), e.getY());
		}
	}
	
}
