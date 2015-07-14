package core.graph;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.graph.adapter.StatementAdapter;
import core.graph.canvas.StatementCanvas;
import core.models.Function;
import core.unit.CFG;

/**
 * Một canvas cuộn để hiển thị nội dung một đồ thị CFG
 */
public class CFGView extends JScrollPane implements LightTabbedPane.EqualsConstruct {
	private static final long serialVersionUID = 1L;
	private Function fn;
	private boolean subCondition;
	private StatementCanvas canvas;
	
	/**
	 * Tạo một canvas cuộn từ một hàm số và cấp độ phủ
	 * @param fn hàm cần hiển thị CFG
	 * @param level lấy đồ thị phủ các điểu kiện con
	 */
	public CFGView(Function fn, boolean subCondition){
		this.fn = fn;
		this.subCondition = subCondition;
		canvas = new StatementCanvas(fn);
		this.setViewportView(canvas);
		canvas.setParent(this);
		canvas.setBackground(Color.WHITE);
		
		getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JViewport v = (JViewport) e.getSource();
				v.repaint();
			}
		});
		
		CFG cfg = fn.getCFG(subCondition);
		canvas.setAdapter(new StatementAdapter(cfg));
	}

	public boolean equalsConstruct(Object... constructItem) {	
		return fn == constructItem[0] && subCondition == (Boolean)constructItem[1];
	}
}
