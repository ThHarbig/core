package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class ProfilePlotRenderer extends DefaultComponentRenderer
{

	private SuperColorProvider colorProvider;
	private Font font;

	public ProfilePlotRenderer()
	{
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	}
	
	public ProfilePlotRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	}

	public void render(Iterable<Probe> probes, String label, Rectangle bounds,
			boolean selected, Graphics2D g) 
	{	
		g.setColor(Color.white);
		g.fillRect(bounds.x, bounds.y,bounds.width, bounds.height);
		
		try {
			double min=colorProvider.minimum();
			double max=colorProvider.maximum();
			double range=max-min;	
			//paint
			int imageHeight=(int) bounds.getHeight();
			int imageWidth=(int) bounds.getWidth();
			for(Probe probe:probes)
			{
				g.setColor(colorProvider.getColor(probe));
				int numExp=probe.getNumberOfExperiments();
				int lastx=bounds.x;
				int lasty=Math.round( (float) (imageHeight-( (colorProvider.getProbeValue(probe, 0)-min)/range)*imageHeight) );

				for(int j=1; j!=numExp; ++j)
				{
					int x= j==numExp-1?imageWidth:(int)Math.round((double)(j*imageWidth) / (double)(numExp-1));
					x+=bounds.x;
					double yp= imageHeight-( (colorProvider.getProbeValue(probe, j)-min)/range)*imageHeight;
					int y=Math.round( (float)yp);
					g.drawLine(lastx, lasty, x, y);
					lastx=x;
					lasty=y;
				}			
			}			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}		
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);
	}

	public Dimension getSuggestedSize() 
	{
		return new Dimension(80,50);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{		
		g.setColor(Color.white);
		g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());

		try {
			//determine range:			
			double min=Double.MAX_VALUE;
			double max=Double.MIN_NORMAL;						

			for(double d: value)
			{
				if( d > max) max=d;
				if( d < min) min=d;
			}		

			double range=max-min;	
			//paint
			int imageHeight=(int) bounds.getHeight();
			int imageWidth=(int) bounds.getWidth();
			
			int lastx=0;
			int lasty=Math.round( (float) (imageHeight-( (value[0]*imageHeight) )));
			int numExp = value.length;
			for(int j=1; j!=numExp; ++j)
			{
				int x= j==numExp-1?imageWidth:(int)Math.round((double)(j*imageWidth) / (double)(numExp-1));
				double yp= imageHeight-( (value[j]-min)/range)*imageHeight;
				int y=Math.round( (float)yp);
				g.drawLine(lastx, lasty, x, y);
				lastx=x;
				lasty=y;
			}			

		} catch (Exception e) 
		{
			// die silently
		}

		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);		
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{
		List<Probe> pl=new ArrayList<Probe>();
		pl.add(value);
		render(pl,label,bounds,selected,g);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbes(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, java.lang.Iterable)
	 */
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> value) 
	{
		render(value,label,bounds,selected,g);
	}

	public SuperColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Profile",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Parallel coordinates",
				"Profile Plot"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
	
	@Override
	public String getRendererStatus() 
	{
		return "color: "+colorProvider.getSourceName();
	}
}