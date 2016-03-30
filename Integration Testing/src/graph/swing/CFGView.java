package graph.swing;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import api.models.IFunction;
import graph.swing.canvas.CFGCanvas;
import graph.swing.node.CFGNodeAdapter;

public class CFGView extends DragScrollPane 
		implements LightTabbedPane.EqualsConstruct, ComponentListener {
	private static final long serialVersionUID = 1L;

	private IFunction fn;
	private int cover;
	private CFGCanvas canvas;
	
	public CFGView(IFunction fn, int cover) {
		this.fn = fn;
		this.cover = cover;
		
		canvas = new CFGCanvas(fn);
		setViewportView(canvas);
		
		addComponentListener(this);
	}
	
	@Override
	public boolean equalsConstruct(Object... c) {
		return fn == c[0] && cover == (Integer)c[1];
	}

	@Override
	public void componentShown(ComponentEvent e) {
		if (!canvas.hasAdapter())
			canvas.setAdapter(new CFGNodeAdapter(fn.getCFG(cover)));
	}
	
	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

}