package mayday.core.gui.properties.items;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;

import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.gui.ExperimentCellRenderer;

@SuppressWarnings("serial")
public class ExperimentsItem extends AbstractListItem  {

	private MasterTable masterTable;
	
	public ExperimentsItem(MasterTable mt) {		
		super("Experiments");
		this.masterTable=mt;
		//speed up
		Experiment prototype = null;
		if (masterTable!=null) {
			if (masterTable.getNumberOfExperiments()>0)
				prototype = masterTable.getExperiment(0);
		};
		if (prototype!=null)
			listField.setPrototypeCellValue(prototype);
		// build list
		initList((DefaultListModel)getValue());
		initButtons();
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		return new JButton[]{EditButton};
	}
	
	
	protected void initList(DefaultListModel lm) {
		int position=0;
		lm.clear();
		for (Experiment e : masterTable.getExperiments()) {
			lm.add(position++, e);
		}
	}

	protected ListCellRenderer getCellRenderer() {
		return new ExperimentCellRenderer();
	}

}
