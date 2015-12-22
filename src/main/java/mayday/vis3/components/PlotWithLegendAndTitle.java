package mayday.vis3.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;

import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.ProbeListLegend;
import mayday.vis3.legend.SimpleTitle;

@SuppressWarnings("serial")
public class PlotWithLegendAndTitle extends BasicPlotPanel {

	protected HashMap<String,Component> legends = new HashMap<String,Component>();
	protected final static String defaultPosition = BorderLayout.SOUTH;
	
	protected Component centerComponent;
	
	public PlotWithLegendAndTitle() {
		setLayout(new BorderLayout());
		ProbeListLegend l = new ProbeListLegend();
		setLegend(l);
		SimpleTitle t = new SimpleTitle();
		setTitle(t);
	}
	
	public PlotWithLegendAndTitle(Component plot) {
		this();
		setPlot(plot);
		if (plot instanceof PlotComponent) {
			setTitledComponent((PlotComponent)plot);
		}
	}
	
	public void setTitledComponent(PlotComponent c) {
		for (Component l : legends.values()){
			if (l instanceof SimpleTitle)
				((SimpleTitle)l).setTitledComponent(c);
			if (l instanceof ProbeListLegend)
				((ProbeListLegend)l).setTitledComponent(c);
		}		
	}
	
	public void setPlot(Component plot) {
		add(plot, BorderLayout.CENTER);
		centerComponent = plot;
		setName(plot.getName());
	}
	
	public void setLegend(Component legend, String position) {
		Component previous;
		if ((previous = legends.remove(position))!=null) {
			remove(previous);
		}
		if (legend!=null) {
			legends.put(position,legend);
			add(legend, position);
		} else {
			legends.remove(position);
		}
	}
	
	public void setLegend(Component legend) {
		setLegend(legend, defaultPosition);
	}
	
	public void setTitle(Component title) {
		setLegend(title, BorderLayout.NORTH);
	}

	public void setup(PlotContainer plotContainer) {}

	public void updatePlot() {}

	
	public Component getPlot() {
		return centerComponent;
	}
	
//	public Setting getPlotSetting() {
//		return null; 
//	}
	
}
