package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.heatmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
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

public class TrackRenderer_hm extends AbstractTrackRenderer {

	protected LogixVizColorProvider cp = null;
	protected Set<Probe> set = null;
	protected HeatmapTrackSettings ts = null;
	protected Rectangle2D.Double r2d = null;
	protected int exp = -1;

	protected int coloringmode;
	protected Strand strand;
	protected SplitView split; 
	
	double maxTransparency=0.;
    double minTransparency=0.;
    
	public TrackRenderer_hm(GenomeOverviewModel Model, AbstractTrackPlugin track) {
		super(Model,track);

		cp = tp.getTrackSettings().getColorProvider();
		ts = (HeatmapTrackSettings)tp.getTrackSettings();

		exp = cp.getExperiment();
	}

	public void updateInternalVariables(){
		exp = cp.getExperiment();
		coloringmode = tp.getTrackSettings().getColorProvider().getColoringMode();
		strand = tp.getTrackSettings().getStrand();
		split = ((HeatmapTrackSettings)tp.getTrackSettings()).getRepresentation(); 
		setInformationTransparency();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;
		Color transpColor;
		if(coloringmode == ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE){
			r2d = new Rectangle2D.Double(0, 0, 1.0,height);
			double[] vals = new double[]{Double.NaN, Double.NaN};
			if(split!=null){
				switch(ts.getStrand()){
				case PLUS:
					for (int i = beg_x; i != end_x; ++i) {
						getExpAndTranspValues(i, vals);
						transpColor = ProviderConnector.getTransparencyColorForExperiment(g2D, vals[0], vals[1],cp,minTransparency, 
								maxTransparency, ts.useTransparency(), ts.invertTransparency());
						drawBox(g2D,transpColor,i);
					}
					break;
				case MINUS:
					for (int i = beg_x; i != end_x; ++i) {
						getExpAndTranspValues(i, vals);
						transpColor = ProviderConnector.getTransparencyColorForExperiment(g2D, vals[0], vals[1],cp,minTransparency, 
								maxTransparency,ts.useTransparency(), ts.invertTransparency());
						drawBox(g2D,transpColor,i);
					}
					break;
				default:
					break;
				}
			}
		} else{
			for (int i = beg_x; i != end_x; ++i) {
				r2d = new Rectangle2D.Double(0, 0, 1.0,height);
				getProbesAtPosition(i);
				if(!set.isEmpty()){
					if(coloringmode == ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST){
						drawPBList(g2D, r2d, i);
					} else if(coloringmode == ColorProviderSetting.COLOR_BY_MIO_VALUE){
						drawMIO(g2D, r2d, i);
					}
				}
			}
		}
	}
	
	private void drawBox(Graphics2D g2D, Color transpCol, int i) {
		if(transpCol!=null){
			g2D.setColor(transpCol);
			r2d.x = i * 1;
			g2D.fill(r2d);
		}
	}

	private void getExpAndTranspValues(double mouseX, double[] expVals) {
		DataMapper.getBpOfView(width,chromeModel,mouseX,ftp);
		if(ftp.isValid()){
			ProviderConnector.getValues_exp_transp(ftp.getFrom(),ftp.getTo(),split, cp.getExperiment(), 
					ts.getTransparencyProvider(), expVals, chromeModel, strand);
		}
	}

	private void drawMIO(Graphics2D g2D, Rectangle2D.Double r2d, int i) {

		TreeMap<Integer,Color> colorSet = cp.getSortedColorList(set);
		ArrayList<Color> colArray = new ArrayList<Color>();
		for(int oneOcc: colorSet.keySet()){
			colArray.add(colorSet.get(oneOcc));
		}

		if (!colArray.isEmpty()) {
			int colorCount = colArray.size();
			double oneColorHeight = ((double) chromeModel.getHeight_trackpanel()) / ((double) colorCount);

			r2d.x = i;
			r2d.height = (Math.max(oneColorHeight, 1.0));

			for (int j = 0; j != colorCount; ++j) {
				g2D.setColor(colArray.get(j));
				r2d.y = j * oneColorHeight;
				g2D.fill(r2d);
			}
		}
	}

	private void drawPBList(Graphics2D g2D, Rectangle2D.Double r2d, int i) {

		HeatmapTrackSettings hts = (HeatmapTrackSettings)ts;
		switch(hts.getProbelistColoring()){
		case COLOR_HIGHEST_PROBELIST:
			Color color = tp.getTrackSettings().getColorProvider().getHighestColorOccurence(set);
			Color transparencyColor = color;
			if (ts.useTransparency()) transparencyColor = ProviderConnector.getTransparentColor(set, color,transparencyColor,minTransparency,
					maxTransparency, ts.getTransparencyProvider(), split, ts.invertTransparency());
			drawBox(g2D,transparencyColor,i);
			break;
		case COLOR_ALL_PROBELISTS:
			TreeMap<Integer,Color> colorMap = cp.getSortedColorList(set);
			int alpha = ProviderConnector.getAlpha(set, minTransparency, maxTransparency, split, ts.getTransparencyProvider());
		
			Color transpCol;
			int numbOcc = 0;
			for(int oneOcc: colorMap.keySet()){
				numbOcc +=  oneOcc;
			}
			
			if (!set.isEmpty()) {

				double oneColorHeight = ((double) height) / ((double) numbOcc);
				r2d.x = i;
				int v = 0;
				Color col;
				for (int oneOcc: colorMap.keySet()) {
					col=colorMap.get(oneOcc);
					transpCol=col;
					if(ts.useTransparency()){
						if(alpha>=0)transpCol = ProviderConnector.getTransparentColor(alpha, col, ts.invertTransparency());
					}
					
					double val = (Math.max(oneColorHeight*oneOcc, 1.0));
					r2d.height = val;
					g2D.setColor(transpCol);
					r2d.y = v;
					g2D.fill(r2d);
					v += val;
				}
			}
			break;
		}
	}
	
	private void getProbesAtPosition(double mouseX) {
		set = Collections.emptySet();
		ftp.clear();
		if (strand==null)
			return;
		DataMapper.getBpOfView(width,chromeModel,mouseX,ftp);
		if(ftp.isValid()){	
			if(ftp.getFrom() == ftp.getTo()){
				if(strand.equals(Strand.PLUS)){
					set = chromeModel.getAllForwardProbes(ftp.getFrom());
				} else if(strand.equals(Strand.MINUS)){
					set = chromeModel.getAllBackwardProbes(ftp.getFrom()); 
				}
			} else {
				if(strand.equals(Strand.PLUS)){
					set = chromeModel.getAllForwardProbes(ftp.getFrom(), ftp.getTo());
				} else if(strand.equals(Strand.MINUS)){
					set = chromeModel.getAllBackwardProbes(ftp.getFrom(),ftp.getTo()); 
				}
			}
		}
	}

	private void setInformationTransparency(){
		minTransparency=ts.getTransparencyProvider().getMinimum();
		maxTransparency=ts.getTransparencyProvider().getMaximum();
	}
}
