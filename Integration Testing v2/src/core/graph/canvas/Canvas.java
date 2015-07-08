package core.graph.canvas;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Setting;
import core.graph.LightLabel;
import core.graph.node.Node;

/**
 * Lớp đồ họa giúp hiển thị các nút đồ thị biểu diễn quan hệ giữa các đối tượng C
 * @author ducvu
 *
 */
public class Canvas extends JPanel {
	private static final long serialVersionUID = -1276708527830335706L;
	
	/** Khoảng cách giữa lề bên trái và nút trái nhất */
	protected static final int paddingX = 25;
	
	/** Khoảng cách giữa lề bên trên và nút trên cùng */
	protected static final int paddingY = 40;
    
    /** Khoảng cách chiều ngang giữa 2 nút kề nhau */
	protected static int marginX = 120;
	
    /** Khoảng cách chiều dọc giữa 2 hàng kề nhau */
	protected static int marginY = 100;
	
	/** Đặt giá trị khoảng cách chiều ngang giữa 2 nút*/
	public static void setMarginX(Integer x){
		marginX = x;
	}
	/** Đặt giá trị khoảng cách chiều dọc giữa 2 hàng*/
	public static void setMarginY(Integer y){
		marginY = y;
	}
	
	protected final static BasicStroke NORMAL_STROKE = new BasicStroke(1.5f);
	protected final static BasicStroke DASHED_STROKE = 
			new BasicStroke(1.5f, BasicStroke.CAP_BUTT, 
			BasicStroke.JOIN_MITER, 10.0f, new float[]{3f}, 0.0f);
	
	private JPanel navigator;
	private JScrollPane parent;
	
	protected ArrayList<Node> defaultNodeList = new ArrayList<Node>();
	
