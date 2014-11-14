package mayday.genetics.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.genetics.basic.ChromosomeSetContainer;

@SuppressWarnings("serial")
public class CSCInspectorPanel extends JPanel {
	
	
	public CSCInspectorPanel(ChromosomeSetContainer... csc) {
		this(true, csc);
	}
	
	public CSCInspectorPanel(boolean editable, ChromosomeSetContainer... csc) {
		setLayout(new ExcellentBoxLayout(false, 5));
		
		SpeciesPanel sp = new SpeciesPanel(csc[0]);
		sp.setEditable(editable);

		if (csc.length>1) {
			CSCPanel cscp = new CSCPanel(csc);
			cscp.setEditable(editable);
			add(cscp);		
			add(new JLabel(">"));
			sp.setParent(cscp);
		}
		
		add(sp);
		
		add(new JLabel(">"));

		ChromosomePanel cp = new ChromosomePanel(null, null);
		cp.setParent(sp);
		cp.setEditable(editable);
		
		add(cp);
	}
	
}
