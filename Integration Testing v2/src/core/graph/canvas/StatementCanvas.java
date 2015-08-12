package core.graph.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import core.GUI;
import core.S;
import core.graph.adapter.StatementAdapter;
import core.graph.node.Node;
import core.graph.node.StatementNode;
import core.models.Function;
import core.models.Statement;

/**
 * Lớp đồ họa giúp hiển thị các nút câu lệnh
 * @author ducvu
 * <br/>TODO bật/tắt thứ tự câu lệnh, hiện kí hiệu T/F trên đồ thị, thiết kế lại toobar
 */
public class StatementCanvas extends Canvas {
	public static final Color DEFAULT = Color.BLACK;
	public static final Color TRUE = Color.BLUE;
	public static final Color FALSE = Color.GREEN;
	public static final Color SELECTED = Color.RED;
	public static final Color SELECTED_EXTRA = Color.ORANGE;
	
	private static final Font FONT_LABEL = new Font("TimesRoman", Font.BOLD, 12);
	
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
		
		int width = getWidth();
		if (width == 0)
			width = GUI.instance.getDefaultCanvasWidth();
		
		beginNode.setLocation(width/2, paddingY);
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
					int cy = y + S.CANVAS_MARGIN_Y - rNode.getHeight() / 2;

					if (isCondition){
						if (is1StmCondition){
							if (i == 0){
								cx -= S.CANVAS_MARGIN_X;
								cy -= S.CANVAS_MARGIN_Y/2;
							}
						}
						else
							cx += i == 0 ? -S.CANVAS_MARGIN_X : S.CANVAS_MARGIN_X;
					}
					rNode.setLocation(cx, cy);
				}
			}
		}
		
		defaultNodeList.addAll(smtNodeList);
		postSetAdapter();
	}
	
	/**
	 * Lựa chọn (làm nổi bật bằng màu đỏ) một đường đi trong đồ thị
	 * @param path danh sách có thứ tự các câu lệnh trên đường đi
	 */
	public void setSelectedPath(ArrayList<Statement> path){
		resetAllSelectingPath(false);
		mAdapter.selectNodesByPath(path, 
				StatementNode.FLAG_SELECT_TRUE, StatementNode.FLAG_SELECT_FALSE, true);
		this.repaint();
	}
	
	/**
	 * Lựa chọn (làm nổi bật bằng màu vàng) một đường đi trong đồ thị
	 * @param path danh sách có thứ tự các câu lệnh trên đường đi
	 */
	public void setSelectedExtraPath(ArrayList<Statement> path){
		int flagTrue = StatementNode.FLAG_SELECT_TRUE_EXTRA,
			flagFalse = StatementNode.FLAG_SELECT_FALSE_EXTRA;
		for (StatementNode node: smtNodeList)
			node.removeFlag(flagTrue | flagFalse);
		mAdapter.selectNodesByPath(path, flagTrue, flagFalse, false);
		this.repaint();
	}
	
	/**
	 * Hủy bỏ đường đi đang được lựa chọn
	 */
	private void resetAllSelectingPath(boolean repaint){
		for (StatementNode node: smtNodeList){
			node.removeFlag(StatementNode.FLAG_SELECT_TRUE 
					| StatementNode.FLAG_SELECT_FALSE);
			node.setLabel(StatementNode.LABEL_NONE);
		}
		if (repaint)
			this.repaint();
	}
	
	/**
	 * Bỏ chọn đường đi đang được lựa chọn của canvas 
	 */
	public void resetSelectingPath(){
		resetAllSelectingPath(true);
	}
	
	/**
	 * Bỏ chọn đường đi phụ trong đồ thị
	 */
	public void resetSelectingExtraPath(){
		for (StatementNode node: smtNodeList){
			node.removeFlag(
					StatementNode.FLAG_SELECT_TRUE_EXTRA 
					| StatementNode.FLAG_SELECT_FALSE_EXTRA);
		}
		this.repaint();
	}
	
	static int[] getPoint(int x1, int y1, int x2, int y2, boolean left){
		int d = 38, r = 10;
		double anpha1 = Math.atan2(y2-y1, x2-x1),
			   anpha2 = Math.asin(r*1.0/d),
			   anpha  = anpha1 + anpha2 * (left ? 1 : -1);
		
		return new int[]{
			x1 + (int) (d * Math.cos(anpha)),
			y1 + (int) (d * Math.sin(anpha))
		};
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x1, y1, x2, y2, xs, ys;
		StatementNode[] refer;
		 int d = 12, h = 5, gap = 25;
		 Graphics2D g2 = (Graphics2D) g;
		 boolean rightSide;
	     
	     g2.setStroke(NORMAL_STROKE);
	     g2.setFont(FONT_LABEL);
		 for (StatementNode n1: smtNodeList){
			 refer = (StatementNode[]) n1.getRefers();
			 xs = n1.getX() + n1.getWidth()/2;
			 ys = n1.getY() + n1.getHeight();
			 boolean isCondition = n1.isConditionNode();
			 int length = isCondition ? refer.length : 1;
			 
			 for (int i = 0; i < length; i++){
				 StatementNode n2 = refer[i];
				 boolean isTrue = i == 0;
				 if (n2 == null) continue;
				 
				 if (n2 == n1){
					 n1.setBorder(Node.DOUBLE_BORDER);
				} else {
					Color color;
					int[] marks = null;
					
					if (n1.hasFlag(isTrue ? 
							StatementNode.FLAG_SELECT_TRUE_EXTRA
							: StatementNode.FLAG_SELECT_FALSE_EXTRA))
						color = SELECTED_EXTRA;
					else if (n1.hasFlag(isTrue ? 
							StatementNode.FLAG_SELECT_TRUE
							: StatementNode.FLAG_SELECT_FALSE))
						color = SELECTED;
					else if (isCondition)
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
							if (isCondition)
								marks = getPoint(x1, y1, x1, y1 + gap, isTrue);
							
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
					if (isCondition){
						if (marks == null)
							marks = getPoint(x1, y1, x2, y2, isTrue);
						g.drawString(isTrue ? "T" : "F",
								marks[0] - 3,
								marks[1] + 5);
					}
				 }
			 }
			 
			 if (!n1.getLabel().isEmpty()) {
					int x = n1.getX() + n1.getWidth();
					int y = n1.getY();
					String str = n1.getLabel();
					int lbr = (str.length() - str.replace(", ", "").length()) / 2;
					int w = 7 * str.length() - 8 * lbr - 1;

					g2.setColor(Color.YELLOW);
					g2.fillOval(x, y - 17, w + 8, 17);
					g2.setColor(Color.BLACK);
					g2.drawString(n1.getLabel(), x + 4, y - 4);

				}
		 }
		 
		 super.postPaintComponent(g2);
	}
	
	/** Mở nội dung mã nguồn chứa nội dung của hàm ứng với đồ thị này*/
	protected void openViewSource(){
		GUI.instance.openFileView(mFunc.getSourceFile());
	}
	
	protected void openViewPath(){
		GUI.instance.beginTestFunction(getFunction());
	}
	
	@Override
	protected void resetAll(boolean repaint) {
		smtNodeList.clear();
		super.resetAll(repaint);
	}
	
	
	@Override
	protected void refresh() {
		for (StatementNode node: mAdapter)
			node.setLocation(0, 0);
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
			viewSource = new JMenuItem("Xem nguồn");
			viewSource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					canvas.openViewSource();
				}
			});
			this.add(viewSource);
			
			seePath = new JMenuItem("Kiểm thử đơn vị");
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
