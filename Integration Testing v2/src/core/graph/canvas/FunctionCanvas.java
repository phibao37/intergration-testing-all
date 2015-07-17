package core.graph.canvas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import core.S;
import core.graph.adapter.FunctionAdapter;
import core.graph.node.FunctionNode;
import core.graph.node.Node;

/**
 * Lớp đồ họa giúp hiển thị các nút đồ thị biểu diễn quan hệ giữa các hàm
 * @author ducvu
 *
 */
public class FunctionCanvas extends Canvas {
	private static final long serialVersionUID = 251346753783856185L;
	
	private FunctionAdapter mAdapter;
	private ArrayList<FunctionNode> fnNodeList = new ArrayList<FunctionNode>();

	@Override
	protected void resetAll(boolean repaint) {
		fnNodeList.clear();
		super.resetAll(repaint);
	}
	
	
	@Override
	protected void refresh() {
		setAdapter(mAdapter);
		super.refresh();
	}

	/**
	 * Tạo cây đồ thị dạng đồ họa mô tả đồ thị gọi hàm
	 * @param adapter cây cấu trúc chứa các nút đồ họa
	 */
	public void setAdapter(FunctionAdapter adapter){
		resetAll(false);
		mAdapter = adapter;
		
		int centerX = this.getWidth()/2, leftCX, leftX;
		int currentY = paddingY;
		ArrayList<FunctionNode> rowNode = new ArrayList<FunctionNode>();
		
		for (FunctionNode n: adapter){
			if (n != null){
				rowNode.add(n);
				fnNodeList.add(n);
			} else {
				//Position all node in this row
				leftCX = centerX - S.CANVAS_MARGIN_X * (rowNode.size()-1)/2;
				FunctionNode node = rowNode.get(0);
				leftX = leftCX - node.getWidth()/2;
				node.setLocation(leftX, currentY);
				for (int i = 1; i < rowNode.size(); i++){
					leftCX += S.CANVAS_MARGIN_X;
					node = rowNode.get(i);
					node.setLocation(leftCX - node.getWidth()/2, currentY);
				}
				
				currentY = currentY + S.CANVAS_MARGIN_Y;
				rowNode.clear();
			}
		}
		
		defaultNodeList.addAll(fnNodeList);
		postSetAdapter();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x1, y1, x2, y2, xs, ys;
		 Node[] refer;
		 int d = 12, h = 5, gap = 25;
		 Graphics2D g2 = (Graphics2D) g;
		 boolean rightSide;
	     
	     g2.setStroke(NORMAL_STROKE);
		 for (FunctionNode n1: fnNodeList){
			 refer = n1.getRefers();
			 xs = n1.getX() + n1.getWidth()/2;
			 ys = n1.getY() + n1.getHeight();
			 
			 for (Node n2: refer){
				 //Recurse
				 if (n2 == n1){
					 n1.setBorder(FunctionNode.DOUBLE_BORDER);
				} else {
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
						int nearSide = n2.getX()
								+ (rightSide ? 0 : n2.getWidth());
						int distance = Math.abs(nearSide
								- (x1 + n1.getWidth() / 2
										* (rightSide ? 1 : -1)));
						boolean outOfPadding = (n1.getX() + n1.getWidth() < n2.getX() 
								|| n2.getX() + n2.getWidth() < n1.getX())
								&& distance > gap + 1;

						if (outOfPadding /*&& n2.getY() >= n1.getY()*/) {
							x1 = n1.getX() + (rightSide ? n1.getWidth() : 0);
							y1 = n1.getY() + n1.getHeight() / 2;
							x2 = n2.getX() + (rightSide ? 0 : n2.getWidth());
							y2 = n2.getY() + n2.getHeight() / 2;
						} else {
							g2.drawLine(x1, y1, x1, y1 + gap);
							int tmp;
							if (outOfPadding) {
								tmp = x2 + (n2.getWidth() / 2 + gap) * (rightSide ? -1 : 1);
							} else {
								tmp = n2.getX()
										+ (rightSide ? n2.getWidth() + gap : -gap);
							}
							g2.drawLine(x1, y1 + gap, tmp, y1 + gap);
							x1 = tmp;
							y2 = y2 + n2.getHeight() / 2;
							g2.drawLine(x1, y1 + gap, x1, y2);
							y1 = y2;
							x2 = n2.getX()
									+ (rightSide ^ outOfPadding ? n2.getWidth() : 0);
						}
					}
					drawArrowLine(g2, x1, y1, x2, y2, d, h); 
				 }
			 }
		 }
		 
		 super.postPaintComponent(g2);
	}

}
