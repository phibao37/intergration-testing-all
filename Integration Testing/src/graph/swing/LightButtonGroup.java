package graph.swing;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class LightButtonGroup extends ButtonGroup {
	private static final long serialVersionUID = 1L;

	public void setSelectedButton(String action){
		Enumeration<AbstractButton> elms = getElements();
		
		while (elms.hasMoreElements()){
			AbstractButton b = elms.nextElement();
			
			if (action.equals(b.getActionCommand())){
				setSelected(b.getModel(), true);
				break;
			}
		}
	}
	
	public String getSelectedAction(){
		return getSelection().getActionCommand();
	}
}
