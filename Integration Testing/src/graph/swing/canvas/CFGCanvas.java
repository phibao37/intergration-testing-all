package graph.swing.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import api.models.IFunction;
import api.models.IStatement;
import graph.swing.node.CFGNode;
import graph.swing.node.Node;
import graph.swing.node.NodeAdapter;

public class CFGCanvas extends Canvas<IStatement> {
	private static final long serialVersionUID = 1L;
	protected static final int MARGIN_X = 150, MARGIN_Y = 90;
	public static final Color DEFAULT = Color.BLACK;
	public static final Color TRUE = Color.BLUE;
	public static final Color FALSE = new Color(0, 153, 51);
	
	private IFunction function;
	
	public CFGCanvas(IFunction fn){
		function = fn;
	}
	
	public IFunction getFunction(){
		return function;
	}
	
	@Override
	protected void parseAdapter(NodeAdapter<IStatement> adapter) {
		adapter.get(0).setLocation(getWidth()/2, PADDING_Y);
		
		for (Node<IStatement> n: adapter){
			CFGNode node = (CFGNode) n;
			Node<IStatement>[] refer = n.getRefer();
			boolean isCondition = node.isCondition(),
					is1StmCondition = node.is1StmCondition();
			int x = node.getX() + node.getWidth()/2;
			int y = node.getY() + node.getHeight()/2;
			int length = isCondition ? refer.length : 1;
			
			for (int i = 0; i < length; i++){
				CFGNode rNode = (CFGNode) refer[i];
				
				if (rNode == null)
					continue;
				if (!(rNode.isLocationSet() && rNode.isCondition())) {
					int cx = x - rNode.getWidth() / 2;
					int cy = y + MARGIN_Y - rNode.getHeight() / 2;

					if (isCondition){
						if (is1StmCondition){
							if (i == 0){
								cx -= MARGIN_X;
								cy -= MARGIN_Y/2;
							}
						}
						else
							cx += i == 0 ? -MARGIN_X : MARGIN_X;
					}
					rNode.setLocation(cx, cy);
				}
			}
		}
	}
	
	/** Math.asin(10.0/38) */
	static double DELTA = 0.26629401711818285;
	
	static int[] getPoint(int x1, int y1, int x2, int y2, boolean left){
		double anpha = Math.atan2(y2-y1, x2-x1),
			   anph6 = anpha * 6 / Math.PI;
		
		if ((left && 5 > anph6) || (!left && 0 < anph6 && anph6 <= 1))
			anpha += DELTA;
		else
			anpha -= DELTA;
		
		return new int[]{
			x1 + (int) (38 * Math.cos(anpha)),
			y1 + (int) (38 * Math.sin(anpha))
		};
	}

	@Override
	protected void paintCanvas(Graphics g, NodeAdapter<IStatement> adapter) {
		int x1, y1, x2, y2, xs, ys;
		Node<IStatement>[] refer;
		int d = 12, h = 5, gap = 25;
		Graphics2D g2 = (Graphics2D) g;
		boolean rightSide;
		Stroke oldStroke = g2.getStroke();
		
		g2.setStroke(NORMAL_STROKE);
		// g2.setFont(FONT_LABEL);
		for (Node<IStatement> n1 : adapter) {
			refer = n1.getRefer();
			xs = n1.getX() + n1.getWidth() / 2;
			ys = n1.getY() + n1.getHeight();
			boolean isCondition = ((CFGNode) n1).isCondition();
			int length = isCondition ? refer.length : 1;

			for (int i = 0; i < length; i++) {
				Node<IStatement> n2 = refer[i];
				boolean isTrue = i == 0;
				if (n2 == null)
					continue;

				if (n2 == n1) {
					//n1.setBorder(Node.DOUBLE_BORDER);
				} else {
					Color color;
					int[] marks = null;

//					if (n1.hasFlag(
//							isTrue ? StatementNode.FLAG_SELECT_TRUE_EXTRA : StatementNode.FLAG_SELECT_FALSE_EXTRA))
//						color = SELECTED_EXTRA;
//					else if (n1.hasFlag(isTrue ? StatementNode.FLAG_SELECT_TRUE : StatementNode.FLAG_SELECT_FALSE))
//						color = SELECTED;
//					else 
					if (isCondition)
						color = isTrue ? TRUE : FALSE;
					else
						color = DEFAULT;
					g2.setColor(color);

					x1 = xs;
					y1 = ys;
					x2 = n2.getX() + n2.getWidth() / 2;
					y2 = n2.getY();
					rightSide = x2 > x1;
					if (y2 > y1) {
						double angle = Math.atan((y2 - y1) * 1.0 / Math.abs(x2 - x1));
						if (angle < Math.PI / 8) {
							y2 = y2 + n2.getHeight() / 2;
							x2 = n2.getX() + (rightSide ? 0 : n2.getWidth());
						}
					} else {
						int nearSide = n2.getX() + (rightSide ? 0 : n2.getWidth());
						int distance = Math.abs(nearSide - (x1 + n1.getWidth() / 2 * (rightSide ? 1 : -1)));
						boolean outOfPadding = (n1.getX() + n1.getWidth() < n2.getX()
								|| n2.getX() + n2.getWidth() < n1.getX()) && distance > gap + 1;

						if (outOfPadding /* && n2.getY() >= n1.getY() */) {
							x1 = n1.getX() + (rightSide ? n1.getWidth() : 0);
							y1 = n1.getY() + n1.getHeight() / 2;
							x2 = n2.getX() + (rightSide ? 0 : n2.getWidth());
							y2 = n2.getY() + n2.getHeight() / 2;
						} else {
							g2.drawLine(x1, y1, x1, y1 + gap);
							if (isCondition)
								marks = getPoint(x1, y1, x1, y1 + gap, isTrue);

							int tmp;
							if (outOfPadding) {
								tmp = x2 + (n2.getWidth() / 2 + gap) * (rightSide ? -1 : 1);
							} else {
								tmp = n2.getX() + (rightSide ? n2.getWidth() + gap : -gap);
							}
							g2.drawLine(x1, y1 + gap, tmp, y1 + gap);
							x1 = tmp;
							y2 = y2 + n2.getHeight() / 2;
							g2.drawLine(x1, y1 + gap, x1, y2);
							y1 = y2;
							x2 = n2.getX() + (rightSide ^ outOfPadding ? n2.getWidth() : 0);
						}
					}
					drawArrowLine(g2, x1, y1, x2, y2, d, h);
					if (isCondition) {
						if (marks == null)
							marks = getPoint(x1, y1, x2, y2, isTrue);
						g.drawString(isTrue ? "T" : "F", marks[0] - 3, marks[1] + 5);
					}
				}
			}

//			if (mShowLabel.isSelected() && !n1.getLabel().isEmpty()) {
//				int x = n1.getX() + n1.getWidth();
//				int y = n1.getY();
//				String str = n1.getLabel();
//				int lbr = (str.length() - str.replace(", ", "").length()) / 2;
//				int w = 7 * str.length() - 8 * lbr - 1;
//
//				g2.setColor(Color.YELLOW);
//				g2.fillOval(x, y - 17, w + 8, 17);
//				g2.setColor(Color.BLACK);
//				g2.drawString(n1.getLabel(), x + 4, y - 4);
//
//			}
		}
		g2.setStroke(oldStroke);
	}

}