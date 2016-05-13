package graph.swing;

import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import api.IRunProcess;
import graph.swing.tablelayout.TableLayout;
import graph.swing.tablelayout.TableLayout.TableRow;

public class ProcessView<E> extends JPanel {
	private static final long serialVersionUID = 1L;

	private TableLayout layout;
	private ProcessStateChanged<E> stateChanged;
	private HashMap<E, TableRow> mapRow;
	
	public ProcessView() {
		layout = new TableLayout(this, new double[][]{
			{10, TableLayout.FILL, 40, 40, 10}, {}
		});
		setLayout(layout);
		
		mapRow = new HashMap<>();
	}
	
	public void addAndRun(IRunProcess<E> p){
		p.setStateChange(itemStateChanged);
		p.thread().start();
	}
	
	@Override
	public void removeAll() {
		layout.clearRows();
		mapRow.clear();
		super.removeAll();
		repaint();
	}
	
	public void clearRow(E element){
		TableLayout.TableRow row = mapRow.remove(element);
		
		if (row != null)
			layout.deleteRow(row);
	}

	private IRunProcess.OnStateChange<E> itemStateChanged = (p, s) -> {
		E element = p.getElement();
		TableLayout.TableRow row = mapRow.get(element);
		
		if (row == null){
			JLabel title = new JLabel(element.toString());
			JLabel status = new JLabel();
			JButton action = new JButton();
			action.setBorder(null);
			action.setContentAreaFilled(false);
			
			row = layout.insertRow(0, 40, true, "c c l c c c l c", null,
					title, status, action);
			mapRow.put(element, row);
		}
		
		if (stateChanged != null)
			stateChanged.stateChanged(p, element, s, 
					(JLabel)row.getComponent(1),
					(JLabel)row.getComponent(2), 
					(JButton) row.getComponent(3));
	};
	
	public void setStateChanged(ProcessStateChanged<E> stateChanged) {
		this.stateChanged = stateChanged;
	}

	public interface ProcessStateChanged<E>{
		void stateChanged(IRunProcess<E> process, E element, int state,
				JLabel title, JLabel status, JButton action);
	}
}
