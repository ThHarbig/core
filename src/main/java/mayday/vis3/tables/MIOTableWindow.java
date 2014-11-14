package mayday.vis3.tables;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.settings.Setting;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.gui.actions.ExportTableAction;
import mayday.vis3.gui.actions.GoToProbeAction;
import mayday.vis3.model.Visualizer;
@SuppressWarnings("serial")
public class MIOTableWindow extends AbstractTableWindow<MIOTableComponent> {
	
	public MIOTableWindow(Visualizer pg) {
		super(pg,"MIO Table");
		for (Setting s : tabular.getSettings())
			addViewSetting(s, null);

		JMenu settings = getMenu(VIEW_MENU, null);

		settings.add(new GoToProbeAction() {
			public boolean goToProbe(String probeIdentifier) {
				return tabular.goToProbe(probeIdentifier);
			}
		});
		settings.add(new JumpToSelectionAction());
	}
	
	public String getPreferredTitle() {
		return "MIO Table";
	}

	protected MIOTableComponent createTableComponent() {
		return new MIOTableComponent(visualizer);
	}

	protected void goToProbe(String name) {
		tabular.goToProbe(name);
	}

	protected JMenu makeFileMenu() {
		JMenu table = new JMenu("Table");
		table.setMnemonic('T');		
		table.add(new ExportTableAction(tabular, getViewModel()));
		table.add(new JSeparator());
		table.add(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				visualizer.removePlot(MIOTableWindow.this);
			}
		});
		return table;
	}
	
	protected boolean manageExperimentSelection() {
		return false;
	}

	protected boolean manageProbeSelection() {
		return true;
	}

}
