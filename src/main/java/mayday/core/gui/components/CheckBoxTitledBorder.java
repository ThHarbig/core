package mayday.core.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;


public class CheckBoxTitledBorder extends ComponentTitledBorder{ 

	public CheckBoxTitledBorder(JCheckBox jcb, JComponent container){
		super(jcb, container, BorderFactory.createEtchedBorder());
	} 

}