	/**
	 * Tạo một canvas hiển thị mới
	 */
	public Canvas(){
		super();
		this.setLayout(null);
		this.setFocusable(true);
		
		navigator = new JPanel();
		navigator.setBackground(Color.WHITE);
		//navigator.setBackground(new Color(255,0 ,0, 128));
		navigator.setBounds(353, 249, 87, 40);
		add(navigator);
		navigator.setLayout(null);
		
		JLabel lbl_clear = new LightLabel();
		lbl_clear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				resetAll(true);
			}
		});
		lbl_clear.setIcon(new ImageIcon(Canvas.class.getResource("/image/clear.png")));
		lbl_clear.setBounds(5, 5, 30, 30);
		navigator.add(lbl_clear);
		
		JLabel lbl_fullscreen = new LightLabel();
		lbl_fullscreen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (fullscreen)
					exitFullScreen();
				else
					goFullScreen();
			}
		});
		lbl_fullscreen.setIcon(new ImageIcon(
			Canvas.class.getResource("/image/fullscreen.png")));
		lbl_fullscreen.setBounds(40, 5, 40, 30);
		navigator.add(lbl_fullscreen);
		
		this.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (e.isPopupTrigger())
                	openMenu(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				if (e.isPopupTrigger())
					openMenu(e);
			}
        });
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				int delta = 5;
				
				if (code >=37 && code <= 40){
					switch (code){
					case 37: //left arrow
						Setting.setProperty(Setting.CvMarginX, marginX - delta);
						break;
					case 38: //up arrow
						Setting.setProperty(Setting.CvMarginY, marginY + delta);
						break;
					case 39: //right arrow
						Setting.setProperty(Setting.CvMarginX, marginX + delta);
						break;
					case 40: //bottom arrow
						Setting.setProperty(Setting.CvMarginY, marginY - delta);
						break;
					}
					Canvas.this.refresh();
				}
				else if (code == 107 || code == 109){
					Setting.setProperty(Setting.NodeFontSize, 
						Node.FONT_SIZE + (code == 107 ? 1 : -1));
					if (code == 107 || code == 109){
						for (Node n: defaultNodeList)
							n.reApplyFont();
					}
					Canvas.this.repaint();
				}
				//System.out.println(code);
			}
		});
	}
	
	
	/** Các công việc sau cùng khi tạo xong một canvas*/
	protected void postSetAdapter(){
		if (defaultNodeList.isEmpty())
			return;
		int minX = defaultNodeList.get(0).getX();
		for (Node n: defaultNodeList){
			n.setCanvas(this);
			this.add(n);
			if (n.getX() < minX)
				minX = n.getX();
		}
		if (minX < paddingX){
			int deltaX = paddingX - minX;
			for (Node n: defaultNodeList)
				n.setLocation(n.getX() + deltaX, n.getY());
		}
		this.repaint();
	}
	
	/** Làm mới lại canvas khi các cài đặt vừa được thay đổi*/
	protected void refresh(){}
	
	/** Xóa bỏ nội dung và thiết đặt lại canvas*/
	protected void resetAll(boolean repaint){
		//Reset canvas
		this.removeAll();
		defaultNodeList.clear();

		//Re-config
		this.add(navigator);
		if (repaint){
			this.repaint();
		}
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		postPaintComponent(g);
	}
	
	protected void postPaintComponent(Graphics g) {
		if (!defaultNodeList.isEmpty()) {
			int maxX = 0, maxY = 0, x, y;
			for (Node node : defaultNodeList) {
				x = node.getX() + node.getWidth();
				y = node.getY() + node.getHeight();
				if (x > maxX)
					maxX = x;
				if (y > maxY)
					maxY = y;
			}

			this.setPreferredSize(new Dimension(maxX + paddingX, maxY
					+ paddingY));
		} else {
			this.setPreferredSize(new Dimension());
		}

		int margin = 5;
		JScrollBar hScrollBar = parent.getHorizontalScrollBar();
		JScrollBar vScrollBar = parent.getVerticalScrollBar();
		int nx = parent.getWidth() - navigator.getWidth()
				+ hScrollBar.getValue()
				- (vScrollBar.isVisible() ? vScrollBar.getWidth() : 0) - margin;
		int ny = parent.getHeight() - navigator.getHeight()
				+ vScrollBar.getValue()
				- (hScrollBar.isVisible() ? hScrollBar.getHeight() : 0)
				- margin;
		navigator.setLocation(nx, ny);
		this.revalidate();
	}
	/**
	  * Vẽ một đường thẳng chứa mũi tên giữa 2 điểm
	  * @param g đối tượng đồ họa dùng để vẽ
	  * @param x1 tọa độ x của điểm đầu
	  * @param y1 tọa độ y của điểm đầu
	  * @param x2 tọa độ x của điểm cuối
	  * @param y2 tọa độ y của điểm cuối
	  * @param d  độ rộng của mũi tên
	  * @param h  chiều dài của mũi tên
	  */
	 protected void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h){
		drawArrow(g, x1, y1, x2, y2, d, h);
        g.drawLine(x1, y1, x2, y2);
	 }
	 /***
	  * Vẽ một đường thẳng cùng các hướng mũi tên chỉ định
	  * @param toSource có mũi tên hướng về điểm đầu (x1, y1)
	  * @param toTarget có mũi tên hướng về điểm cuối (x2, y2)
	  */
	 protected void drawArrowsLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h, 
			 boolean toSource, boolean toTarget){
		 if (toSource)
			 drawArrow(g, x2, y2, x1, y1, d, h);
		 if (toTarget)
			 drawArrow(g, x1, y1, x2, y2, d, h);
		 g.drawLine(x1, y1, x2, y2);
	 }

	private void drawArrow(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
		int dx = x2 - x1, dy = y2 - y1;
		double D = Math.sqrt(dx * dx + dy * dy);
		double xm = D - d, xn = xm, ym = h, yn = -h, x;
		double sin = dy / D, cos = dx / D;

		x = xm * cos - ym * sin + x1;
		ym = xm * sin + ym * cos + y1;
		xm = x;

		x = xn * cos - yn * sin + x1;
		yn = xn * sin + yn * cos + y1;
		xn = x;

		int[] xpoints = { x2, (int) xm, (int) xn };
		int[] ypoints = { y2, (int) ym, (int) yn };

		g.fillPolygon(xpoints, ypoints, 3);
	}
	 
	 /**
	  * Thiết đặt đối tượng bao ngoài của canvas
	  */
	 public void setParent(JScrollPane js){
		 parent = js;
	 }
	 

	protected boolean fullscreen;
	private JScrollPane backupParent;
	
	/** Mở chế độ toàn màn hình*/
	private void goFullScreen() {
		if (fullscreen)
			return;
		fullscreen = true;
		JFrame frame = new JFrame();

		frame.setUndecorated(true);
		frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(frame);
		frame.setContentPane(this.getClonePane());
		frame.revalidate();
		frame.repaint();
		frame.setVisible(true);
	}
	
	/** Thoát chế độ toàn màn hình*/
	private void exitFullScreen() {
		if (!fullscreen)
			return;
		fullscreen = false;
		SwingUtilities.windowForComponent(this).dispose();

		this.parent = this.backupParent;
		this.parent.setViewportView(this);
		this.revalidate();
		this.repaint();
	}
	
	/** Trả về đối tượng canvas được lưu trữ trong nền
	 * @throws Exception  */
	private JScrollPane getClonePane() {
		JScrollPane scrollPane = new JScrollPane();
		this.backupParent = this.parent;
		this.parent = scrollPane;

		scrollPane.setViewportView(this);
		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JViewport v = (JViewport) e.getSource();
				v.repaint();
			}
		});

		return scrollPane;
	}
	
	/** Phương thức giúp mở bảng tùy chỉnh khi có sự kiện truyền vào thích hợp*/
	protected void openMenu(MouseEvent e) {}
}