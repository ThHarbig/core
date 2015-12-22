package mayday.vis3.plots.heatmap2.headers;

import mayday.core.EventFirer;
import mayday.core.gui.PreferencePane;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

public abstract class AbstractHeaderPlugin extends AbstractPlugin implements HeaderElement {

	public final static String MC_COL = "Visualization/Heatmap/Column Header Type";
	public final static String MC_ROW = "Visualization/Heatmap/Row Header Type";
	
	private EventFirer<UpdateEvent, UpdateListener> firer = new EventFirer<UpdateEvent, UpdateListener>() {
		protected void dispatchEvent(UpdateEvent event, UpdateListener listener) {
			listener.elementNeedsUpdating(event);
		}
	};
	
	public void addUpdateListener(UpdateListener ul) {
		firer.addListener(ul);
	}
	
	public void removeUpdateListener(UpdateListener ul) {
		firer.removeListener(ul);
	}
	
	protected void fireChange(int type) {
		firer.fireEvent(new UpdateEvent(this, type));
	}
	
	public String getPluginID() {
		return PluginManager.getInstance().getPluginFromClass(getClass()).getIdentifier();
	}
	
	public String getName() {
		return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
	}
	
	public void init() { /*for PLUMA */	}

	public PreferencePane getPreferencesPanel() {
		return null;
	}


}
