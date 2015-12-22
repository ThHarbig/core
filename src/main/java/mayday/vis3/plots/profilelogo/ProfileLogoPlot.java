package mayday.vis3.plots.profilelogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.math.binning.Binning;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.typed.IntListSetting;
import mayday.vis3.ColorProvider;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.ColorGradient.MIDPOINT_MODE;
import mayday.vis3.gradient.agents.Agent_Tricolore;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.profilelogo.renderer.BinningRendererTool;
import mayday.vis3.plots.profilelogo.renderer.ProfileLogoRenderer;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;

@SuppressWarnings("serial")
public class ProfileLogoPlot extends ChartComponent implements ViewModelListener, ProbeListListener
{

	private ProfileLogoData data;
	private ProfileLogoRenderer[] renderer;
	final private Binning binning = new Binning();
	private ColorProvider coloring;
	private ColorGradientSetting colorGradientSetting; 
	private ColorGradient gradient; 
	private BooleanHierarchicalSetting useCutPoints;
	private IntListSetting cutPoints;

	protected DataSeries selectionLayer;

	public ProfileLogoPlot()
	{
		setGrid(0.0, 0.2);
		setGridEmphasize(0.0, 1.0);
		binning.strategy.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				updatePlot();
			}
		});
		gradient=new ColorGradient(0, 1, 2, true, 3, MIDPOINT_MODE.Center, new Agent_Tricolore(false, Color.green, Color.black, Color.red, 1.0));
	}

	public Binning getBinning() {
		return binning;
	}

	public ColorGradient getGradient() {
		return gradient;
	}

	public void createView() 
	{
		executeBinning();
		for(int i=0; i!= data.getExperimentCount(); ++i)
		{
			double hu=0;

			for(int j=0; j!= data.getBinCount(); ++j)
			{	
				DataSeries ds=new DataSeries();
				double ph= data.at(j,i);///(ProfileLogo.log2(data.getBinCount()) );
				ds.setShape(
						new Vis3ProxyShape(
								renderer[j].renderToShape(
										new Rectangle2D.Double(0, 0,0.7, ph)
								),renderer[j].getColor()));


				ds.addPoint(i-.35, hu, null);
				hu+=ph;
				addDataSeries(ds);				
			}
		}
		DataSeries beauty = new DataSeries();
		beauty.addPoint(0, 0, null);
		beauty.addPoint(data.getExperimentCount(), ProfileLogo.log2(data.getBinCount()), null);
		//		beauty.setConnected(true);		
		beauty.setColor(Color.white);
		addDataSeries(beauty);

		if(useCutPoints.getBooleanValue()) {
			DataSeries beast=new DataSeries();
			
			List<Integer> cutoffs=cutPoints.getIntegerListValue();
			for(int i: cutoffs){
				beast.addDPoint(i-0.5, 0);
				beast.addDPoint(i-0.5, ProfileLogo.log2(data.getBinCount()));
				beast.setConnected(true);		
				beast.setColor(Color.black);
			}
			addDataSeries(beast);
		}
		useExperimentNamesAsXLabels();
		setScalingUnitX(1.0);
	}

	private class Vis3ProxyShape extends Shape
	{
		private java.awt.Shape shape;
		private Color color;
		public Vis3ProxyShape(java.awt.Shape renderToShape, Color color) 
		{
			this.shape=renderToShape;
			this.color=color;
		}

		public void paint(Graphics2D g)
		{
			g.setColor(color);
			g.fill(shape);
		}
	}

	public void setup(PlotContainer plotContainer) 
	{
		super.setup(plotContainer);
		//		plotContainer.getMenu(PlotContainer.VIEW_MENU, this).add(new JMenuItem(new SettingsAction()));
		viewModel.addViewModelListener(this);
		coloring = new ColorProvider(viewModel);

		colorGradientSetting=new ColorGradientSetting("Bin Coloring", null, gradient);

		//		binning.setParameters(new BinningParameters(3));
		//		binning.getParameters().setThresholds(estimateThresholds());
		//		binning.setStrategy(new ThresholdBinning());

		plotContainer.addViewSetting(binning.strategy, this);	
		plotContainer.addViewSetting(colorGradientSetting, this);
		useCutPoints=new BooleanHierarchicalSetting("Show cut Points", null, false);
		cutPoints=new IntListSetting("Cut points", null, new ArrayList<Integer>());
		useCutPoints.addSetting(cutPoints);
		plotContainer.addViewSetting(useCutPoints, this);

		plotContainer.setPreferredTitle("Profile Logo", this);
		updatePlot();
	}

	/**
	 * Execute the binning with the given parameters and strategy. 
	 */
	private void executeBinning()
	{
		int[][] num=binning.getStrategy().execute(viewModel);
		double[][] height=ProfileLogo.produceLogo(num);
		int nob = binning.getNumberOfBins(viewModel);
		data=new ProfileLogoData(height,ProfileLogo.log2(nob));
		gradient.setMax(nob);
		renderer=BinningRendererTool.suggestRenderer(nob, gradient);
	}

	/**
	 * Produces an estimate of thresholds. 
	 * @return A list of 2 thresholds, i.e. mean + / - stdev. 
	 */


	public void viewModelChanged(ViewModelEvent vme) 
	{		
		updatePlot();
	}

	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE:
			repaint();
			break;
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		}
	}

	public void removeNotify() {
		super.removeNotify();
		if (coloring!=null)
			coloring.removeNotify();
		viewModel.removeViewModelListener(this);	
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		return "Bits";
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "Experiment";
	}

}
