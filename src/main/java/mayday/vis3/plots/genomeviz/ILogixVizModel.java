package mayday.vis3.plots.genomeviz;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.genetics.basic.Species;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;

public interface ILogixVizModel {

	public long getChromosomeStart();
	public long getChromosomeEnd();

	public long getViewStart();
	public long getViewEnd();
	
	void fireChanged();

	public Chrome getActualChrome();
	
	public Species getActualSpecies();
	
	public void stateChanged(SettingChangeEvent e);
	
	public SettingChangeListener getSettingsChangeListener();
}
