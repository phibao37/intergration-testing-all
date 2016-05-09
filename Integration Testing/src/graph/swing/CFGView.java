package graph.swing;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;

import api.models.IFunction;
import api.models.ITestpath;
import graph.canvas.CFGCanvas;
import graph.node.CFGNodeAdapter;

public class CFGView extends DragScrollPane 
		implements LightTabbedPane.EqualsConstruct, ComponentListener {
	private static final long serialVersionUID = 1L;

	private IFunction fn;
	private int cover;
	private CFGCanvas canvas;
	private MouseListener nodeListener;
	
	public CFGView(IFunction fn, int cover, MouseListener nodeListener) {
		this.fn = fn;
		this.cover = cover;
		this.nodeListener = nodeListener;
		
		canvas = new CFGCanvas(fn);
		setViewportView(canvas);
		addComponentListener(this);
	}
	
	public void setHightLightTestpath(ITestpath tp){
		canvas.setHightLightTestpath(tp);
	}
	
	private void addNodeListenerImidiately(){
		if (nodeListener != null){
			canvas.getAdapter().forEach(n -> n.addMouseListener(nodeListener));
			nodeListener = null;
		}
	}
	
	public int getCover(){
		return cover;
	}
	
	@Override
	public boolean equalsConstruct(Object... c) {
		return fn == c[0] && cover == (Integer)c[1];
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		if (getSize().width > 0 && !canvas.hasAdapter()){
			canvas.setAdapter(new CFGNodeAdapter(fn.getCFG(cover)));
			addNodeListenerImidiately();
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

}
