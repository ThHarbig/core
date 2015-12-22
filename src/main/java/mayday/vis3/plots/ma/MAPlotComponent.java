package mayday.vis3.plots.ma;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.scatter.ScatterPlotComponent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class MAPlotComponent extends ScatterPlotComponent {

	protected BooleanSetting isLogged = new BooleanSetting(
			"Apply logarithm",
			"If data is not logarithmic, it has to be transformed for the MA plot",
			false);
	
	// these two shadow the valueproviders in ScatterPlotComponent
	protected MAValueProvider X;
	protected MAValueProvider Y;

	
	public MAPlotComponent() {
		super();
	}
	
	protected static final double DIVLOG2 = 1.0/Math.log(2);
	
	protected double log2(double x) {
		return Math.log(x) * DIVLOG2;
	}
	
	@Override
	public DataSeries viewProbes(Collection<Probe> probes) {
		DataSeries ds = new DataSeries();
		boolean log = isLogged.getBooleanValue();
		if (X!=null && Y!=null) {
			for (Probe pb : probes) {
				double xx = X.getValue(pb);
				double yy = Y.getValue(pb);
				double M,A;
				if (log) {
					xx = log2(xx);
					yy = log2(yy);
				}
				M = xx-yy;
				A = (xx+yy)/2.0;				
				ds.addPoint(A, M, pb);
				szb.setObject(A,M,pb);
			}
		}
		return ds;
	}
	@Override
	public String getPreferredTitle() {
		return "MA Plot";
	}
	
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		plotContainer.addViewSetting(isLogged, this);
		isLogged.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				updatePlot();				
			}			
		});
	}
	
	@Override
	protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {
		Set<Probe> newSelection = new HashSet<Probe>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		boolean log = isLogged.getBooleanValue();
		for (Probe pb : viewModel.getProbes()) {
			double xx = X.getValue(pb);
			double yy = Y.getValue(pb);
			double M,A;
			if (log) {
				xx = log2(xx);
				yy = log2(yy);
			}
			M = xx-yy;
			A = (xx+yy)/2.0;			
			boolean inX = (A>clicked1[0] && A<clicked2[0]);
			boolean inY = (M<clicked1[1] && M>clicked2[1]);
			if (inX && inY)
				newSelection.add(pb);
		}
		/*
		 * selection modes: 
		 * - no modifier: replace
		 * - ctrl: union with previous selection
		 * - alt: intersect with previous selection
		 * - ctrl+alt: remove from previous selection
		 */
		
		Set<Probe> previousSelection = viewModel.getSelectedProbes();
		if (control && alt) {
			previousSelection = new HashSet<Probe>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (control) {
			newSelection.addAll(previousSelection);
		} else if (alt) {
			newSelection.retainAll(previousSelection);
		} else {
			// nothing to do with prev selection
		}
		viewModel.setProbeSelection(newSelection);
	}
	
	protected void initValueProviders(ViewModel vm, PlotContainer plotContainer) {
		
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updatePlot();
			}
		};	
		
		X = new MAValueProvider(viewModel,"First Experiment");
		Y = new MAValueProvider(viewModel,"Second Experiment");
		plotContainer.addViewSetting(X.getSetting(), this);
		plotContainer.addViewSetting(Y.getSetting(), this);
		X.addChangeListener(cl);
		Y.addChangeListener(cl);
		Y.setProvider(Y.new ExperimentProvider(1));

	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if (X!=null && Y!=null)
			return  "M ("+X.getSourceName()+"-"+Y.getSourceName()+")";
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if (X!=null && Y!=null)
			return "A (("+X.getSourceName()+"+"+Y.getSourceName()+")/2)";
		return xtitle;
	}

}
