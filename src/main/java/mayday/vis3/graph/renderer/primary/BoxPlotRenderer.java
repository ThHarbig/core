package mayday.vis3.graph.renderer.primary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class BoxPlotRenderer extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;
	private Font font;

	private HierarchicalSetting setting=new HierarchicalSetting("Box Plot Setting");
	private BooleanSetting indicateMean=new BooleanSetting("Indicate Mean", null, true);
	private BooleanSetting indicateExtremes=new BooleanSetting("Indicate Extremes", null, true);
	private DoubleSetting whiskerRule=new DoubleSetting(
			"IQB factor for whiskers", 
			"The factor x for whisker extension.\n Ususally x=1.5",
			1.5,1.0,99.0,true,true);
	private DoubleSetting boxWidth=new DoubleSetting("Box width", "The fraction of space consumed by the box", 0.7,0.1,1.0,true,true);
	private BooleanHierarchicalSetting useColorSetting=new BooleanHierarchicalSetting("Fill boxes",null,false);
	private ColorSetting boxColor=new ColorSetting("Bar Color", null, Color.white);


	public BoxPlotRenderer() 
	{
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
		useColorSetting.addSetting(boxColor);
		setting.addSetting(indicateMean)
		.addSetting(indicateExtremes)
		.addSetting(whiskerRule)
		.addSetting(useColorSetting)
		.addSetting(boxWidth);
	}

	public BoxPlotRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
		useColorSetting.addSetting(boxColor);
		setting.addSetting(indicateMean)
		.addSetting(indicateExtremes)
		.addSetting(whiskerRule)
		.addSetting(useColorSetting)
		.addSetting(boxWidth);
	}


	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{	
		List<BoxplotBox> boxes=new ArrayList<BoxplotBox>();
		boxes.add(buildBox(whiskerRule.getDoubleValue(), value));
		List<Color> c= new ArrayList<Color>();
		c.add(boxColor.getColorValue());
		drawBoxes(g, bounds, boxWidth.getDoubleValue(),c, boxes);		
		// draw frame, label, etc. 
		g.setColor(Color.black);
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);	
	}

	private BoxplotBox buildBox(double k, double... vals)
	{
		List<Double> values=new ArrayList<Double>();
		for(double d: vals)
		{
			values.add(d);
		}
		Collections.sort(values);
		BoxplotBox box=new BoxplotBox();
		box.min=values.get(0);
		box.median=Statistics.median(values);
		box.mean=Statistics.mean(values);
		box.q1=quantile(values, 4, 1);
		box.q3=quantile(values, 4, 3);
		box.max=values.get(values.size()-1);
		// lower whisker:
		double iqr=box.q3-box.q1;
		for(int o=values.size()/4; o>=0; --o)
		{
			if(values.get(o) > box.q1-k*iqr)
				box.lowerWhisker=values.get(o);
		}
		for(int o=3*(values.size()/4); o>=values.size(); ++o)
		{
			if(values.get(o) < box.q3+k*iqr)
				box.upperWhisker=values.get(o);
		}
		return box;
	}
	
	public List<BoxplotBox> buildBoxes(Iterable<Probe> probes, double k)
	{
		Probe firstProbe=probes.iterator().next();

		List<BoxplotBox> res= new ArrayList<BoxplotBox>();
		for(int i=0; i!=firstProbe.getNumberOfExperiments(); ++i)
		{
			List<Double> values=new ArrayList<Double>();
			for(Probe p:probes)
			{
				values.add(colorProvider.getProbeValue(p,i));
			}
			Collections.sort(values);
			BoxplotBox box=new BoxplotBox();
			box.min=values.get(0);
			box.median=Statistics.median(values);
			box.mean=Statistics.mean(values);
			box.max=values.get(values.size()-1);
			if(values.size()==1)
			{
				box.q1=values.get(0);
				box.q3=	values.get(0);
				box.lowerWhisker=values.get(0);
				box.upperWhisker=values.get(0);
			}else
			{
				box.q1=quantile(values, 4, 1);
				box.q3=quantile(values, 4, 3);

				// lower whisker:
				double iqr=box.q3-box.q1;
				for(int o=values.size()/4; o>=0; --o)
				{
					if(values.get(o) > box.q1-k*iqr)
						box.lowerWhisker=values.get(o);
				}
				for(int o=0; o!=values.size(); ++o)
				{
					if(values.get(o) < box.q3+k*iqr)
						box.upperWhisker=values.get(o);
				}
			}
			res.add(box);
		}
		return res;
	}
	
	/**
	 * Calculates the k-th q-quantile of a sorted list of values. If the values are not sorted, the result is undefined.
	 * @param x The sorted sample values
	 * @param q The quantile (i.e. 100 for percentiles, 4 for quartiles)
	 * @param k The number of the quantile
	 */
	private double quantile(List<Double> x, double q, double k)
	{
		if(k > q) throw new IllegalArgumentException("Can not calculate the "+k+"th quantile of "+q);		
		double p=x.size()*(k/q);
		int idx=(int)Math.round(p);
		return x.get(idx); 
	}

	public static class BoxplotBox
	{
		public double min;
		public double lowerWhisker;
		public double q1;
		public double mean;
		public double median;
		public double q3;
		public double upperWhisker;
		public double max;

	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{
		List<BoxplotBox> boxes=new ArrayList<BoxplotBox>();
		boxes.add(buildBox(whiskerRule.getDoubleValue(), value.getValues()));
		if(useColorSetting.getBooleanValue())
		{

			List<Color> c= new ArrayList<Color>();
			c.add(boxColor.getColorValue());
			drawBoxes(g, bounds, boxWidth.getDoubleValue(),c, boxes);
		}			
		else
		{
			List<Color> c= new ArrayList<Color>();
			c.add(colorProvider.getColor(value));
			drawBoxes(g, bounds, boxWidth.getDoubleValue(),c , boxes);
		}

		// draw frame, label, etc. 
		g.setColor(Color.black);
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);	
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbes(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, java.lang.Iterable)
	 */
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> value) 
	{
		//prepare data:
		List<BoxplotBox> boxes=buildBoxes(value, whiskerRule.getDoubleValue());


		if(useColorSetting.getBooleanValue())
		{
			List<Color> c= new ArrayList<Color>();
			for(int i=0; i!=boxes.size(); ++i)
				c.add(boxColor.getColorValue());
			drawBoxes(g, bounds, boxWidth.getDoubleValue(),c, boxes);
		}			
		else
			drawBoxes(g, bounds, boxWidth.getDoubleValue(), colorProvider.getMeanColors(value), boxes);

		// draw frame, label, etc. 
		g.setColor(Color.black);
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);	

	}

	public void drawBoxes(Graphics2D g, Rectangle bounds, double boxPerc, List<Color> c, List<BoxplotBox> boxes)
	{
		g.setBackground(Color.white);
		g.clearRect(0, 0, bounds.width, bounds.height);
		// get range and the works
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;

		for(BoxplotBox b:boxes)
		{
			if(b.min < min)
				min=b.min;
			if(b.max > max)
				max=b.max;
		}

		AffineTransform tBak=g.getTransform();
		g.translate(bounds.x, bounds.y);
		double sx=bounds.getWidth()/(boxes.size()*1.0);
		double range=Math.abs(max-min);
		double space=(1-boxPerc)/2.0;
		double h=bounds.height;
		g.scale(sx, 1 );
		Object antialias=g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setStroke(new BasicStroke(0f));

		for(int i=0; i!= boxes.size(); ++i)
		{
			BoxplotBox b=boxes.get(i);
			// draw box:			
			g.setColor(c.get(i));
			Rectangle2D r=new Rectangle2D.Double(
					i+space,
					h-((b.q3-min)/range)*h,
					boxPerc,
					(((b.q3-b.q1))/range)*h
					);
			g.draw(r);
			// draw median
			Line2D l=new Line2D.Double(i+space,h-((b.median-min)/range)*h ,i+space+boxPerc,h-((b.median-min)/range)*h);
			g.draw(l);
			
			l.setLine(i+space,h-((b.upperWhisker-min)/range)*h ,i+space+boxPerc,h-((b.upperWhisker-min)/range)*h );
			g.draw(l);
			// connecting line
			l.setLine(i+0.5, h-((b.upperWhisker-min)/range)*h, i+0.5, h-((b.q3-min)/range)*h);
			g.draw(l);
			
			l.setLine(i+space,h-((b.lowerWhisker-min)/range)*h ,i+space+boxPerc,h-((b.lowerWhisker-min)/range)*h );
			g.draw(l);
			// connecting line
			l.setLine(i+0.5, h-((b.lowerWhisker-min)/range)*h, i+0.5, h-((b.q1-min)/range)*h);
			g.draw(l);
			
			//draw mean if necessary
			double rad=0.2;
			if(indicateMean.getBooleanValue())
			{
				l.setLine(i+space+rad,h-((b.mean-min)/range)*h ,i+space+boxPerc-rad,h-((b.mean-min)/range)*h );
				g.draw(l);
			}
			if(indicateExtremes.getBooleanValue())
			{
				l.setLine(i+space+rad,h-((b.min-min)/range)*h ,i+space+boxPerc-rad,h-((b.min-min)/range)*h );
				g.draw(l);
				
				l.setLine(i+space+rad,h-((b.max-min)/range)*h ,i+space+boxPerc-rad,h-((b.max-min)/range)*h );
				g.draw(l);
			}
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias);
		g.setTransform(tBak);
		
	}


	public Dimension getSuggestedSize() 
	{
		return new Dimension(80,50);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Boxplot",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Summarize values using a Boxplot.",
				"Box Plot Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

	@Override
	public Setting getSetting() 
	{
		return setting;
	}

	public SuperColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}
	
	@Override
	public String getRendererStatus() 
	{
		return "box : "+ (indicateMean.getBooleanValue()?"+mean":"")
		+ (indicateExtremes.getBooleanValue()?"+minmax":"")
		+", color: "+colorProvider.getSourceName();
	}
}


