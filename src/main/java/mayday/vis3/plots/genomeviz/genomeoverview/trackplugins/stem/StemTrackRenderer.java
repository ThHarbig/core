package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.stem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.genetics.basic.Strand;
import mayday.vis3.ColorProvider;
import mayday.vis3.ValueProvider;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;

public class StemTrackRenderer extends AbstractTrackRenderer {

	protected PaintingPanel pps = null;
	
   
	double maxHeight = 0.;
    double minHeight = 0.;
    double maxTransparency=0.;
    double minTransparency=0.;
    double maxWidth = 0.;
    double minWidth = 0.;

    protected int exp = -1;
	protected int coloringmode;
	protected Strand strand;

	
	public StemTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin track, PaintingPanel paintingpanel) {
		super(Model,track);
		pps = paintingpanel;
	}

	private void setInformationHeight() {
		StemTrackSettings sts= (StemTrackSettings)tp.getTrackSettings();
		ValueProvider vp = sts.getHeightProvider();
		maxHeight = vp.getMaximum();
			//chromeModel.getAbsoluteMaxOfProbes(getExperiment(), strand);
        minHeight = vp.getMinimum();
        	//chromeModel.getAbsoluteMinOfProbes(getExperiment(), strand);

        if(maxHeight<0) maxHeight = 0;
        else if(minHeight >0) minHeight = 0;
        
        if(minHeight==0 && maxHeight==0) 
        {
        	maxHeight=1;
        	minHeight=-1;
        }
	}
	
	private void setInformationWidth(){
		maxWidth = width;
        minWidth = 0;
	}
	
	private void setInformationTransparency(){
		StemTrackSettings sts= (StemTrackSettings)tp.getTrackSettings();
		minTransparency=sts.getTransparencyProvider().getMinimum();
		maxTransparency=sts.getTransparencyProvider().getMaximum();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		final Graphics2D g2dmain = (Graphics2D) g;

		final Insets ins = pps.getInsets();

		/* transform view into codomain */		
		final AffineTransform myTransform = AffineTransform.getTranslateInstance(ins.left, ins.top);
		myTransform.scale(1, -(height-1) / Math.abs(maxHeight - minHeight));
		myTransform.translate(minWidth, -maxHeight);
		 
		g2dmain.transform(myTransform);

		/* assign a 1px stroke */
		g2dmain.setStroke(new BasicStroke(1f / width, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));

		final Rectangle rect = pps.getVisibleRect();
//		drawMinMaxLine(g2dmain,myTransform, width, height);
		drawPlot(g2dmain, myTransform, rect);

		drawZeroLine(g2dmain,myTransform, width, height);
		
	}
	
	
	
	private void drawPlot(Graphics2D g2dmain, AffineTransform myTransform, Rectangle rect) {
		
		final double[] src = { 0., 0., width, 0. };
		final double[] dst = new double[4];
		myTransform.transform(src, 0, dst, 0, 2);
		double transparencyValue = 0;
		Double heightValue =0.;
		Color colorValue=null;
		Rectangle2D.Double r2d = null;
		StemTrackSettings sts= (StemTrackSettings)tp.getTrackSettings();
		ValueProvider heightProvider = null;
		ValueProvider transparencyProvider = null;
		ColorProvider colorProvider = null;
		
		if(sts==null)
			return;
		
		heightProvider=sts.getHeightProvider();
		transparencyProvider=sts.getTransparencyProvider();
		colorProvider= sts.getColorProvider();
		
		if (heightProvider!=null&&transparencyProvider!=null&&colorProvider!=null) {
			for (int i = beg_x; i != end_x; ++i) {

				MultiTreeMap<Double, Probe> map = fillListWithProbes(i);

				if (!map.isEmpty()) {
					
					for (Double d : map.descendingKeySet()) {

						List<Probe> probes = map.get(d);

						double prevHeight_p = 0.;
						double prevHeight_n = 0.;

						if (!probes.isEmpty()) {
							for (Probe pb : probes) {
								heightValue = heightProvider.getValue(pb);
								transparencyValue= transparencyProvider.getValue(pb);
								colorValue = null;
								try {
									colorValue = colorProvider.getColor(pb);
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								
								if(colorValue!=null){
									Color transpCol = colorValue;
							
									if (sts.useTransparency()) {
										int alpha = (int)DataMapper.mapValue(minTransparency, maxTransparency, transparencyValue, 0, 255);
										
										if (sts.invertTransparency())
											alpha = 255-alpha;
										if(alpha<=255 && alpha >=0){
											transpCol = new Color(colorValue.getRed(), colorValue
													.getGreen(), colorValue.getBlue(), alpha);
										} else {
											System.err.println("StemTrackRenderer: alpha out of bounds. Alpha is: " + alpha + " transparencyValue is: " + transparencyValue); ;
										}
									}
									g2dmain.setColor(transpCol);
									
									if (!heightValue.isNaN()) {

										if (heightValue > 0.) {
											r2d = new Rectangle2D.Double(0, 0, 1.0,
													heightValue);
											if (prevHeight_p != r2d.height) {
												prevHeight_p = r2d.height;
												r2d.x = i * 1;
												g2dmain.fill(r2d);
											}

										} else if (heightValue < 0.) {

											r2d = new Rectangle2D.Double(0, heightValue,
													1.0, Math.abs(heightValue));
											if (prevHeight_n != r2d.height) {
												prevHeight_n = r2d.height;
												r2d.x = i * 1;
												g2dmain.fill(r2d);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else{
			System.out.println("StemTrackRenderer");
		}
	}
	
	private void drawZeroLine(final Graphics2D g2dmain, AffineTransform myTransform, int width, int height) {
		final Line2D.Double zeroLine = new Line2D.Double();
		
		final double[] src = { 0., 0., width, 0. };
		final double[] dst = new double[4];
		myTransform.transform(src, 0, dst, 0, 2);
		g2dmain.setColor(Color.DARK_GRAY);
		zeroLine.setLine(0., 0., width,0.);
		g2dmain.draw(zeroLine);
		
	}
	
	private MultiTreeMap<Double,Probe> fillListWithProbes(double mouseX) {
		MultiTreeMap<Double,Probe> set = new MultiTreeMap<Double,Probe>();
		DataMapper.getBpOfView(width, chromeModel, mouseX,ftp);
		if (ftp.isValid()) {
			if (ftp.getFrom() == ftp.getTo()) {
				if (strand.equals(Strand.PLUS)) {
					set = chromeModel.getAllForwardProbes_Abs(ftp.getFrom(), exp);
				} else if (strand.equals(Strand.MINUS)) {
					set = chromeModel.getAllBackwardProbes_Abs(ftp.getFrom(), exp);
				}
			} else {
				if (strand.equals(Strand.PLUS)) {
					set = chromeModel.getAllForwardProbes_Abs(ftp.getFrom(), ftp.getTo(), exp);
				} else if (strand.equals(Strand.MINUS)) {
					set = chromeModel.getAllBackwardProbes_Abs(ftp.getFrom(), ftp.getTo(), exp);
				}
			}
		}
		return set;
	}

	public void updateInternalVariables() {
		setInformationHeight();
		setInformationWidth();
		setInformationTransparency();
		exp = tp.getTrackSettings().getColorProvider().getExperiment();
		coloringmode = tp.getTrackSettings().getColorProvider().getColoringMode();
		strand = tp.getTrackSettings().getStrand(); 
	}
}
