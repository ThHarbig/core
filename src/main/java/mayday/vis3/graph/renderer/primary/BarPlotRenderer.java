package mayday.vis3.graph.renderer.primary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
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

public class BarPlotRenderer extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;
	private Font font;
	
	private SummaryRenderingSetting summaryRenderingSetting=new SummaryRenderingSetting();
	private HierarchicalSetting setting=new HierarchicalSetting("Bar Plot Setting");
	private BooleanSetting indicateZero=new BooleanSetting("Indicate Zero", null, true);
	private DoubleSetting barWidth=new DoubleSetting("Bar width", "The fraction of space consumed by the bar", 0.7,0.1,1.0,true,true);
	private BooleanHierarchicalSetting useColorSetting=new BooleanHierarchicalSetting("Bar Color",null,false);
	private ColorSetting barColor=new ColorSetting("Bar Color", null, Color.blue);
	
	private ProbeSummary summary=new ProbeSummary(summaryRenderingSetting);
	
	public BarPlotRenderer() 
	{
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
		useColorSetting.addSetting(barColor);
		setting.addSetting(summaryRenderingSetting).addSetting(useColorSetting).addSetting(indicateZero).addSetting(barWidth);
	}
	
	public BarPlotRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
		useColorSetting.addSetting(barColor);
		setting.addSetting(summaryRenderingSetting).addSetting(useColorSetting).addSetting(indicateZero).addSetting(barWidth);		
	}
	

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{	
		// clear the screen;
		g.setColor(Color.white);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);		
		// get range
		double min=colorProvider.minimum();
		if(min > 0)
			min=0;
		double max=colorProvider.maximum();
		if(max < 0)
			max=0;

		drawBars(g, barColor.getColorValue(), bounds, barWidth.getDoubleValue(), min, max, value);
		double range=max-min;	
//		//paint
		int imageHeight=bounds.height;
		int imageWidth=bounds.width;
		double zeroP= imageHeight-( (0-min)/range)*imageHeight;
		int zero=Math.round( (float)zeroP);
		if(indicateZero.getBooleanValue())
		{
			g.drawLine(bounds.x, zero, imageWidth, zero);
		}
		// draw frame, label, etc. 
		g.setColor(Color.black);
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);
	}
	
	public static void drawBars(Graphics2D g, Color c, Rectangle bounds, double barPerc, double min, double max, double... values)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(values.length*1.0);
		double range=Math.abs(max-min);
		double zeroP= bounds.height-( (0-min)/range)*bounds.height;
		double space=(1-barPerc)/2.0;
		g.scale(sx, range );
		g.setStroke(new BasicStroke(0));
		g.setColor(c);
		for(int i=0; i!= values.length; ++i)
		{
			double yp=(values[i]-min)/(max-min) * bounds.getMaxY();
			yp=bounds.getMaxY()-yp;
			Rectangle2D r=new Rectangle2D.Double(i+space, yp<zeroP?yp:zeroP, barPerc, yp<zeroP?zeroP:yp);
			g.fill(r);			
		}
		g.setTransform(tBak);
	}
	
	public static void drawBars(Graphics2D g, List<Color> c, Rectangle bounds, double barPerc, double min, double max, double... values)
	{
		AffineTransform tBak=g.getTransform();
		g.translate(bounds.x, bounds.y);
		double sx=bounds.getWidth()/(values.length*1.0);
		double range=Math.abs(max-min);
		double zeroP= bounds.height-( (0-min)/range)*bounds.height;
		double space=(1-barPerc)/2.0;
		
		g.scale(sx, 1 );
		g.setStroke(new BasicStroke(0));
		
		for(int i=0; i!= values.length; ++i)
		{
//			double yp=max/values[i];
//			double yp=//(values[i]-vmin) / (vmax-vmin) * (min-max)+max;
			double yp=(values[i]-min)/(max-min) * bounds.getMaxY();
			yp=bounds.getMaxY()-yp;
			g.setColor(c.get(i));
			Rectangle2D r=new Rectangle2D.Double(i+space, yp<zeroP?yp:zeroP, barPerc, yp<zeroP?zeroP:yp);
			g.fill(r);			
		}
		g.setTransform(tBak);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{
		// clear the screen;
		g.setColor(Color.white);
		g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());		
		// get range
		double min=colorProvider.minimum();
		if(min > 0)
			min=0;
		double max=colorProvider.maximum();
		if(max < 0)
			max=0;

		if(useColorSetting.getBooleanValue())
			drawBars(g, barColor.getColorValue(), bounds, barWidth.getDoubleValue(), min, max, value.getValues());
		else
			drawBars(g, colorProvider.getColors(value), bounds, barWidth.getDoubleValue(), min, max, value.getValues());
		double range=max-min;	
//		//paint
		int imageHeight=(int) bounds.getHeight();
		int imageWidth=(int) bounds.getWidth();		
		double zeroP= imageHeight-( (0-min)/range)*imageHeight;
		int zero=Math.round( (float)zeroP);
		if(indicateZero.getBooleanValue())
		{
			g.drawLine(0, zero, imageWidth, zero);
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
		// clear the screen;
		g.setColor(Color.white);
		g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());		
		// get range
		double min=colorProvider.minimum();
		if(min > 0)
			min=0;
		double max=colorProvider.maximum();
		if(max < 0)
			max=0;

		if(useColorSetting.getBooleanValue())
			drawBars(g, barColor.getColorValue(), bounds, barWidth.getDoubleValue(), min, max, summary.summarize(value));
		else
			drawBars(g, colorProvider.getMeanColors(value), bounds, barWidth.getDoubleValue(), min, max, summary.summarize(value));
		double range=max-min;	
//		//paint
		int imageHeight=(int) bounds.getHeight();
		int imageWidth=(int) bounds.getWidth();		
		double zeroP= imageHeight-( (0-min)/range)*imageHeight;
		int zero=Math.round( (float)zeroP);
		if(indicateZero.getBooleanValue())
		{
			g.drawLine(0, zero, imageWidth, zero);
		}
		// draw frame, label, etc. 
		g.setColor(Color.black);
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);	
		
		
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
				"PAS.GraphViewer.Renderer.Barplot",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Draw expression values using bar plots",
				"Bar Plot Renderer"				
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
		return "bar: "+summaryRenderingSetting.getStringValue()+", color: "+colorProvider.getSourceName();
	}
}
