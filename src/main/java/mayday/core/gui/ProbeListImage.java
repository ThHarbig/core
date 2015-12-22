/**
 * Provides a nices graphical rendering of a probe list for mayday's main window
 */

package mayday.core.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.methods.ManipulationMethodSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.CollectionValueProvider;
import mayday.vis3.PlotPlugin;
import mayday.vis3.model.ProbeDataManipulator;
import mayday.vis3.model.manipulators.None;
import mayday.vis3.plots.histogram.Histogram;
import mayday.vis3.plots.profile.ProfilePlot;



public class ProbeListImage extends BufferedImage
{
	// Static members ===============================================================
	private static class Mode {
		public int x,y;		
		public Mode(int X, int Y) {
			x=X;y=Y;
		}
		public String toString() {
			return (x+" x "+y);
		}
	}
	
	public static final Mode[] MODES = new Mode[]{
		// quadratische modes
		new Mode(32,32),
		new Mode(64,64),
		new Mode(80,80),
		new Mode(100,100),
		// modes nach goldenem schnitt
		new Mode(50,32), //default
		new Mode(64,40),
		new Mode(80,50),
		new Mode(100,60)
	};
	
	public static final BooleanSetting useGraphics;
	private static final ObjectSelectionSetting<Mode> previewMode;
	private static final IntSetting maxProbes;
	private static final ManipulationMethodSetting manipMethod;
	public static final PluginTypeSetting<PlotPlugin> doubleclickplot;
	
	public static final Setting setting = new HierarchicalSetting("ProbeList display")
		.addSetting(useGraphics = new BooleanSetting("Use graphic previews",null,true))
		.addSetting(previewMode = new ObjectSelectionSetting<Mode>("Preview size",null,4,MODES))
		.addSetting(maxProbes = new IntSetting("Maximal number of probes to use",null,5000))
		.addSetting(manipMethod = new ManipulationMethodSetting("Preview data manipulation",null,new None()))
		.addSetting(doubleclickplot = new PluginTypeSetting<PlotPlugin>("Double-click opens",null,new ProfilePlot(), MaydayDefaults.Plugins.CATEGORY_PLOT)
	);
	
	public static Dimension getImageSize()
	{
		return new Dimension(previewMode.getObjectValue().x,previewMode.getObjectValue().y );
	}

	// Instance members ===============================================================
	
	protected Color backgroundColor;	
	protected ProbeList probeList;
	protected ProbeDataManipulator manip = new ProbeDataManipulator();

	public ProbeListImage(Color backgrColor) {
		this(null, backgrColor);
	}
	
	public ProbeListImage(ProbeList p,Color backgrColor) {
		super(previewMode.getObjectValue().x,previewMode.getObjectValue().y,BufferedImage.TYPE_USHORT_555_RGB);
		manip.setManipulation(manipMethod.getInstance());
		backgroundColor=backgrColor;
		probeList=p;
		draw();
	}
	
	public ProbeListImage(ProbeList p) {
		this(p,new Color(0xEE,0xEE,0xEE));
	}
	
	public ProbeListImage(List<Double> values, Color color, int w, int h) {
		super(w, h, BufferedImage.TYPE_USHORT_555_RGB);
		Graphics2D g=createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0,0,w,h);
		g.setColor(Color.black);
		g.drawRect(0, 0, w-1, h-1);
		g.setColor(color);
		
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
				
		for(double d:values) {
			if(d < min ) min=d;
			if(d > max ) max=d;			
		}
		double range=max-min;
		
