package mayday.core.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JRadioButton;


public class RadioButtonTitledBorder extends ComponentTitledBorder{ 

	public RadioButtonTitledBorder(JRadioButton jrb, JComponent container){
		super(jrb, container, BorderFactory.createEtchedBorder());
	} 

}
