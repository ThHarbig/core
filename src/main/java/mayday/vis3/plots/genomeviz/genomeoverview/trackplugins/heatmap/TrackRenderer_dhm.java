package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.heatmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.Probe;
import mayday.genetics.basic.Strand;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.LogixVizColorProvider;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ProviderConnector;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;

public class TrackRenderer_dhm extends AbstractTrackRenderer{
	
	
	protected LogixVizColorProvider cp = null;
	protected Set<Probe> list_p = null;
	protected Set<Probe> list_m = null;
	protected HeatmapTrackSettings ts = null;
	protected Rectangle2D.Double r2d = null;
	protected double y_p = 0.;
	protected double y_m = 0.;
	protected double height_p = 0.;
	protected double height_m = 0.;
	protected SplitView split = null;
	protected int exp = -1;
	protected double[] expvals = null;
	double maxTransparency=0.;
    double minTransparency=0.;
	
	
	protected int coloringmode;
	protected Strand strand;

	public TrackRenderer_dhm(GenomeOverviewModel Model, AbstractTrackPlugin TrackPlugin) {
		super(Model,TrackPlugin);

		cp = tp.getTrackSettings().getColorProvider();
		ts = (HeatmapTrackSettings)tp.getTrackSettings();
		split = ts.getRepresentation();
		exp = cp.getExperiment();
	
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;
		SplitView split = ts.getRepresentation();
		int exp = cp.getExperiment();
		y_p = 0.;
		y_m = Math.ceil((double)height/2.);
		height_p = Math.floor((double)height/2.);
		height_m = Math.floor((double)height/2.);
		drawBothStrands(width, height, g2D, split, exp);
	}
	
	private void drawBothStrands(int width, int height, Graphics2D g2D,
			SplitView split, int exp) {
		
		expvals = new double[]{Double.NaN, Double.NaN, Double.NaN , Double.NaN};
		coloringmode = ts.getColorProvider().getColoringMode();
		Color transpColor;
		if(coloringmode == ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE){
			for (int i = beg_x; i != end_x; ++i) {
				getBothValues_exp_transp(i, expvals);
				createForwardRectangle();
				transpColor=ProviderConnector.getTransparencyColorForExperiment(g2D,expvals[0],expvals[2],cp,minTransparency, 
						maxTransparency,ts.useTransparency(), ts.invertTransparency());
				drawBox(g2D,transpColor,i);
				
				createBackwardRectangle();
				transpColor=ProviderConnector.getTransparencyColorForExperiment(g2D,expvals[1],expvals[3],cp,minTransparency, 
						maxTransparency,ts.useTransparency(), ts.invertTransparency());
				drawBox(g2D,transpColor,i);
			}
		}else if(coloringmode == ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST){
			for (int i = beg_x; i != end_x; ++i) {
				fillBothListsWithProbes(i);
				
				if(!list_p.isEmpty()){
					createForwardRectangle();
					drawPBList(g2D, i, true);
				}
				
				if(!list_m.isEmpty()){
					createBackwardRectangle();
					drawPBList(g2D, i, false);
				}
			}
			
		} else if(coloringmode == ColorProviderSetting.COLOR_BY_MIO_VALUE){
			for (int i = beg_x; i != end_x; ++i) {
				fillBothListsWithProbes(i);
				
				if(!list_p.isEmpty()){
					createForwardRectangle();
					drawMIO(g2D, i, list_p);
				}
				
				if(!list_m.isEmpty()){
					createBackwardRectangle();
					drawMIO(g2D, i, list_m);
				}
			}			
		}
		drawZeroLine(width, height, g2D);
	}
	
	private void drawBox(Graphics2D g2D, Color color, int i) {
		if(color!=null){
			g2D.setColor(color);
			r2d.x = i * 1;
			g2D.fill(r2d);
		}
	}
	
	private void createBackwardRectangle() {
		if (r2d==null)
			r2d = new Rectangle2D.Double(0,y_m, 1.0,height_m);
		else
			r2d.setFrame(0,y_m,1.0,height_m);
	}

	private void createForwardRectangle() {
		if (r2d==null)
			r2d = new Rectangle2D.Double(0,y_p, 1.0,height_p);
		else
			r2d.setFrame(0,y_p,1.0,height_p);
	}

	private void drawZeroLine(int width, int height, Graphics2D g2D) {
		final Line2D.Double zeroLine = new Line2D.Double();
		g2D.setColor(Color.DARK_GRAY);
		zeroLine.setLine(beg_x, Math.floor(height/2.), end_x,Math.floor(height/2.));
		g2D.draw(zeroLine);
	}
	
	private void drawMIO(Graphics2D g2D, int i, Set<Probe> list) {
	
		for(Probe pb: list){
			cp.getValue(pb);
		}
		Color col = cp.getHighestColorOccurence(list);

			
			Color transparencyColor = col;
			
			if (ts.useTransparency()) transparencyColor = ProviderConnector.getTransparentColor(list, col,transparencyColor,minTransparency,
					maxTransparency, ts.getTransparencyProvider(), split, ts.invertTransparency());
			
			r2d.x = i;
			g2D.setColor(transparencyColor);
			g2D.fill(r2d);
	}
	
