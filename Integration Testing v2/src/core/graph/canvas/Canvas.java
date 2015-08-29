package core.graph.canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.GUI;
import core.S;
import core.Utils;
import core.graph.DragScrollPane;
import core.graph.node.Node;
import java.awt.event.HierarchyListener;
import java.awt.event.HierarchyEvent;
import java.awt.SystemColor;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.ComponentOrientation;

/**
 * Lớp đồ họa giúp hiển thị các nút đồ thị biểu diễn quan hệ giữa các đối tượng C
 * @author ducvu
 *
 */
public class Canvas extends JPanel implements MouseListener {
	private static final long serialVersionUID = -1276708527830335706L;
	
	/** Khoảng cách giữa lề bên trái và nút trái nhất */
	protected static final int paddingX = 25;
	
	/** Khoảng cách giữa lề bên trên và nút trên cùng */
	protected static final int paddingY = 40;
	
	protected final static BasicStroke NORMAL_STROKE = new BasicStroke(1.5f);
	protected final static BasicStroke DASHED_STROKE = 
			new BasicStroke(1.5f, BasicStroke.CAP_BUTT, 
			BasicStroke.JOIN_MITER, 10.0f, new float[]{3f}, 0.0f);
	
	protected ArrayList<Node> defaultNodeList = new ArrayList<Node>();
	protected boolean fullscreen;
	private JViewport parent;
	private JScrollPane parentWrap;
	protected JPanel toolbar;
	
