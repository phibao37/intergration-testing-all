package core.graph.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import core.graph.adapter.StatementAdapter;
import core.graph.node.Node;
import core.graph.node.StatementNode;
import core.models.Function;

/**
 * Lớp đồ họa giúp hiển thị các nút câu lệnh
 * @author ducvu
 *
 */
public class StatementCanvas extends Canvas {
	public static final Color DEFAULT = Color.BLACK;
	public static final Color TRUE = Color.BLUE;
	public static final Color FALSE = Color.GREEN;
	public static final Color SELECTED = Color.RED;
	
	private static final long serialVersionUID = 1L;
	private Function mFunc;
	private StatementAdapter mAdapter;
	private ArrayList<StatementNode> smtNodeList = new ArrayList<StatementNode>();
	
	/** Tạo một đồ thị tương ứng với một hàm */
	public StatementCanvas(Function fn){
		this.mFunc = fn;
	}
	
	public StatementCanvas() {}
	public void setFunction(Function fn){
		this.mFunc = fn;
		setAdapter(new StatementAdapter(fn.getCFG(true)));
	}
	
	/** Tạo cây đồ họa mô tả đồ thị dòng điều khiển*/
	public void setAdapter(StatementAdapter adapter){
		this.mAdapter = adapter;
		resetAll(false);
		smtNodeList.addAll(adapter);
		StatementNode beginNode = smtNodeList.get(0);
		
		//TODO ?beginNode.setLocation(GUI.instance.getGraphWidth()/2, paddingY);
		beginNode.setLocation(getWidth()/2, paddingY);
		for (StatementNode node: smtNodeList){
			StatementNode[] refer = (StatementNode[]) node.getRefers();
			boolean isCondition = node.isConditionNode();
			 boolean is1StmCondition = node.is1StmConditionNode();
			int x = node.getX() + node.getWidth()/2;
			int y = node.getY() + node.getHeight()/2;
			int length = isCondition ? refer.length : 1;
			
			
			for (int i = 0; i < length; i++){
				StatementNode rNode = refer[i];
				
				if (rNode == null)
					continue;
				if (!(rNode.isLocationSet() && rNode.isConditionNode())) {
					int cx = x - rNode.getWidth() / 2;
					int cy = y + marginY - rNode.getHeight() / 2;

					if (isCondition){
						if (is1StmCondition){
							if (i == 0){
								cx -= marginX;
								cy -= marginY/2;
							}
						}
						else
							cx += i == 0 ? -marginX : marginX;
					}
					rNode.setLocation(cx, cy);
				}
			}
		}
		
		defaultNodeList.addAll(smtNodeList);
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
		 for (StatementNode n1: smtNodeList){
			 refer = n1.getRefers();
			 xs = n1.getX() + n1.getWidth()/2;
			 ys = n1.getY() + n1.getHeight();
			 boolean isCondition = n1.isConditionNode();
			 int length = isCondition ? refer.length : 1;
			 
			 for (int i = 0; i < length; i++){
				 Node n2 = refer[i];
				 if (n2 == null) continue;
				 
				 if (n2 == n1){
					 n1.setBorder(Node.DOUBLE_BORDER);
				} else {
					Color color;
					if (isCondition)
						color = i == 0 ? TRUE : FALSE;
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
	
	/** Mở nội dung mã nguồn chứa nội dung của hàm ứng với đồ thị này*/
	protected void openViewSource(){
		//GUI.instance.openFileView(fn.getFile());
	}
	
	protected void openViewPath(){
		//UnitMain unit = fn.getUnitSuite();
		//int level = BodyStatementVisitor.STATEMENT_CONDITION;
		//GUI.instance.setTestPath(unit.getBasisPath(level), unit.getSolutions(level));
	}
	
	@Override
	protected void resetAll(boolean repaint) {
		smtNodeList.clear();
		super.resetAll(repaint);
	}
	
	
	@Override
	protected void refresh() {
		setAdapter(mAdapter);
		super.refresh();
	}
	
	/** Trả về hàm tương ứng*/
	public Function getFunction(){
		return mFunc;
	}
	
	protected void openMenu(MouseEvent e) {
		menu.openPopupNode(this, e);
	}

	private static PopupMenu menu = new PopupMenu();

	private static class PopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		private StatementCanvas canvas;
		private JMenuItem viewSource, seePath;

		private PopupMenu() {
			viewSource = new JMenuItem("View source");
			viewSource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					canvas.openViewSource();
				}
			});
			this.add(viewSource);
			
			seePath = new JMenuItem("View path");
			seePath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					canvas.openViewPath();
				}
			});
			this.add(seePath);
		}

		private void openPopupNode(StatementCanvas n, MouseEvent e) {
			canvas = n;
			this.show(canvas, e.getX(), e.getY());
		}
	}
}
