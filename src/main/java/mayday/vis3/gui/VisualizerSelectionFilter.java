package mayday.vis3.gui;

import mayday.vis3.model.Visualizer;

public interface VisualizerSelectionFilter {

	public boolean pass(Visualizer vis);
	
}