	/**
	 * Tạo một canvas hiển thị mới
	 */
	public Canvas(){
		super();
		addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if (Utils.hasFlag(e.getChangeFlags(), HierarchyEvent.PARENT_CHANGED)){
					parent = (JViewport) getParent();
				}
			}
		});
		
		this.setLayout(null);
		this.setFocusable(true);
		this.setBackground(Color.WHITE);
		this.addMouseListener(this);
		
		toolbar = new Toolbar();
		toolbar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		toolbar.setVisible(S.CANVAS_SHOW_TOOLBAR);
		toolbar.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		toolbar.setBounds(356, 246, 84, 35);
		add(toolbar);
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT, 
				Toolbar.PADDING_X, Toolbar.PADDING_Y));
		
		JButton button_1 = new JButton("");
		button_1.setSelected(true);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fullscreen)
					exitFullScreen();
				else
					goFullScreen();
			}
		});
		
		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolbar.setVisible(false);
			}
		});
		button.setToolTipText("Đóng [Double-Click Canvas]");
		button.setIcon(new ImageIcon(Canvas.class.getResource("/image/close.png")));
		toolbar.add(button);
		button_1.setToolTipText("Toàn màn hình");
		button_1.setIcon(new ImageIcon(Canvas.class.getResource("/image/fullscreen.png")));
		toolbar.add(button_1);
		
		JButton button_2 = new JButton("");
		button_2.setToolTipText("Lưu hình ảnh");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportToImage();
			}
		});
		button_2.setIcon(new ImageIcon(Canvas.class.getResource("/image/export.png")));
		toolbar.add(button_2);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				int delta = 5;
				
				if (code >=37 && code <= 40){
					switch (code){
					case 37: //left arrow
						S.CANVAS_MARGIN_X -= delta;
						break;
					case 38: //up arrow
						S.CANVAS_MARGIN_Y += delta;
						break;
					case 39: //right arrow
						S.CANVAS_MARGIN_X += delta;
						break;
					case 40: //bottom arrow
						S.CANVAS_MARGIN_Y -= delta;
						break;
					}
					S.save();
					Canvas.this.refresh();
				}
				else if (code == 107 || code == 109){
					Node.addFontSize(code == 107 ? 1 : -1);
					
					if (code == 107 || code == 109){
						for (Node n: defaultNodeList)
							n.reApplyFont();
					}
					S.save();
					Canvas.this.repaint();
				}
			}
		});
	}
	
	
	/** Các công việc sau cùng khi tạo xong một canvas*/
	protected void postSetAdapter(){
		if (defaultNodeList.isEmpty())
			return;
		int minX = defaultNodeList.get(0).getX();
		for (Node n: defaultNodeList){
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
		this.add(toolbar);
		if (repaint){
			this.repaint();
		}
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

		Point p = parent.getViewPosition();
		int x = (int)p.getX() + parent.getWidth() - toolbar.getWidth() - 5;
		int y = (int)p.getY() + parent.getHeight() - toolbar.getHeight() - 5;
		toolbar.setLocation(x, y);
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

	protected void drawArrow(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
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
	
	/** Mở chế độ toàn màn hình*/
	void goFullScreen() {
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
	void exitFullScreen() {
		if (!fullscreen)
			return;
		fullscreen = false;
		SwingUtilities.windowForComponent(this).dispose();

		this.parentWrap.setViewportView(this);
		this.revalidate();
		this.repaint();
	}
	
	/** Trả về đối tượng canvas được lưu trữ trong nền
	 * @throws Exception  */
	private JScrollPane getClonePane() {
		JScrollPane scrollPane = new DragScrollPane();
		this.parentWrap = (JScrollPane) parent.getParent();
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


	@Override
	public void mouseClicked(MouseEvent e) {
		requestFocusInWindow();
		if (e.getClickCount() == 2){
			toolbar.setVisible(!toolbar.isVisible());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
        	openMenu(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger())
        	openMenu(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	private static JFileChooser export;
	private static FileNameExtensionFilter exportExt;
	
	void exportToImage(){
		if (getComponentCount() < 2){
			GUI.instance.setStatus(1, "Không có dữ liệu để lưu");
			return;
		}
		if (export == null){
			export = new JFileChooser();
			exportExt = new FileNameExtensionFilter(
					"Hình ảnh (JPG, PNG)", new String[]{"jpg", "png"});
			export.setFileFilter(exportExt);
		}
		
		try{
			Component c = parent.getParent();
			JTabbedPane t = (JTabbedPane) c.getParent();
			String title = t.getTitleAt(t.indexOfComponent(c));
			
			export.setSelectedFile(new File(
					export.getSelectedFile(), title + ".png"));
		} catch (Exception e){
			export.setSelectedFile(new File(
					export.getSelectedFile(), "Canvas.png"));
		}
		
		if (export.showDialog(this.getTopLevelAncestor(), 
				"Lưu hình ảnh") != JFileChooser.APPROVE_OPTION)
			return;
		
		File choose = export.getSelectedFile();
		if (!exportExt.accept(choose)){
			choose = new File(choose.getAbsolutePath() + ".jpg");
		}
		
		int w = getWidth();
		int h = getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		boolean showToolbar = toolbar.isVisible();
		
		toolbar.setVisible(false);
		print(g);
		toolbar.setVisible(showToolbar);
		
		try {
			choose.delete();
			ImageIO.write(bi, Utils.getExtension(choose), choose);
			GUI.instance.setStatus(2, "Lưu %s thành công", choose.getName());
		} catch (Exception e) {
			javax.swing.JOptionPane.showMessageDialog(this, e);
		}
	}
	
	static class Toolbar extends JPanel implements MouseListener{
		private static final long serialVersionUID = 1L;
		private static final Color BG = SystemColor.inactiveCaptionBorder;
		private static final LineBorder LINE = new LineBorder(
				SystemColor.activeCaption);
		private static final LineBorder NONE = new LineBorder(BG);
		private static final Color ACTIVE = new Color(153, 180, 209);
		public static final int PADDING_X = 5;
		public static final int PADDING_Y = 5;
		
		public Toolbar(){
			addMouseListener(new MouseAdapter() {});
		}
		
		protected void addImpl(Component comp, Object constraints, int index){
			super.addImpl(comp, constraints, index);
			comp.addMouseListener(this);
			((JComponent)comp).setBorder(NONE);
			if (comp instanceof JButton){
				JButton b = (JButton) comp;
				b.setContentAreaFilled(false);
				if (b.isSelected()){
					b.setBackground(ACTIVE);
					b.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							AbstractButton a = (AbstractButton) e.getSource();
							a.setContentAreaFilled(!a.isContentAreaFilled());
						}
					});
				}
			}
			
			int w = PADDING_X, h = 0;
			for (Component c: getComponents()){
				Dimension d = c.getPreferredSize();
				w += d.width + PADDING_X + 2;
				if (d.height > h)
					h = d.height;
			}
			setSize(w, h + 2 + 2*PADDING_Y);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			((JComponent)e.getComponent()).setBorder(LINE);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			((JComponent)e.getComponent()).setBorder(NONE);
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

	}
}