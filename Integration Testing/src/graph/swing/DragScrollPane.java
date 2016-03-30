package graph.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Vùng cuộn cho phép kéo thả để cuộn
 */
public class DragScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private static Cursor MOVE = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	
	private int x, y;
	
	public DragScrollPane(){
		Dimension dimen = new Dimension(0, 0);
		int unit = 10;
		
		verticalScrollBar.setPreferredSize(dimen);
		horizontalScrollBar.setPreferredSize(dimen);
		verticalScrollBar.setUnitIncrement(unit);
		horizontalScrollBar.setUnitIncrement(unit);
		
		viewport.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				viewport.repaint();
			}
		});
	}

	@Override
	public void setViewportView(Component view) {
		super.setViewportView(view);
		
		view.removeMouseListener(mListener);
		view.removeMouseMotionListener(mListener);
		view.addMouseListener(mListener);
		view.addMouseMotionListener(mListener);
	}
	
	private MouseAdapter mListener = new MouseAdapter() {

		
		@Override
		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			setCursor(null);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			setCursor(MOVE);
			int newX = horizontalScrollBar.getValue() - e.getX() + x;
			int newY = verticalScrollBar.getValue() - e.getY() + y;
			
			horizontalScrollBar.setValue(newX);
			verticalScrollBar.setValue(newY);
		}

	};
	
}
