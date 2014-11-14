package mayday.vis3.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

@SuppressWarnings("serial")
public class CombineAllOpenPlotsAction extends AbstractAction {

	private Visualizer visualizer;
	
	public CombineAllOpenPlotsAction(Visualizer pg) {
		super("Combine All");
		visualizer=pg;
	}
	
	public void actionPerformed(ActionEvent e) {
		final LinkedList<Component> comps = new LinkedList<Component>();
		LinkedList<PlotWindow> pws = new LinkedList<PlotWindow>();
		
		for (VisualizerMember vm : visualizer.getMembers()) {
			if (vm instanceof PlotWindow) {
				PlotWindow pw = (PlotWindow)vm;
				Component c = pw.getContent();
				if (c instanceof PlotWithLegendAndTitle)
					comps.add(((PlotWithLegendAndTitle)c).getPlot());
				else
					comps.add(c);
				pws.add(pw);
			}
		}
		
		if (pws.size()==1)
			return; 
		
		for (PlotWindow pw : pws)
			visualizer.removePlot(pw);
		
		MultiPlotPanel newComponent = new MultiPlotPanel() {
			public void setup(PlotContainer parent) {				
				super.setup(parent);
				setPlots(comps.toArray(new Component[0]));
			}			
		};
		
		PlotWithLegendAndTitle pwl = new PlotWithLegendAndTitle(newComponent);
		pwl.setTitledComponent(null);
	
		PlotWindow pw = new PlotWindow(pwl, visualizer);		
		pw.setVisible(true);
		visualizer.addPlot(pw);
		
	}

}
