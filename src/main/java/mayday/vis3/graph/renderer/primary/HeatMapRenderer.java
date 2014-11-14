package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class HeatMapRenderer extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;
	private Font font;
	
	public HeatMapRenderer() 
	{
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	}
	
	public HeatMapRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	}
	
	public void render(Iterable<Probe> probes, String label, Rectangle bounds,
			boolean selected, Graphics2D g) 
	{
		List<Color> colors;
		g.setColor(Color.white);
		g.fillRect(bounds.x, bounds.x,bounds.width, bounds.height);
		
		if(probes.iterator().hasNext())
		{
			try {
				int pc=0;
				for(@SuppressWarnings("unused") Probe p:probes)
					pc++;
				
				colors = colorProvider.getColors(probes.iterator().next());
				
//				int dx=(int) (bounds.getWidth() / colors.size());
				double dy= (bounds.getHeight() / pc);
				int j=0;
				for(Probe p:probes)
				{					
					colors = colorProvider.getColors(p);
					Rectangle2D r=new Rectangle2D.Double(bounds.x, j*dy,bounds.width,dy);
					RendererTools.drawColorLine(g, colors, r);
					j++;
				}			
			} catch (Exception e) 
			{
				g.setColor(Color.white);
				g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());
			}
		}
		
		g.setColor(Color.black);
//		g.drawRect(0, 0, (int)bounds.getWidth()-1, (int)bounds.getHeight()-1);	
		g.setFont(font);		
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{		
		ChromogramRenderer.getDefaultRenderer().drawDouble(g, bounds, label, selected, value);
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
	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(80,50);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Heatmap",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render Probes using a heatmap",
				"Heat Map Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

	public SuperColorProvider getColorProvider() 
	{
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		this.colorProvider = colorProvider;
	}
	
	@Override
	public String getRendererStatus() 
	{
		return "color: "+colorProvider.getSourceName();
	}
}
