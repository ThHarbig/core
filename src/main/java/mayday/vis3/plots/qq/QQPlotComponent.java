package mayday.vis3.plots.qq;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.scatter.AbstractProbeListScatterPlotComponent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class QQPlotComponent extends AbstractProbeListScatterPlotComponent {

	protected ValueProvider X;
	protected ValueProvider Y;
	protected BooleanSetting trendAdjusted;
	
	protected double min= Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;

	protected Rectangle selRect;

	public QQPlotComponent() {
		trendAdjusted = new BooleanSetting("Trend adjusted", "Plot (x_i, x_i-y_i) instead of (x_i, y_i)", false); 
	}

	@Override
	public DataSeries viewProbes(Collection<Probe> probes) {
		DataSeries ds = new DataSeries();
		boolean ta = trendAdjusted.getBooleanValue();
		if (X!=null && Y!=null) {
			double[] xv = new double[probes.size()];
			double[] yv = new double[probes.size()];
			int i=0;
			for (Probe pb : probes) {
				double xx = X.getValue(pb);
				double yy = Y.getValue(pb);
				xv[i] = xx;
				yv[i] = yy;
				min = Math.min(min, Math.min(xx, yy));
				max = Math.max(max, Math.max(xx, yy));
				++i;
			}
			Arrays.sort(xv);
			Arrays.sort(yv);
			for (i=0; i!=xv.length; ++i) {
				ds.addPoint(xv[i], ta?(xv[i]-yv[i]):yv[i], null);
			}
		}
		return ds;
	}

	@Override
	public int getNumberOfComponents() {
		return viewModel.getProbeLists(true).size();
	}

	@Override
	public DataSeries getPlotComponent(int i) {
		int index = getNumberOfComponents()-i-1;
		ProbeList pl = viewModel.getProbeLists(true).get(index);
		DataSeries res = viewProbes(pl.getAllProbes());
		res.setAfterJumpModifier(probeColorSetter);
		return res;
	}

	@Override
	public String getPreferredTitle() {
		return "Quantile-Quantile Plot";
	}

	public void setup(PlotContainer plotContainer) {
		if (firstTime) {
			setXLabeling(null);
			setYLabeling(null);
		}
		super.setup(plotContainer);
		plotContainer.addViewSetting(trendAdjusted, this);
		trendAdjusted.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				updatePlot();
			}
		});
		initValueProviders(viewModel, plotContainer);
	}

	protected void initValueProviders(ViewModel vm, PlotContainer plotContainer) {

		if (X==null || Y==null) {
			ChangeListener cl = new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					updatePlot();
				}
			};	

			X = new ValueProvider(viewModel,"X axis");
			Y = new ValueProvider(viewModel,"Y axis");
			X.addChangeListener(cl);
			Y.addChangeListener(cl);
			if (vm.getDataSet().getMasterTable().getNumberOfExperiments()>1)
				Y.setProvider(Y.new ExperimentProvider(1));
		}
		plotContainer.addViewSetting(X.getSetting(), this);
		plotContainer.addViewSetting(Y.getSetting(), this);
	}

	@Override
	public DataSeries doSelect(Collection<Probe> probes) {
		DataSeries ds = new DataSeries();
		ds.setConnected(true);
		if (trendAdjusted.getBooleanValue()) {			
			ds.addPoint(min,0,null);
			ds.addPoint(max,0,null);
		} else {
			ds.addPoint(min, min, null);
			ds.addPoint(max, max, null);
		}
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
		return ds;
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if (Y!=null)
			return  Y.getSourceName();
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if (X!=null)
			return  X.getSourceName();
		return xtitle;
	}
	
}
