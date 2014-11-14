package mayday.vis3.graph.model;

import java.util.List;

import mayday.core.Probe;
import mayday.vis3.graph.components.MultiProbeComponent;


public interface GraphWithProbeModel
{
	public abstract List<MultiProbeComponent> getComponents(Probe probe);

}
