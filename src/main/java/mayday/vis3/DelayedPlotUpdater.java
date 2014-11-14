package mayday.vis3;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.gui.PlotComponent;

public class DelayedPlotUpdater extends DelayedUpdateTask implements
		SettingChangeListener {

	protected PlotComponent ppc;
	
	public DelayedPlotUpdater(PlotComponent ppc) {
		super("Delayed Plot Updater",100);
		this.ppc = ppc;
	}
	
	protected boolean needsUpdating() {
		return true;
	}

	protected void performUpdate() {
		ppc.updatePlot();
	}

	public void stateChanged(SettingChangeEvent e) {
		trigger();
	}

}
