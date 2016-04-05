package graph.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Constructor;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Đối tượng đồ họa điều khiển các thành phần con theo các tab đóng mở được
 * @author ducvu
 *
 */
public class LightTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private static class Panel extends JPanel{
		private static final long serialVersionUID = 1L;
		private static final Color close = new Color(255, 0, 0, 220);
		private static final Color closeLite = new Color(255, 0, 0, 125);
		
		private LightTabbedPane tab;
		private JLabel lTitle, closeButton, gap;
		private boolean changed = false;
		private boolean closeable = false;
		
		public Panel(LightTabbedPane tabPane, String title){
			setOpaque(false);
			setLayout(new BorderLayout());
			tab = tabPane;
			
			lTitle = new JLabel(title);
			Dimension size = lTitle.getPreferredSize();
			size.setSize(size.getWidth(), 20);
			lTitle.setPreferredSize(size);
			add(lTitle, BorderLayout.WEST);
			
			gap = new JLabel();
			gap.setPreferredSize(new Dimension(5, 20));
			add(gap, BorderLayout.CENTER);
			
			closeButton = new JLabel("x");
			add(closeButton, BorderLayout.EAST);
			closeButton.setForeground(closeLite);
			closeButton.setToolTipText("Close");
			closeButton.setBackground(new Color(0,0,0,0));
			closeButton.setFont(new Font("Tahoma", Font.BOLD, 15));
			closeButton.setOpaque(true);
			
			closeButton.setHorizontalAlignment(SwingConstants.CENTER);
			closeButton.setPreferredSize(new Dimension(9, 20));
			
			closeButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					closeThisTab();
				}
		    	@Override
		    	public void mouseEntered(MouseEvent e) {
		    		closeButton.setForeground(close);
		    		Panel.this.repaint();
		    	}
		    	@Override
		    	public void mouseExited(MouseEvent e) {
		    		closeButton.setForeground(closeLite);
		    		Panel.this.repaint();
		    	}
			});
			
			LightMouseAdapter listener = new LightMouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e) {
					int button = e.getButton();
					
					if (button == MouseEvent.BUTTON1){
						super.mousePressed(e);
					}
					else if (button == MouseEvent.BUTTON2){
						closeThisTab();
					}
				}
			};
			addMouseListener(listener);
			addMouseMotionListener(listener);
			
			setCloseable(closeable);
		}
		
		private void setCloseable(boolean closeable){
			this.closeable = closeable;
			closeButton.setVisible(false);
			gap.setVisible(false);
			if (closeable){
				closeButton.setVisible(true);
				gap.setVisible(true);
			}
		}
		private void setTitleText(String title){
			lTitle.setPreferredSize(null);
			lTitle.setText(title);
			Dimension size = lTitle.getPreferredSize();
			size.setSize(size.getWidth(), 20);
			lTitle.setPreferredSize(size);
		}
		private void closeThisTab(){
			if (closeable)
				tab.removeTabAt(tab.indexOfTabComponent(Panel.this));
		}
	}
	
	/**
	 * Tạo một panel điều khiển với chế độ xuống dòng khi tràn tab
	 * @param tabPlacement vị trí đặt vùng điều khiển tab
	 */
	public LightTabbedPane(int tabPlacement){
		this(tabPlacement, JTabbedPane.WRAP_TAB_LAYOUT);
	}
	
	/**
	 * Tạo một panel điều khiển theo tab
	 * @param tabPlacement vị trí đặt vùng điều khiển tab
	 * @param tabLayoutPolicy chế độ khi tràn tab
	 */
	public LightTabbedPane(int tabPlacement, int tabLayoutPolicy){
		super(tabPlacement, tabLayoutPolicy);
		
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = getSelectedIndex();
				Panel tab = index >= 0 ? getTabComponentAt(index) : null;
				
				if (tab != null && tab.changed){
					tab.changed = false;
					String title = getTitleAt(index);
					title = title.substring(0, title.length() - 1);
					setTitleAt(index, title);
				}
			}
		});
	}
	
	@Override
	public Panel getTabComponentAt(int index){
		return (Panel) super.getTabComponentAt(index);
	}
	@Override
	public void insertTab(String title, Icon icon, Component component, 
			String tip, int index){
		super.insertTab(title, icon, component, tip, index);
		Panel panel = new Panel(this, title);
		setTabComponentAt(index, panel);
		
	}
	/**
	 * Thêm một tab nếu nó chưa tồn tại, sau đó chuyển đến tab đó
	 * @param constructType dùng để tạo đối tượng nếu tab chưa tồn tại
	 * @param construct đối tượng dùng để so sánh sự tồn tại của tab
	 * @return đối tượng đã được lựa chọn
	 */
	public Component openTab(String title, Icon icon, String tip, 
			Constructor<?> constructType, Object... construct){
		EqualsConstruct ec = null, cur;
		int index = getTabCount();
		Component c = null;

		for (int i = 0; i < index; i++) {
			c = getComponentAt(i);
			if (c instanceof EqualsConstruct) {
				cur = (EqualsConstruct) c;
				if (cur.equalsConstruct(construct)) {
					ec = cur;
					index = i;
					break;
				}
			}
		}
		if (ec == null) {
			try {
				c = (Component) constructType.newInstance(construct);
				addTab(title, icon, c, tip);
				setTabCloseableAt(c, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setSelectedIndex(index);
		return getSelectedComponent();
	}
	@Override
	public void setTitleAt(int index, String title){
		super.setTitleAt(index, title);
		getTabComponentAt(index).setTitleText(title);
	}
	
	/**
	 * Xác nhận rằng nội dung của một tab đã bị thay đổi
	 * @param index vị trí tab bị thay đổi nội dung
	 */
	public void setTabChangedAt(int index){
		if (index != this.getSelectedIndex()){
			Panel tab = this.getTabComponentAt(index);
			if (!tab.changed){
				tab.changed = true;
				this.setTitleAt(index, this.getTitleAt(index) + "*");
			}
		}
	}
	
	/**
	 * Thiết đặt chế độ đóng tab cho từng tab
	 * @param index vị trí tab
	 * @param closeable <i>Mặc định</i>: <b>false</b> khi đối tượng được tạo<br/> 
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 * &nbsp;&nbsp;&nbsp;đổi thành <b>true</b> nếu muốn tab trở thành không đóng được
	 */
	public void setTabCloseableAt(int index, boolean closeable){
		Panel panel = (Panel) this.getTabComponentAt(index);
		panel.setCloseable(closeable);
	}
	
	/**
	 * @see #setTabCloseableAt(Component, boolean)
	 */
	public void setTabCloseableAt(Component c, boolean closeable){
		setTabCloseableAt(indexOfComponent(c), closeable);
	}
	
	/**
	 * Các đối tượng có thể so sánh sự giống nhau 
	 * qua các thành phần được truyền vào trong hàm khởi tạo
	 */
	public static interface EqualsConstruct{
		
		/** Trả về đúng nếu <b>constructItem</b> bằng các thành phần trong hàm khởi tạo*/
		public boolean equalsConstruct(Object... constructItem);
	}
	
	static class LightMouseAdapter implements MouseListener,
			MouseWheelListener, MouseMotionListener{

	    public void mouseClicked(MouseEvent e) {
	        redispatchToParent(e);
	    }

	    public void mousePressed(MouseEvent e) {
	        redispatchToParent(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        redispatchToParent(e);
	    }

	    public void mouseEntered(MouseEvent e) {
	        redispatchToParent(e);
	    }

	    public void mouseExited(MouseEvent e) {
	        redispatchToParent(e);
	    }

	    public void mouseWheelMoved(MouseWheelEvent e){
	        redispatchToParent(e);
	    }

	    public void mouseDragged(MouseEvent e){
	        redispatchToParent(e);
	    }

	    public void mouseMoved(MouseEvent e) {
	        redispatchToParent(e);
	    }

	    private void redispatchToParent(MouseEvent e){
	        Component source = (Component) e.getSource(),
	        		parent = source.getParent().getParent();
	        
	        MouseEvent parentEvent = SwingUtilities.convertMouseEvent(
	        		source, e, parent);
	        parent.dispatchEvent(parentEvent);
	        
	    }
	}
}