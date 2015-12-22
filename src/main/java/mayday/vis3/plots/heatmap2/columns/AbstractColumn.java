package mayday.vis3.plots.heatmap2.columns;

import mayday.core.EventFirer;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

public abstract class AbstractColumn implements HeatmapColumn {

	protected AbstractColumnGroupPlugin group;
	
	public EventFirer<UpdateEvent, UpdateListener> firer = new EventFirer<UpdateEvent, UpdateListener>() {

		@Override
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
	
	public AbstractColumnGroupPlugin getGroup() {
		return group;
	}
	
	public void setGroup(AbstractColumnGroupPlugin hcg) {
		group = hcg;
	}
	
}
