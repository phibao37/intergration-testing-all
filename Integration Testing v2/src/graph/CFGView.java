package graph;

import graph.adapter.StatementAdapter;
import graph.canvas.StatementCanvas;
import core.models.Function;
import core.unit.CFG;

/**
 * Một canvas cuộn để hiển thị nội dung một đồ thị CFG
 */
public class CFGView extends DragScrollPane implements LightTabbedPane.EqualsConstruct {
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
		
		CFG cfg = fn.getCFG(subCondition);
		canvas.setAdapter(new StatementAdapter(cfg));
	}
	
	/**
	 * Trả về canvas ở bên trong
	 */
	public StatementCanvas getCanvas(){
		return canvas;
	}

	public boolean equalsConstruct(Object... constructItem) {	
		return fn == constructItem[0] && subCondition == (Boolean)constructItem[1];
	}
}
