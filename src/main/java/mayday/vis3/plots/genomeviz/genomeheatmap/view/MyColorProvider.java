package mayday.vis3.plots.genomeviz.genomeheatmap.view;

import java.awt.Color;
import java.util.List;

import mayday.core.Probe;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.NumericMIO;
import mayday.vis3.ColorProvider;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;

public class MyColorProvider extends ColorProvider{
 
	public MyColorProvider(ViewModel vm) {
		super(vm);	
	}

	public ColorGradient getColorGradient() {
		return colorGradient;
	}
	
	public void updateGradient_manual() {
		super.updateGradient();
	}
	
	public void setMinMax(double min, double max){
		colorGradient.setMax(max);
		colorGradient.setMin(min);
	}
	
	public void fireEvent(){
		this.fireChanged();
	}
	
	public Color getColorByMioForProbelist(List<Probe> probelist, SplitView splitView){
		Double v = 0.;
		Color c = null;
		
		if(!categoricalMIO){
			switch (splitView) {		
			case mean:
				v = getMeanValue_MIO(probelist, v);
				c = getColor(v);
				return c;
			case max:
				v = getMaxValue_MIO(probelist, v);
				c = getColor(v);
				return c;
			case min:
				v = getMaxValue_MIO(probelist, v);
				c = getColor(v);
				return c;
			default:
				return null;
			}
		} else {
			return Color.BLACK;
		}
	}

//	@SuppressWarnings("unchecked")
//	private Double getFirstValue_Mio(List<Probe> probelist, Double v) {
//		GenericMIO<Number> mt = null;
//		mt = (GenericMIO<Number>) mg.getMIO(probelist.get(0));
//		if (mt != null) {
//				v = ((NumericMIO<Number>) mt).getValue().doubleValue();
//		}
//		return v;
//	}

	@SuppressWarnings("unchecked")
	private Double getMeanValue_MIO(List<Probe> probelist, Double v) {
		GenericMIO<Number> mt = null;
		for (Probe pb : probelist) {
			mt = (GenericMIO<Number>) mg.getMIO(pb);
			if (mt != null) {
					v += ((NumericMIO<Number>) mt).getValue().doubleValue();
			}
		}
		return v/probelist.size();
	}
	
	@SuppressWarnings("unchecked")
	private Double getMaxValue_MIO(List<Probe> probelist, Double maxVal) {
		maxVal = -Double.MAX_VALUE;
		GenericMIO<Number> mt = null;
		for (Probe pb : probelist) {
			mt = (GenericMIO<Number>) mg.getMIO(pb);
			if (mt != null) {
				double val = ((NumericMIO<Number>) mt).getValue().doubleValue();
				if (maxVal < val) {
					maxVal = val;
				}
			}
		}
		return maxVal;
	}

	public void init(int i, int j) {
		setExperiment(i);
		setMode(j);
		fireEvent();
	}

	public void init(ColorProvider coloring) {
		setExperiment(coloring.getExperiment());
		setMode(coloring.getColoringMode());
		fireEvent();
	}
}
