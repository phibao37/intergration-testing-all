package graph.node;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JLabel;

import api.models.IElement;

public class Node<E extends IElement> extends JLabel {
	private static final long serialVersionUID = 1L;
	protected static final int MAX_STR_LEN = 30;
	
	private E element;
	private Node<E>[] refer;
	private int x, y;
	
	protected Node(){
		setHorizontalAlignment(CENTER);
		setFocusable(true);
		
		// Implement some mouse drag-drop listener
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int xx = e.getX();
				int yy = e.getY();
				int newX = Node.this.getX() + xx - x;
				int newY = Node.this.getY() + yy - y;

				newX = Math.max(newX, 0);
				newY = Math.max(newY, 0);
				setLocation(newX, newY);
				getParent().repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				x = (int) e.getPoint().getX();
				y = (int) e.getPoint().getY();
				requestFocusInWindow();
				if (e.isPopupTrigger())
					openMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					openMenu(e);
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();

				if (code >= 37 && code <= 40) {
					int x = getX();
					int y = getY();
					int delta = e.isControlDown() ? 10 : 1;

					// e.consume();
					switch (code) {
					case 37: // left arrow
						setLocation(Math.max(x - delta, 0), y);
						break;
					case 38: // up arrow
						setLocation(x, Math.max(y - delta, 0));
						break;
					case 39: // right arrow
						setLocation(x + delta, y);
						break;
					case 40: // bottom arrow
						setLocation(x, y + delta);
						break;
					}
					getParent().repaint();
				}
			}
		});
	}
	
	protected Node(E element){
		this();
		setElement(element);
		String content = element.getContent();
		
		if (content.length() > MAX_STR_LEN){
			setToolTipText(content);
			content = content.substring(0, MAX_STR_LEN - 3) + "...";
		}
		
		setText(content);
	}
	
	public boolean isLocationSet(){
		return getX() != 0 || getY() != 0;
	}
	
	protected void setElement(E element){
		this.element = element;
	}
	
	public E getElement(){
		return element;
	}

	public Node<E>[] getRefer() {
		return refer;
	}

	public void setRefer(Node<E>[] refer) {
		this.refer = refer;
	}
	
	@Override
	public String toString() {
		return element.toString();
	}
	
	protected void openMenu(MouseEvent e) {}
}
