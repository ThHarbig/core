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

public class ChromogramRenderer extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;
	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	
	public ChromogramRenderer() 
	{
	
	}
	
	public ChromogramRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;		
	}
	
	public void render(Iterable<Probe> probes, String label, Rectangle bounds, boolean selected, Graphics2D g) 
	{
		List<Color> colors;
		g.setColor(Color.white);
		g.fillRect(bounds.x, bounds.y,bounds.width, bounds.height);	
		
		if(probes.iterator().hasNext())
		{
			try {
				colors = colorProvider.getMeanColors(probes);
				RendererTools.drawColorLine(g,colors,bounds);
//				double dx=(bounds.getWidth() / colors.size());
//				int w=(int)Math.round(dx);
//				int lastx=0;
//				for(int i=0; i!= colors.size(); ++i)
//				{
//					g.setColor(colors.get(i));	
//					w=i%2==0?(int)Math.floor(dx):(int)Math.ceil(dx);
//					g.fillRect(lastx, 0, w, (int)bounds.getHeight());
//					lastx+=w;
//				}			
			} catch (Exception e) 
			{
				e.printStackTrace();
				g.setColor(Color.white);
				g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());
			}
		}else
		{
			g.setColor(Color.white);
			g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());
		}

		g.setFont(font);	
		g.setColor(Color.white);
		RendererTools.drawLabel(g, bounds, label);
		RendererTools.drawBox(g, bounds, selected);
	}
	
	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{		
		List<Color> colors=colorProvider.getColors(value);
		RendererTools.drawColorLine(g,colors,bounds);
//		double dx=(bounds.getWidth() / colors.size());
//		int w=(int)Math.round(dx);
//		int lastx=0;
//		for(int i=0; i!= colors.size(); ++i)
//		{
//			g.setColor(colors.get(i));	
//			w=i%2==0?(int)Math.floor(dx):(int)Math.ceil(dx);
//			g.fillRect(lastx, 0, w, (int)bounds.getHeight());
//			lastx+=w;
//		}
		g.setFont(font);	
		g.setColor(Color.white);

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
	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(80,50);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Chromogram",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"One-dimensional heatmap",
				"Heat Stream Renderer"				
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
	
	@Override
	public String getRendererStatus() 
	{
		return "color: "+colorProvider.getSourceName();
	}
	
}
