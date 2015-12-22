package mayday.vis3.plots.profilelogo.legend;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;

import mayday.core.math.binning.AbstractBinningStrategy;
import mayday.core.math.binning.Binning;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.LegendItem;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.profilelogo.renderer.BinningRendererTool;
import mayday.vis3.plots.profilelogo.renderer.ProfileLogoRenderer;

@SuppressWarnings("serial")
public class BinningLegend extends BasicPlotPanel implements SettingChangeListener, ViewModelListener {

	protected Binning binning;
	protected BooleanSetting visibility;
	protected PlotComponent titledComponent;
	protected ViewModel vm;
	protected ColorGradient gradient; 
	
	public BinningLegend(Binning b, PlotComponent titledComponent) {
		binning = b;
		//setLayout(new GridLayout(0,1));		
		setVisible(false);
		setBackground(Color.WHITE);
		setOpaque(true);
		binning.strategy.addChangeListener(this);
		visibility = new BooleanSetting("Show legend",null,false);
		visibility.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				setVisible(visibility.getBooleanValue());
			}
		});
		this.titledComponent = titledComponent;
	}
	
	public void setGradient(ColorGradient gradient) {
		this.gradient = gradient;
	}
	
	public static String formatNumber(Double Number) {
		String label = ""+ Number;
		if (label.length()>6) {
			if (label.contains("E"))
				label = label.substring(0, 5)+label.substring(label.lastIndexOf('E'));
			else 
				label = label.substring(0,5);
		}
		return label;
	}
	
	public void updateLegend() {
		removeAll();

		LinkedList<String> thr = new LinkedList<String>();
		Double lastD=0.0;
		
		AbstractBinningStrategy strat = binning.strategy.getInstance();
		
		List<Double> threshs = strat.getThresholds(vm);
		
		for (int i=0; i!=threshs.size(); ++i) {
			Double d = threshs.get(i);
			if (i==0) {
				thr.add("<html>x &lt; "+formatNumber(d));				
			}
			else {
				thr.add("<html>"+formatNumber(lastD)+" &lt; x &lt; "+formatNumber(d));
			}
			lastD=d;
		}
		thr.add("<html>"+formatNumber(lastD)+" &lt; x");
		
		int[] dims = MultiPlotPanel.findBestRC(thr.size());
		setLayout(new GridLayout(dims[1],dims[0]));
		ProfileLogoRenderer[] renderer = BinningRendererTool.suggestRenderer(binning.getNumberOfBins(vm),gradient);
		for (int i=0; i!=renderer.length; ++i)
			add(new LegendItem(renderer[i].getColor(),thr.get(i)));
		revalidate();
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		plotContainer.addViewSetting(visibility, titledComponent);
		vm = plotContainer.getViewModel();
		vm.addViewModelListener(this);
		updateLegend();
	}

	public void updatePlot() {
	}

	public void stateChanged(SettingChangeEvent e) {
		updateLegend();
	}
	
	public void removeNotify() {
		super.removeNotify();
		vm.removeViewModelListener(this);
	}
	
	public void viewModelChanged(ViewModelEvent vme) {
		updateLegend();
	}
	
}
