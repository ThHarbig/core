package mayday.vis3.legend;

import java.awt.Color;
import java.awt.GridLayout;

import mayday.core.ProbeList;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class ProbeListLegend extends BasicPlotPanel implements SettingChangeListener, ViewModelListener {

	protected ViewModel viewModel;
	protected PlotComponent titledComponent;
	
	protected BooleanSetting legendVisible;
	protected ObjectSelectionSetting<String> style;
	protected HierarchicalSetting setting;
	
	protected final static String STYLE_HORIZONTAL = "Horizontal";
	protected final static String STYLE_VERTICAL = "Vertical";
	protected final static String STYLE_MATRIX = "Matrix";
	
	public ProbeListLegend() {
		//setLayout(new GridLayout(0,1));		
		legendVisible = new BooleanSetting("Show Legend",null,false);
		style = new ObjectSelectionSetting<String>("Layout style",null,0,
				new String[]{STYLE_MATRIX, STYLE_HORIZONTAL, STYLE_VERTICAL});
		setting = new HierarchicalSetting("Legend").addSetting(legendVisible).addSetting(style);
		setting.addChangeListener(this);
		
		setVisible(legendVisible.getBooleanValue());
		
		setBackground(Color.WHITE);
		setOpaque(true);
	}
	
	public ProbeListLegend(PlotComponent titledComponent) {
		this();
		setTitledComponent(titledComponent);
	}
	
	public void updateLegend() {
		removeAll();
		int elementcount = viewModel.getProbeLists(false).size();
		int[] dims;
		switch(style.getSelectedIndex()) {
		case 1: // horizontal
			dims = new int[]{1,elementcount};
			break;
		case 2: // vertical
			dims = new int[]{elementcount,1};
			break;
		default: // matrix
			dims = MultiPlotPanel.findBestRC(elementcount);
		}
		setLayout(new GridLayout(dims[1],dims[0]));		
		for (ProbeList pl : viewModel.getProbeLists(false)) {
			add(new ProbeListLegendItem(pl));
		}
		revalidate();
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		viewModel = plotContainer.getViewModel();
		viewModel.addViewModelListener(this);
		plotContainer.addViewSetting(setting, titledComponent);
		updateLegend();
	}

	public void setTitledComponent(PlotComponent c) {
		titledComponent = c;
	}
	
	public void updatePlot() {
	}

	public void stateChanged(SettingChangeEvent e) {
		if (e.hasSource(style))
			updateLegend();
		if (e.hasSource(legendVisible))
			setVisible(legendVisible.getBooleanValue());
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED || vme.getChange()==ViewModelEvent.PROBELIST_ORDERING_CHANGED) {
			updateLegend();
		}
	}
	
	public void removeNotify() {
		super.removeNotify();
		viewModel.removeViewModelListener(this);
	}
	
}