		int numExp=values.size();
		int lastx=0;
		int lasty=Math.round( (float) (h-( (values.get(0)-min)/range)*h) );
		for(int j=1; j!=numExp; ++j)
		{
			int x= j==numExp-1?w:
				(int)Math.round(
					(double)(j*w) / (double)(numExp-1)
			);
			
			double yp= h-( (values.get(j)-min)/range)*h;
			int y=Math.round( (float)yp);			
			g.drawLine(lastx, lasty, x, y);
			lastx=x;
			lasty=y;
		}
		g.dispose();
	}
	
	private void draw()
	{
		Graphics2D g=createGraphics();
		g.setBackground(backgroundColor);	
		g.clearRect(0,0,getWidth(),getHeight());
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
				
		if (probeList==null || probeList.getNumberOfProbes()==0)  
			return;
		
		g.setColor(probeList.getColor());
		
		//for large probe lists, draw only some probes.
		
		double minD=manip.getMinimum(null, probeList.getAllProbes());
		double maxD=manip.getMaximum(null, probeList.getAllProbes());
		if(Double.isNaN(minD) || Double.isNaN(maxD)) {			
			Font f=g.getFont();
			Font f2=new Font(f.getName(),f.getStyle(),getHeight()-5);
			g.setFont(f2);
			g.drawString("NA", 5, getHeight()-5);			
			g.setFont(f);
			return;
		}
		
		double range=maxD-minD;

			
		Object[] probes = probeList.toArray();
		
		// end checks and balances. 
		int numExp= ((Probe)probes[0]).getNumberOfExperiments();
		int numPb = probes.length;

		int imageWidth = getWidth();
		int imageHeight = getHeight();
		
		if (numExp>1) {
			// draw a profile preview
			if(numPb > maxProbes.getIntValue()) {	
				Random random=new Random();
				for (int i=0; i!=maxProbes.getIntValue(); ++i) {
					Probe probe=(Probe)probes[random.nextInt(probes.length)];
					drawProbe(g, probe, numExp, minD, range, imageWidth, imageHeight);
				}
			} else {
				for (Object o : probes) {
					Probe probe=(Probe)o;
					drawProbe(g, probe, numExp, minD, range, imageWidth, imageHeight);				
				}
			}
		} else {
			// draw a histogram preview
			Collection<Double> vals = new ArrayList<Double>();
			if (numPb > maxProbes.getIntValue()) {
				Random random=new Random();
				for (int i=0; i!=maxProbes.getIntValue(); ++i) {
					Probe probe=(Probe)probes[random.nextInt(probes.length)];
					vals.add(probe.getValue(0));
				}
			} else {
				for (Object o : probes) {
					Probe probe=(Probe)o;
					vals.add(probe.getValue(0));	
				}
			}
			
			CollectionValueProvider cvp = new CollectionValueProvider();
			cvp.setValues(vals);
			Histogram h = new Histogram(cvp, 20, false);
			
			int nob = h.getNumberOfBins();
			
			double hmax=0;
			for (int i=0; i!=nob; ++i)
				hmax = Math.max(hmax, h.getBinFrequency(i));
			
			double scaledImageHeight = (double)imageHeight/hmax;
			
			for (int i=0; i!=nob; ++i) {				
				int xpos = (int)(((double)imageWidth/(double)nob)*i);
				int xpos2 = (int)(((double)imageWidth/(double)nob)*(i+1));
				int ypos = (int)((-h.getBinFrequency(i)*scaledImageHeight)+imageHeight);
				g.fillRect( xpos , ypos, xpos2-xpos, imageHeight-ypos);
			}
			
		}
		

		

		g.dispose();
	}

	private void drawProbe(Graphics2D g, Probe probe, int numExp, double min, double range, int imageWidth, int imageHeight) {

		double[] pdata = getProbeValues(probe);

		int lastx=0;
		int lasty=Math.round( (float) (imageHeight-( (pdata[0]-min)/range)*imageHeight) );


		for(int j=1; j!=numExp; ++j)
		{			
			int x= j==numExp-1?imageWidth:
				(int)Math.round(
						(double)(j*imageWidth) / (double)(numExp-1)
				);

			double yp= imageHeight-( ( pdata[j]-min)/range)*imageHeight;
			int y=Math.round( (float)yp);
			g.drawLine(lastx, lasty, x, y);
			lastx=x;
			lasty=y;
		}
	}
	
	private double[] getProbeValues(Probe p) {
		double ret[] = manip.getProbeValues(p);
		if (ret==p.getValues())
            ret = Arrays.copyOf(ret, ret.length);
		for (int i=0; i!=ret.length; ++i)
			if (Double.isNaN(ret[i]))
				ret[i]=0.0;
		return ret;
	}
	
	public Color getBackgroundColor() 
	{
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) 
	{
		this.backgroundColor = backgroundColor;
		draw();
	}

	public ProbeList getProbeList() {
		return probeList;
	}

	public void setProbeList(ProbeList probeList) {
		this.probeList = probeList;
		draw();
	}

	public static int fetchWidth() {
		return previewMode.getObjectValue().x;
	}
	
	public static int fetchHeight() {
		return previewMode.getObjectValue().y;
	}
		
}
