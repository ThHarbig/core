package mayday.vis3.tables;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.settings.Setting;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.gui.actions.ExportTableAction;
import mayday.vis3.model.Visualizer;
@SuppressWarnings("serial")
public class SampleTableWindow extends AbstractTableWindow<SampleTableComponent> {
	
	public SampleTableWindow(Visualizer pg) {
		super(pg,"Sample Table");
		for (Setting s : tabular.getSettings())
			addViewSetting(s, null);
	}
	
	public String getPreferredTitle() {
		return "Sample Table";
	}

	protected SampleTableComponent createTableComponent() {
		return new SampleTableComponent(visualizer);
	}

	protected void goToProbe(String name) {
		// nothing can be done here 
	}

	protected JMenu makeFileMenu() {
		JMenu table = new JMenu("Table");
		table.setMnemonic('T');		
		table.add(new ExportTableAction(tabular, getViewModel()));
		table.add(new JSeparator());
		table.add(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				visualizer.removePlot(SampleTableWindow.this);
			}
		});
		return table;
	}
	
	protected boolean manageExperimentSelection() {
		return true;
	}

	protected boolean manageProbeSelection() {
		return false;
	}


}
