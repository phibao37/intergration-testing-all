package graph.node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.util.List;

import api.expression.IReturnExpression;
import api.models.IStatement;
import core.Utils;

public class CFGNode extends Node<IStatement> {
	private static final long serialVersionUID = 1L;
	public static final int NORMAL = 0, CONDITION = 1, MARK = 2,
			PADDING_X = 20, PADDING_Y = 10, MARK_SIZE = 25,
			MAX_IN_BLOCK = 10;
	
	private int type;
	private Color borderColor;
	
	public CFGNode(IStatement stm) {
		super(stm);
		Dimension size = getPreferredSize();
		
		if (stm.isCondition()){
			type = CONDITION;
			size.width += 2*PADDING_X;
			size.height += 2*PADDING_Y;
		}
		else if (stm.isNormal()){
			type = NORMAL;
			size.width += PADDING_X;
			size.height += PADDING_Y;
		}
		else{
			type = MARK;
			setText(null);
			size.width = size.height = MARK_SIZE;
		}
		
		setSize(size);
		setBorderColor(Color.BLACK);
		setBackground(SystemColor.inactiveCaptionBorder);
	}
	
	public CFGNode(List<IStatement> stmList){
		setElement(stmList.get(0));
		type = NORMAL;
		
		StringBuilder txt = new StringBuilder(), real = new StringBuilder();
		boolean large = false;
		int count = 0;
		
		for (IStatement stm: stmList){
			String s = stm.getContent();
			real.append(s).append("<br>");
			
			if (count++ < MAX_IN_BLOCK){
				if (s.length() <= MAX_STR_LEN)
					txt.append(s);
				else {
					txt.append(s.substring(0, MAX_STR_LEN-3)).append("...");
					large = true;
				}
				txt.append("<br>");
			}
			else
				large = true;
		}
		setText(Utils.htmlCenter(txt.toString()));
		if (large)
			setToolTipText(Utils.html(real.toString()));
		
		Dimension size = getPreferredSize();
		size.width += PADDING_X;
		size.height += PADDING_Y;
		setSize(size);
		setBorderColor(Color.BLACK);
		setBackground(SystemColor.inactiveCaptionBorder);
	}
	
	public boolean isCondition(){
		return getElement().isCondition();
	}
	
	/** Kiểm tra nút có là nút điều kiện đơn giản <br/>
	 * (1 câu lệnh duy nhất tại nhánh, hoặc câu lệnh return) hay không*/
	public boolean is1StmCondition(){
		if (!isCondition())
			return false;
		IStatement stm = getElement(), 
				trueStm = stm.getTrue(),
				falseStm = stm.getFalse();
		if (trueStm.isCondition())
			return false;
		return trueStm.getRoot() instanceof IReturnExpression
				|| trueStm.getTrue() == falseStm;
	}

	public void setBorderColor(Color color){
		borderColor = color;
	}
	
	public Color getBorderColor(){
		return borderColor;
	}
	
	public int getType(){
		return type;
	}
	
	public boolean inside(int x, int y, int width, int height){
		switch (type){
		case NORMAL:
			return x >= 0 && x < width && y >= 0 && y < height;
		case CONDITION:
			long w21 = width/2, h21 = height/2, 
			X = (x - w21)*h21, Y = (y - h21)*w21, Z = w21*h21;
			return X + Y < Z && X - Y < Z
				&& -X + Y < Z && -X - Y < Z;
		case MARK:
			long w2 = width/2, h2 = height/2,
				W2 = w2*w2, H2 = h2*h2;
			return H2*(x-w2)*(x-w2) + W2*(y-h2)*(y-h2) <= W2*H2;
		default: return false;
		}
	}
	
	protected void paint(Graphics2D g, int x, int y, int width, int height){
		switch (type){
		case NORMAL:
			g.setColor(getBackground());
			g.fillRect(x, y, width, height);
			g.setColor(getBorderColor());
			g.drawRect(x, y, width, height);
			break;
		case CONDITION:
			int w2 = width/2, h2 = height/2;
			int[] X = {0, w2, width, w2},
					Y = {h2, 0, h2, height};
			g.setColor(getBackground());
			g.fillPolygon(X, Y, 4);
			g.setColor(getBorderColor());
			g.drawPolygon(X, Y, 4);
			break;
		case MARK:
			g.setColor(getBackground());
			g.fillOval(x, y, width, height);
			g.setColor(getBorderColor());
			g.drawOval(x, y, width, height);
			break;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;

            Color oldColor = g2d.getColor();
            paint(g2d, 0, 0, getWidth() - 1, getHeight() - 1);
            g2d.setColor(oldColor);
        }
		super.paintComponent(g);
	}

	@Override
	public boolean inside(int x, int y){
		return inside(x, y, getWidth(), getHeight());
	}
}
