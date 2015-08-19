package core.graph.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import core.GUI;
import core.S;
import core.Utils;
import core.graph.adapter.FunctionAdapter;
import core.graph.node.FunctionNode;
import core.graph.node.Node;
import core.inte.FunctionPair;
import core.models.Function;

/**
 * Lớp đồ họa giúp hiển thị các nút đồ thị biểu diễn quan hệ giữa các hàm
 * @author ducvu
 *
 */
public class FunctionCanvas extends Canvas {
	private static final long serialVersionUID = 251346753783856185L;
	public static final Color DEFAULT = Color.BLACK;
	public static final Color SELECTED = Color.RED;
	
	private FunctionAdapter mAdapter;
	private ArrayList<FunctionNode> fnNodeList = new ArrayList<>();
	
	private ArrayList<Line> listLine = new ArrayList<>();
	private Function source, target;
	private boolean mShowStub = false;

	public FunctionCanvas() {
		JButton stub = new JButton(new ImageIcon(
				Canvas.class.getResource("/image/stub.png")));
		stub.setToolTipText(Utils.html(
				"Thêm Stub<br/>[Ctrl+Click]: Xóa ô nhập"));
		stub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				mShowStub = !mShowStub;
				for (FunctionNode n: fnNodeList)
					n.setStubFieldVisible(mShowStub, 
							Utils.hasFlag(e.getModifiers(), ActionEvent.CTRL_MASK),
					i++ == 0);
			}
		});
		toolbar.add(stub);
	}
	
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
	
	/**
	 * Đánh dấu một cặp hàm-gọi-hàm là đang được chọn
	 */
	public void setSelectFunctionPair(FunctionPair pair){
		for (FunctionNode node: fnNodeList)
			node.clearAllSelectedRefer();
		mAdapter.selectFunctionPair(pair);
		this.repaint();
	}
	
	/**
	 * Bỏ đánh dấu tất cả các cặp hàm gọi hàm đang được chọn
	 */
	public void clearAllSelectedFunctionPair(){
		for (FunctionNode node: fnNodeList)
			node.clearAllSelectedRefer();
		this.repaint();
	}
	
	private Line add(int x1, int y1, int x2, int y2){
		Line r = new Line(x1, y1, x2, y2, source, target);
		listLine.add(r);
		return r;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int D = 7, x = e.getX(), y = e.getY();
		boolean notFound = true;
		
		for (Line r: listLine)
			if (r.intersects(x-D, y-D, 2*D, 2*D)){
				GUI.instance.functionPairClicked(r.source, r.target, 
						e.getClickCount() == 2);
				notFound = false;
				break;
			}
		
		if (notFound)
			super.mouseClicked(e);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(NORMAL_STROKE);
		
		listLine.clear();
		
		int x1, y1, x2, y2, xs, ys;
		 Node[] refer;
		 int d = 12, h = 5, gap = 25;
		 boolean rightSide;
	     
		 for (FunctionNode n1: fnNodeList){
			 refer = n1.getRefers();
			 xs = n1.getX() + n1.getWidth()/2;
			 ys = n1.getY() + n1.getHeight();
			 source = n1.getFunction();
			 
			 int i = 0;
			 for (Node n2: refer){
				 target = (Function) n2.getElement();
				 g.setColor(n1.isSelectedRefer(i++) ? SELECTED : DEFAULT);
				 
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
							g2.draw(add(x1, y1, x1, y1 + gap));
							int tmp;
							if (outOfPadding) {
								tmp = x2 + (n2.getWidth() / 2 + gap) * (rightSide ? -1 : 1);
							} else {
								tmp = n2.getX()
										+ (rightSide ? n2.getWidth() + gap : -gap);
							}
							g2.draw(add(x1, y1 + gap, tmp, y1 + gap));
							x1 = tmp;
							y2 = y2 + n2.getHeight() / 2;
							g2.draw(add(x1, y1 + gap, x1, y2));
							y1 = y2;
							x2 = n2.getX()
									+ (rightSide ^ outOfPadding ? n2.getWidth() : 0);
						}
					}
					drawArrow(g, x1, y1, x2, y2, d, h);
					
					
					g2.draw(add(x1, y1, x2, y2));
				 }
			 }
		 }
		 
		 super.postPaintComponent(g);
	}
	
	static class Line extends Line2D.Double{
	private static final long serialVersionUID = 1L;
	
	private Function source, target;
	
	public Line(int x1, int y1, int x2, int y2, Function source, Function target){
		super(x1, y1, x2, y2);
		this.source = source;
		this.target = target;
	}
	
	
}
	
//	static class Rectangle extends Polygon{
//		private static final long serialVersionUID = 1L;
//		
//		private Function source, target;
//		
//		public Rectangle(int x1, int y1, int x2, int y2, double r, 
//				Function source, Function target){
//			this.source = source;
//			this.target = target;
//			int dx = x2 - x1, dy = y2 - y1;
//			double d = Math.sqrt((dx*dx+dy*dy)/2.0);
//			int m = (int)(r*(dx+dy)/d), 
//				n = (int)(r*(dx-dy)/d);
//			
//			addPoint(x1 - m, y1 + n);
//			addPoint(x1 - n, y1 - m);
//			addPoint(x2 + m, y2 - n);
//			addPoint(x2 + n, y2 + m);
//		}
//
//		@Override
//		public String toString() {
//			return source.getName() + " -> " + target.getName();
//		}
//		
//		
//	}

}
