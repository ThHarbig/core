package mayday.vis3.plots.heatmap2.interaction;

import java.util.EventListener;

public interface UpdateListener extends EventListener {
	
	public void elementNeedsUpdating(UpdateEvent evt);

}
