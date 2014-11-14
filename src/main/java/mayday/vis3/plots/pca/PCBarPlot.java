package mayday.vis3.plots.pca;

import java.awt.Color;
import java.util.Collection;

import mayday.core.Probe;
import mayday.vis3.plots.bars.AbstractBarPlotComponent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class PCBarPlot extends AbstractBarPlotComponent {

	double ev[];
	
	public PCBarPlot(double[] EigenValues) {
		ev = EigenValues;
		getZoomController().setActive(false);
	}
	
	@Override
	public DataSeries doSelect(Collection<Probe> probes) {
		return new DataSeries(); // never select anything
	}

	@Override
	public BarShape getBar(int i) {
		return new BarShape(.5,ev[i],Color.blue);
	}

	@Override
	public int getNumberOfBars() {
		return Math.min(ev.length,10);
	}

	@Override
	public String getPreferredTitle() {
		return "PC Eigenvalues";
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		return "Percent Eigenvalue";
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "PC";
	}
}
