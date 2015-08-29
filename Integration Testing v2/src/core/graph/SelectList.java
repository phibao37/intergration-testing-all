package core.graph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class SelectList<T> extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Cursor MOVE = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

	private int width = 0;
	private int height = 50;
	private int hgap = 5;
	private int vgap = 5;
	
	private List<Item> mItems;
	private Color mItemColor = Color.WHITE;
	private OnItemStateChange<T> mItemListener = new OnItemStateChange<T>() {
		@Override
		public void stateChanged(T item, boolean enable) {}	
	};
	
	public SelectList(){
		setLayout(null);
	}
	
	public void setModel(List<? extends T> base, List<? extends T> enable){
		mItems = new ArrayList<>(base.size());
		for (T item: enable)
			mItems.add(new Item(item, true));
		if (base != enable){
			for (T item: base)
				if (!enable.contains(item))
					mItems.add(new Item(item, false));
		}
		
		removeAll();
		for (Item item: mItems){
			add(item);
			item.setPWidth(width);
		}
		reCalculate();
	}
	
	public void setModel(T[] base, T[] enable){
		setModel(Arrays.asList(base), Arrays.asList(enable));
	}
	
	public void setHeight(int height){
		this.height = height;
		if (mItems != null)
			reCalculate();
	}
	
	public void setItemStateChangeListener(OnItemStateChange<T> listener){
		mItemListener = listener;
	}
	
	public void setItemColor(Color color){
		mItemColor = color;
		for (Item item: mItems)
			item.setBackground(color);
	}
	
	public ArrayList<T> getSelectList(){
		ArrayList<T> r = new ArrayList<>();
		
		for (Item item: mItems)
			if (item.isEnabled())
				r.add(item.item);
		return r;
	}
	
	public void setItemWidth(int width){
		this.width = width;
		for (Item item: mItems)
			item.setPWidth(width);
		reCalculate();
	}
	
	private void reCalculate(){
		int x = hgap;
		
		for (Item item: mItems){
			if (!item.dragging)
				item.setBounds(x, vgap, item.pWidth, height);
			x += item.pWidth + hgap;
		}
		
		Dimension d = new Dimension(x, height + 2*vgap);
		setSize(d);
		setPreferredSize(d);
	}
	
	private void requestNewX(int x1, int x2, Item drag){
		int newI = -1;
	
		if (x1 == hgap)
			newI = 0;
		else if (x2 == getWidth() - hgap)
			newI = mItems.size() - 1;
		else
		for (int i = 0; i < mItems.size(); i++){
			Item item = mItems.get(i);
			if (item == drag) continue;
			
			int xx1 = item.getX(), xx2 = xx1 + item.getWidth(), c = (x1+x2)/2;
			if (xx1 <= c && c <= xx2)
				newI = i;
		}
		
		if (newI >= 0){
			int old = 0;
			while (mItems.get(old) != drag)
				old++;
			
			mItems.add(newI, mItems.remove(old));
			reCalculate();
		}
	}
	
	public static interface OnItemStateChange<E>{
		public void stateChanged(E item, boolean enable);
	}
	
	private class Item extends JLabel{
		private static final long serialVersionUID = 1L;
		
		private T item;
		private int x;
		private boolean dragging;
		private int pWidth;
		
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			mItemListener.stateChanged(item, enabled);
		}

		public Item(T item, boolean enable){
			super(item.toString());
			this.item = item;
			
			setEnabled(enable);
			setHorizontalAlignment(CENTER);
			setBorder(new LineBorder(Color.BLACK));
			setOpaque(true);
			setBackground(mItemColor);
			setCursor(MOVE);
			
			MouseAdapter adapter = new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2)
						setEnabled(!isEnabled());
				}

				@Override
				public void mousePressed(MouseEvent e) {
					x = e.getPoint().x;
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (dragging){
						dragging = false;
						reCalculate();
					}
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (!dragging){
						dragging = true;
						SelectList.this.setComponentZOrder(Item.this, 0);
					}
					int newX = getX() + e.getX() - x;
					
					newX = Math.max(hgap, newX);
					newX = Math.min(newX, SelectList.this.getWidth()
							- hgap - getWidth());
					
					requestNewX(newX, newX + getWidth(), Item.this);
					setLocation(newX, getY());
				}
				
			};
			addMouseListener(adapter);
			addMouseMotionListener(adapter);
		}
		
		public void setPWidth(int width){
			if (width == 0){
				setPreferredSize(null);
				pWidth = getPreferredSize().width + 2*hgap;
			}
			else
				pWidth = width;
		}
	}
}