	private void drawPBList(Graphics2D g2D, int i,
			boolean forward) {
		switch (ts.getProbelistColoring()) {
		case COLOR_HIGHEST_PROBELIST:
			Color colorValue = null;

			if (forward) {
				colorValue = ts.getColorProvider().getHighestColorOccurence(list_p);
			} else {
				colorValue = ts.getColorProvider().getHighestColorOccurence(list_m);
			}
			Color transparencyColor = colorValue;
			
			if (ts.useTransparency()) transparencyColor = getTransparentColor(forward, colorValue,transparencyColor);

			drawBox(g2D,transparencyColor,i);
			break;
		case COLOR_ALL_PROBELISTS:
			
			int numbOcc=0;
			if (forward) {
				int alpha = ProviderConnector.getAlpha(list_p, minTransparency, maxTransparency, split, ts.getTransparencyProvider());
				TreeMap<Integer,Color> colorMap = cp.getSortedColorList(list_p);
				if (!colorMap.isEmpty()) {
					for(Integer oc: colorMap.keySet())numbOcc+=oc;
					double v = 0;
					paintingByProbelist(g2D, i, alpha, colorMap, numbOcc, v);
				}
			} else {
				int alpha = ProviderConnector.getAlpha(list_m, minTransparency, maxTransparency, split, ts.getTransparencyProvider());
				TreeMap<Integer,Color> colorMap = cp.getSortedColorList(list_m);
				for(Integer oc: colorMap.keySet())numbOcc+=oc;
				if (!colorMap.isEmpty()) {
					double v = (int) (y_m);
					paintingByProbelist(g2D, i, alpha, colorMap, numbOcc, v);
				}
			}
			break;
		}
	}

	private void paintingByProbelist(Graphics2D g2D, int i, int alpha,
			TreeMap<Integer, Color> colorMap, int numbOcc, double v) {
		Color transpCol;
		double oneOccHeight = ((double) r2d.height)/ ((double)numbOcc);
		r2d.x = i;
		Color col;
		for (int nO : colorMap.keySet()) {
			col=colorMap.get(nO);
			transpCol = col;
			
			double val = (Math.max(oneOccHeight*nO, 1.0));
			r2d.height = val;
			
			if (ts.useTransparency()) {
				if (ts.invertTransparency())
					alpha = 255-alpha;
		
				if(alpha<=255 && alpha >=0){
					transpCol= new Color(col.getRed(), col.getGreen(), col.getBlue(),alpha);
				} else {
					System.err.println("StemTrackRenderer: alpha out of bounds. Alpha is: " + alpha + " transparencyValue is: " + alpha); ;
				}
			}
			g2D.setColor(transpCol);
			r2d.y = v;
			g2D.fill(r2d);
			v += val;
		}
	}

	private Color getTransparentColor(boolean forward, Color color,
			Color transparencyColor) {
		if (forward) {
			return ProviderConnector.getTransparentColor(list_p, color,transparencyColor,minTransparency,
					maxTransparency, ts.getTransparencyProvider(), split, ts.invertTransparency());
		} else {
			return ProviderConnector.getTransparentColor(list_m, color,transparencyColor,minTransparency,
					maxTransparency, ts.getTransparencyProvider(), split, ts.invertTransparency());
		}
	}
	
//	private int getAlpha(boolean forward) {
//		double transparencyValue = Double.NaN;
//		if (forward) {
//			transparencyValue = ProviderConnector.getTransparencyValue(
//					list_p, ts.getTransparencyProvider(), split);
//		} else {
//			transparencyValue = ProviderConnector.getTransparencyValue(
//					list_m, ts.getTransparencyProvider(), split);
//		}
//		if (transparencyValue != Double.NaN) {
//			int alpha = (int) Mapper.mapValue(minTransparency,
//					maxTransparency, transparencyValue, 0, 255);
//			return alpha;
//		}
//		return -1;
//	}
	
	
	private void fillBothListsWithProbes(double mouseX) {
		list_p = new HashSet<Probe>();
		list_m = new HashSet<Probe>();
		DataMapper.getBpOfView(this.width,chromeModel,mouseX,ftp);
		if(ftp.isValid()){
			list_p = chromeModel.getAllForwardProbes(ftp.getFrom(),ftp.getTo());
			list_m = chromeModel.getAllBackwardProbes(ftp.getFrom(),ftp.getTo()); 
		}
	}
	
	private void getBothValues_exp_transp(double mouseX, double[] expVals) {
		DataMapper.getBpOfView(width,chromeModel,mouseX,ftp);
		if(ftp.isValid()){
			ProviderConnector.getBothValues_exp_transp(ftp.getFrom(),ftp.getTo(),split,ts.getColorProvider().getExperiment(), ts.getTransparencyProvider(), expVals, chromeModel);
		}
	}

	public void updateInternalVariables(){
		exp = cp.getExperiment();
		coloringmode = tp.getTrackSettings().getColorProvider().getColoringMode();
		strand = tp.getTrackSettings().getStrand();
		split = ((HeatmapTrackSettings)tp.getTrackSettings()).getRepresentation();
		setInformationTransparency();
	}
	
	private void setInformationTransparency(){
		minTransparency=ts.getTransparencyProvider().getMinimum();
		maxTransparency=ts.getTransparencyProvider().getMaximum();
	}
}
