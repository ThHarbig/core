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
import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class GradientGeneRenderer extends DefaultComponentRenderer
{
	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	private SuperColorProvider colorProvider;
	
	public GradientGeneRenderer() 
	{	
	}
	
	public GradientGeneRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;
	}	

	public void render(Iterable<Probe> probes, String label, Rectangle bounds, boolean selected, Graphics2D g) 
	{
		Color c=Color.white;
		if(probes.iterator().hasNext())
		{
			c=colorProvider.getMeanColors(probes).get(colorProvider.getExperiment());
		}				 
		g.setColor(c);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
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
	public void drawDouble(Graphics2D g, Rectangle bounds, String label,
			boolean selected, double... value)
	{		
		Color c=Color.white;
		c=colorProvider.getColor(Statistics.mean(value));						 
		g.setColor(c);
		g.fillRect(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight());
		g.setFont(font);		
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
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Gradient",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render Probes using a color gradient",
				"Gradient Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

	public SuperColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}
	
	
}
