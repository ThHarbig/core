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
public class PercentileTableWindow extends AbstractTableWindow<PercentileTableComponent> {
	
	public PercentileTableWindow(Visualizer pg) {
		super(pg,"Percentile Table");
		for (Setting s : tabular.getSettings())
			addViewSetting(s, null);

	}
	
	public String getPreferredTitle() {
		return "Experiment Percentile Table";
	}

	protected PercentileTableComponent createTableComponent() {
		return new PercentileTableComponent(visualizer);
	}

	protected JMenu makeFileMenu() {
		JMenu table = new JMenu("Table");
		table.setMnemonic('T');		
		table.add(new ExportTableAction(tabular, getViewModel()));
		table.add(new JSeparator());
		table.add(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				visualizer.removePlot(PercentileTableWindow.this);
			}
		});
		return table;
	}

	protected void goToProbe(String name) {
		// empty		
	}

	protected boolean manageExperimentSelection() {
		return true;
	}

	protected boolean manageProbeSelection() {
		return false;
	}
	
}
