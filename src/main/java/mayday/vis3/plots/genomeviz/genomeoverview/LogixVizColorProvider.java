package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayFrame;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.NumericMIO;
import mayday.vis3.ColorProvider;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.model.ViewModel;

public class LogixVizColorProvider extends ColorProvider{
	
	protected MaydayFrame expListFrame;
	protected MaydayFrame stemTrackFrame;
	
	public LogixVizColorProvider(ViewModel vm) {
		super(vm);
		this.setExperiment(0);
	}
	
	
	
	public void removeNotify() {
		if (stemTrackFrame!=null)
			stemTrackFrame.dispose();
		viewModel.removeViewModelListener(this);
	}

	public void closeFrames() {
		if(this.expListFrame!=null){
			expListFrame.setVisible(false);
			expListFrame.dispose();
		}
		
		if(this.stemTrackFrame!=null){
			stemTrackFrame.setVisible(false);
			stemTrackFrame.dispose();
		}
		
		viewModel.removeViewModelListener(this);
	}
	


	public void setSetting(ColorProviderSetting setting) {
		this.setting = setting;
	}
	
//	public String getExperimentName(){
//		return this.viewModel.getDataSet().getMasterTable().getExperimentName(experiment);
//	}

	/**
	 * return double value for a probe, only possible for COLOR_BY_EXPERIMENT_VALUE and COLOR_BY_MIO_VALUE
	 * others returns NaN; categoricalMIO in COLOR_BY_MIO_VALUE also returns NaN.
	 * @param pb
	 * @return double value or NaN
	 */
	@SuppressWarnings("unchecked")
	public double getValue(Probe pb) {
		double val= Double.NaN;
		switch(colorMode) {
		case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
			val= viewModel.getProbeValues(pb)[experiment];
			break;
		case ColorProviderSetting.COLOR_BY_MIO_VALUE:
			GenericMIO mt = (GenericMIO)mg.getMIO(pb);
			if (mt!=null) {
				if (categoricalMIO) {
					return val;
				} else {
					val = ((NumericMIO<Number>)mt).getValue().doubleValue();
				}								
			}
			break;
		}
		return val;
	}
	
	@SuppressWarnings("unchecked")
	public TreeMap<Integer,Color> getSortedColorList(Set<Probe> list) {
		Color c = Color.black; // default to black
		HashMap<Color, Integer> hm = new HashMap<Color, Integer>();
		Double v;
		
		switch(colorMode) {
		case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST:
			for(Probe pb: list){
				ProbeList pl = viewModel.getTopPriorityProbeList(pb);
				if (pl!=null){
					c = pl.getColor();
					addValueToList(c, hm);
				}
			}
			break;
		case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
			for(Probe pb: list){
				v = viewModel.getProbeValues(pb)[experiment];
				addValueToList(getColor(v), hm);
				
			}
			break;
		case ColorProviderSetting.COLOR_BY_MIO_VALUE:
			for(Probe pb: list){
				GenericMIO mt = (GenericMIO)mg.getMIO(pb);
				if (mt!=null) {
					if (categoricalMIO) {
						addValueToList(categoricalColors.get(mt.getValue()), hm);
					} else {
						v = ((NumericMIO<Number>)mt).getValue().doubleValue();						
						addValueToList(getColor(v), hm);
					}								
				}
			}
			break;
		}
		
		TreeMap<Integer,Color> ts=new TreeMap<Integer,Color>();
		for (Color col : hm.keySet()) {
			ts.put(hm.get(col), col);
		}
		return ts;
	}

	public Color getHighestColorOccurence(Set<Probe> list) {
		if (list!=null && list.size()>0) {
			TreeMap<Integer,Color> c = getSortedColorList(list);
			if (c.size()>0)
				return c.lastEntry().getValue();
		}
		return Color.black;
	}


	private void addValueToList(Color c, HashMap<Color, Integer> hm) {
		if(hm.containsKey(c)){
			int count = hm.get(c);
			hm.put(c, (count+1));
		}else{
			hm.put(c, 1);
		}
	}
	
	public void fireChanged() { // expose to outside world
		super.fireChanged();
	}

}
