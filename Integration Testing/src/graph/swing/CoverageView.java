package graph.swing;

import java.awt.Color;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import api.models.ITestpath;
import api.solver.ISolution;

public class CoverageView extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;

	public void setModel(List<ITestpath> list_path){
		model.setRowCount(0);
		
		for (int i = 0; i < list_path.size(); i++){
			ITestpath tp = list_path.get(i);
			ISolution sln = tp.getSolution();
			
			model.addRow(new Object[]{
					i+1,
					tp,
					sln.getMessage(),
					sln.getReturnValue(),
					null,
					sln.getSolver()
			});
		}
	}
	
	/**
	 * Create the panel.
	 */
	public CoverageView() {
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null, null},
			},
			new String[] {
				"Id", "Testpath", "Argument", "Return", "Expected", "Solver"
			}
		));
		model = (DefaultTableModel) table.getModel();
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(30);
		table.getColumnModel().getColumn(4).setPreferredWidth(30);
		table.getColumnModel().getColumn(5).setResizable(false);
		table.getColumnModel().getColumn(5).setPreferredWidth(45);
		table.getColumnModel().getColumn(5).setMinWidth(45);
		setViewportView(table);

		viewport.setBackground(Color.WHITE);
	}

}
