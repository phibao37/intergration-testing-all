package core.graph.node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import core.S;
import core.graph.Graphable;
import core.graph.canvas.Canvas;

/**
 * Một nút biểu thị một phần tử được hiển thị ra dưới dạng đồ họa.<br/>
 * Thông thường, các nút này được liên kết với các nút khác để tạo thành một cây đồ họa
 * biểu thị cho một cấu trúc cây nào đó
 * @author ducvu
 *
 */
public class Node extends JLabel {
	private static final long serialVersionUID = -4883719306522826155L;

	private static Font FONT = new Font("Tahoma", Font.BOLD, S.NODE_FONT_SIZE);
	
	private static final int MAX_STR_LEN = 30;
	private static final int PADDING_W = 15;
	private static final int PADDING_H = 10;
	
	public static final CompoundBorder DOUBLE_BORDER =
			new CompoundBorder(
				new LineBorder(new Color(0, 0, 0), 1, true), 
				new CompoundBorder(
					new LineBorder(new Color(255, 255, 255), 2, true),
					new LineBorder(new Color(0, 0, 0), 1, true)
				)
			);
	
	protected Graphable mElement;
	private Node[] mRefers;
	private Canvas mCanvas;
	private int x, y;
	private int mFlags = 0;
	
	/**
	 * Tạo một nút mới từ phần tử tương ứng
	 * @param element
	 */
	protected Node(Graphable element){
		String content = element.getNodeContent();
		Dimension size;
		
		mElement = element;
		if (content.length() > MAX_STR_LEN){
			this.setToolTipText(content);
			content = content.substring(0, MAX_STR_LEN) + "...";
		}
		setFont(FONT);
		setText(content);
		
		size = getPreferredSize();
		size.setSize(size.getWidth() + PADDING_W, size.getHeight() + PADDING_H);
		setSize(size);
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setHorizontalAlignment(SwingConstants.CENTER);
		setOpaque(true);
		setFocusable(true);
		setBackground(SystemColor.inactiveCaptionBorder);
		
		//Implement some mouse drag-drop listener
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int xx = e.getX();
				int yy = e.getY();
				int newX = Node.this.getX() + xx - x;
				int newY = Node.this.getY() + yy - y;
				
				newX = Math.max(newX, 0);
				newY = Math.max(newY, 0);
				setLocation(newX, newY);
				mCanvas.repaint();
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				x = (int)e.getPoint().getX();
				y = (int)e.getPoint().getY();
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
				
				if (code >=37 && code <= 40){
					int x = getX();
					int y = getY();
					int delta = e.isControlDown() ? 10 : 1;
					
					//e.consume();
					switch (code){
					case 37: //left arrow
						setLocation(Math.max(x - delta, 0), y);
						break;
					case 38: //up arrow
						setLocation(x, Math.max(y - delta, 0));
						break;
					case 39: //right arrow
						setLocation(x + delta, y);
						break;
					case 40: //bottom arrow
						setLocation(x, y + delta);
						break;
					}
					mCanvas.repaint();
				}
			}
		});
	}
	
	/**
	 * Làm mới lại nút khi font bị thay đổi
	 */
	public void reApplyFont(){
		this.setFont(FONT);
		Dimension size = getPreferredSize();
		size.setSize(size.getWidth() + PADDING_W, size.getHeight() + PADDING_H);
		setSize(size);
	}
	
	/**
	 * Trả về phần tử chứa trong nút
	 */
	public Graphable getElement(){
		return mElement;
	}
	
	/** Thiết đặt kích thước cho văn bản trong nút*/
	public static void addFontSize(int size){
		size = S.NODE_FONT_SIZE + size;
		if (size < 5)
			size = 5;
		else if (size > 40)
			size = 40;
		S.NODE_FONT_SIZE = size;
		FONT = FONT.deriveFont((float)size);
	}
	
	/**
	 * Thiết đặt danh sách các nút mà nút này tham chiếu tới
	 * @param refers danh sách nút tham chiếu
	 */
	public void setRefers(Node[] refers){
		mRefers = refers;
	}
	
	/**
	 * Trả về danh sách các nút mà nút này tham chiếu tới
	 */
	public Node[] getRefers(){
		return mRefers;
	}
	
	/**
	 * Xóa tất cả các đính kèm
	 */
	public static final int FLAG_CLEAR_ALL = -1;
	
	/**
	 * Thêm một đính kèm cờ hiệu cho nút này
	 * @param flag cờ hiệu để chỉ định một tình trạng
	 */
	public void addFlag(int flag){
		mFlags = mFlags | flag;
	}
	
	/**
	 * Loại bỏ một đính kèm khỏi nút này, sử dụng {@link #FLAG_CLEAR_ALL} để loại bỏ
	 * tất cả các đính kèm
	 */
	public void removeFlag(int flag){
		mFlags = mFlags & (~flag);
	}
	
	/**
	 * Kiểm tra nút có giữ một cờ hiệu nào không
	 */
	public boolean hasFlag(int flag){
		return (mFlags & flag) != 0;
	}
	
	/** Thiết đặt canvas chứa nút*/
	public void setCanvas(Canvas canvas){
		mCanvas = canvas;
	}
	
	public String toString(){
		return mElement.getNodeContent();
	}

	/** Phương thức giúp mở bảng menu khi có sự kiện ấn chuột phải*/
	protected void openMenu(MouseEvent e) {}
}
