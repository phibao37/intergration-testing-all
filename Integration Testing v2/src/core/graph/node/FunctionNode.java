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
	
	private boolean[] referSelected;
	
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
				GUI.instance.openFunctionDetails(fn);
				if (e.getClickCount() == 2){
					openTestcaseManager();
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
	
	protected void openTestcaseManager(){
		GUI.instance.openFunctionTestcaseManager(getFunction());
	}
	
	@Override
	public void setRefers(Node[] refers) {
		super.setRefers(refers);
		referSelected = new boolean[refers.length];
	}
	
	/**
	 * Xóa bỏ mọi đánh dấu
	 */
	public void clearAllSelectedRefer(){
		for (int i = 0; i < referSelected.length; i++)
			referSelected[i] = false;
	}
	
	public void setSelectedRefer(int index){
		referSelected[index] = true;
	}
	
	public boolean isSelectedRefer(int index){
		return referSelected[index];
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

		private PopupMenu() {
			JMenuItem item;
			
			item = new JMenuItem("Xem nguồn");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openViewSource();
				}
			});
			this.add(item);

			item = new JMenuItem("Xem đồ thị CFG");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openViewCFG();
				}
			});
			this.add(item);

			item = new JMenuItem("Xem đồ thị CFG3");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openViewCFG3();
				}
			});
			this.add(item);

			item = new JMenuItem("Kiểm thử đơn vị");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.beginTest();
				}
			});
			this.add(item);
			
			item = new JMenuItem("Quán lý testcase");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					node.openTestcaseManager();
				}
			});
			this.add(item);
		}

		private void openPopupNode(FunctionNode n, MouseEvent e) {
			node = n;
			this.show(node, e.getX(), e.getY());
		}
	}
	
}
