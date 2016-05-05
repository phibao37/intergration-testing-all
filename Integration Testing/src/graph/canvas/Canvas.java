package graph.canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

import api.models.IElement;
import graph.node.Node;
import graph.node.NodeAdapter;

public abstract class Canvas<E extends IElement> extends JPanel 
		implements MouseListener {
	private static final long serialVersionUID = 1L;

	protected static final int PADDING_X = 25,
			PADDING_Y = 40;
	protected static final BasicStroke NORMAL_STROKE = new BasicStroke(1.5f);
	
	protected NodeAdapter<E> adapter;
	
	protected Canvas(){
		setLayout(null);
		setFocusable(true);
		setBackground(Color.WHITE);
		addMouseListener(this);
	}
	
	public void setAdapter(NodeAdapter<E> adapter){
		resetAll(false);
		this.adapter = adapter;
		parseAdapter(adapter);
		
		if (adapter.isEmpty()) return;
		int minX = Integer.MAX_VALUE;
		
		for (Node<E> node: adapter){
			add(node);
			if (node.getX() < minX)
				minX = node.getX();
		}
		if (minX < PADDING_X){
			int deltaX = PADDING_X - minX;
			for (Node<E> node: adapter)
				node.setLocation(node.getX() + deltaX, node.getY());
		}
		repaint();
	}
	
	protected abstract void parseAdapter(NodeAdapter<E> adapter);
	
	public void resetAll(boolean repaint){
		removeAll();
		adapter = null;
		if (repaint)
			repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int maxX = 0, maxY = 0, x, y;
		
		if (adapter != null){
			paintCanvas(g, adapter);
			for (Node<E> node: adapter){
				x = node.getX() + node.getWidth();
				y = node.getY() + node.getHeight();
				if (x > maxX)
					maxX = x;
				if (y > maxY)
					maxY = y;
			}
		}
		
		setPreferredSize(new Dimension(maxX + PADDING_X, maxY + PADDING_Y));
		
		revalidate();
	}
	
	protected abstract void paintCanvas(Graphics g, NodeAdapter<E> adapter);

	public boolean hasAdapter(){
		return adapter != null;
	}
	
	public NodeAdapter<E> getAdapter(){
		return adapter;
	}
	
	/**
	 * Vẽ một đường thẳng chứa mũi tên giữa 2 điểm
	 * 
	 * @param g
	 *            đối tượng đồ họa dùng để vẽ
	 * @param x1
	 *            tọa độ x của điểm đầu
	 * @param y1
	 *            tọa độ y của điểm đầu
	 * @param x2
	 *            tọa độ x của điểm cuối
	 * @param y2
	 *            tọa độ y của điểm cuối
	 * @param d
	 *            độ rộng của mũi tên
	 * @param h
	 *            chiều dài của mũi tên
	 */
	protected void drawArrowLine(Graphics g, int x1, int y1, 
			int x2, int y2, int d, int h) {
		drawArrow(g, x1, y1, x2, y2, d, h);
		g.drawLine(x1, y1, x2, y2);
	}

	protected void drawArrow(Graphics g, int x1, int y1, 
			int x2, int y2, int d, int h) {
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
	
	protected void openMenu(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		requestFocusInWindow();
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
}
