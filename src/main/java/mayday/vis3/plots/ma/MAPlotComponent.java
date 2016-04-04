package mayday.vis3.plots.ma;

import java.awt.*;
import java.util.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.scatter.ScatterPlotComponent;
import mayday.vis3.vis2base.DataSeries;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

@SuppressWarnings("serial")
public class MAPlotComponent extends ScatterPlotComponent {

	protected BooleanSetting isLogged = new BooleanSetting(
			"Apply logarithm",
			"If data is not logarithmic, it has to be transformed for the MA plot",
			false);

	protected BooleanSetting showRegression = new BooleanSetting(
			"Show Loess Regression",
			"Compute Loess Regression line and show it in the plot",
			true);

	protected BooleanSetting regressionHeuristic = new BooleanSetting(
			"Regress on Means of Bins",
			"Regress on means of bins of width (span A-values / N). " +
					"This increases the performance significantly.",
			true);
	private DataSeries regression;


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
		if (X!=null && Y!=null && probes.size() > 0) {
			// Remember M A values for regression
			// For performance improvement use pre allocated array size
			final double[] ms = new double[probes.size()];
			final double[] as = new double[probes.size()];
			// Collection does not allow direct element access
			// manually mentain index for insertion into the upper arrays
			int i = 0;
			for (Probe pb : probes) {
				// compute M & A value
				double xx = X.getValue(pb);
				double yy = Y.getValue(pb);
				double M, A;
				if (log) {
					xx = log2(xx);
					yy = log2(yy);
				}
				M = xx-yy;
				A = (xx+yy)/2.0;
				ds.addPoint(A, M, pb);
				szb.setObject(A,M,pb);
				// store result
				ms[i] = M;
				as[i] = A;
				// next element
				i += 1;
			}
			if (// we do not have the regression stored yet
					regression == null) {
				/*
				 side note: viewProbes is first called for the whole set, not
				 only for the selection. Therefore, the values in 'as' & 'ms'
				 are the ones needed for the regression.
				 */
				// Dummy regression object, to prevent multiple
				// regression computations on plot start
				regression = new DataSeries();

				AbstractTask compute = new AbstractTask("Loess Regression") {
					@Override
					protected void initialize() { }

					@Override
					protected void doWork() throws Exception {
						try {
							computeRegression(as, ms);
						} catch (NotFiniteNumberException e) {
							// in log case, NaN could happens which 'destroy' the regression
							// use a dummy then
							regression = new DataSeries();
						}
						updatePlot();
					}
				};
				compute.start();
			}

		}
		return ds;
	}

	@Override
	public void updatePlot() {
		super.updatePlot();
		// add regression if available and wanted
		if (regression != null && showRegression.getBooleanValue()) {
			addDataSeries(regression);
		}
	}

	private void computeRegression(double[] as, double[] ms) {
		// create a loess interpolator with R comparable values, because that's
		// what most users  probably expect.
		// Values taken from:
		// http://stackoverflow.com/questions/12704658/difference-between-r-loess-and-org-apache-commons-math-loessinterpolator
		LoessInterpolator loess = new LoessInterpolator(0.75, 2);
		// apache implementation requires the ordinate values to be sorted
		Integer[] order =  argsort(as);
		as = permutation(order, as);
		ms = permutation(order, ms);

		double minA = as[0];
		double maxA = as[as.length - 1];

		// The loess implementation requires a strict monotone increasing ordinate
		// series.
		// => Compute average for probes with same 'A' value
		// For performance improvement, values are binned (if choosen)

		// Inplace aggregate values. copyToIndex shows were next value will be stored
		int copyToIndex = 0;
		double lastA = as[0];
		// Helper for Computing the mean for a variable amount of values
		DescriptiveStatistics meanState = new DescriptiveStatistics();
		meanState.clear();
		meanState.addValue(ms[0]);
		// Deviation for binning
		double dev = (maxA - minA) / as.length;
		// Aggregate
		for (int i=1; i < as.length; i++) {
			if (	// binning criteria
					( regressionHeuristic.getBooleanValue() && as[i] - lastA <= dev) ||
					// aggregation due to identical value to prevent NotFiniteNumberException
					(!regressionHeuristic.getBooleanValue() && as[i] == lastA) ) {
				meanState.addValue(ms[i]);
			} else {
				// Store aggregation
				as[copyToIndex] = lastA;
				ms[copyToIndex] = meanState.getMean();
				// initiate next aggregation
				copyToIndex += 1;
				lastA = as[i];
				meanState.clear();
				meanState.addValue(ms[i]);
			}
		}
		// store last aggregation
		as[copyToIndex] = lastA;
		ms[copyToIndex] = meanState.getMean();
		// slice to truncate arrays
		int sliceLength = copyToIndex + 1;
		as = Arrays.copyOf(as, sliceLength);
		ms = Arrays.copyOf(ms, sliceLength);


		// regress
		PolynomialSplineFunction psl = loess.interpolate(as, ms);
		// Sample the function
		final int SAMPLES = 200;
		double step = (maxA - minA) / SAMPLES;
		regression = new DataSeries();
		for (int i=0; i < SAMPLES; i++) {
			double x = minA + i * step;
			double y = psl.value(x);
			regression.addPoint(x, y, null);
		}
		// Add as a line to plot
		regression.setConnected(true);
		regression.setColor(Color.RED);
	}

	/**
	 * Return a copy of values that is sorte by a given order of indices.
	 * @param indices
	 * @param values
     * @return
     */
	public static double[] permutation(Integer[] indices, double[] values) {
		assert indices.length == values.length;
		final int N = indices.length;
		double[] result = new double[N];
		for (int i=0; i < N; i++) {
			result[i] = values[indices[i]];
		}
		return result;
	}

	/**
	 * Return array of indices in which order the parameter would be sorted.
	 * @param arg
	 * @return
     */
	public static Integer[] argsort(final double[] arg) {
		final int N = arg.length;
		Integer[] indices = new Integer[N];
		for (int i=0; i < N; i++) {
			indices[i] = i;
		}
		// sort indices according to values
		Arrays.parallelSort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer index1, Integer index2) {
				return ((Double) arg[index1]).compareTo(arg[index2]);
			}
		});
		return indices;
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
				// force new computation of regression
				regression = null;
				updatePlot();				
			}			
		});
		// regression setting
		plotContainer.addViewSetting(showRegression, this);
		showRegression.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {
				updatePlot();
				repaint();
			}
		});
		plotContainer.addViewSetting(regressionHeuristic, this);
		regressionHeuristic.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {
				// force new computation of regression
				regression = null;
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
