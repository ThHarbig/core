package mayday.vis3.graph.renderer.primary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mayday.core.Probe;
import mayday.core.gui.GUIUtilities;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class PieChartRenderer extends  DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;

	public PieChartRenderer() 
	{
	
	}
	
	public PieChartRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;		
	}
	
	public void render(Iterable<Probe> probes, String label, Rectangle bounds, boolean selected, Graphics2D g) 
	{

	}
	
	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{	
		List<Double> fractions=new ArrayList<Double>();
		List<Color> colors=new ArrayList<Color>();
		
		if(value.length ==1)
		{
			if(value[0] >=0 && value[0] <=1)
			{
				fractions.add(value[0]);
				fractions.add(1-value[0]);
				colors.add(Color.blue);
				colors.add(Color.white);
			}
			drawPie(g, bounds, selected, fractions, colors);
			return;
		}
		
		double sum=0;
		for(int i=0; i!= value.length; ++i)
		{
			sum+=value[i];
		}
		Color[] c=GUIUtilities.rainbow(value.length, 0.8);
		for(int i=0; i!= value.length; ++i)
		{
			fractions.add(value[i]/sum);
			colors.add(c[i]);
		}		
		drawPie(g, bounds, selected, fractions, colorProvider.getColors(value));	
		
		
		
		
		
	}
	
	private void drawPie(Graphics2D g, Rectangle bounds, boolean Selected, List<Double> fractions,List<Color> colors)
	{
		double angle =-90;
		int wh=Math.min(bounds.width, bounds.height)-2;
		int x=(bounds.width-wh) /2;
		int y=(bounds.height-wh) /2;
		
		
		for(int i=0; i!= fractions.size(); ++i)
		{
			double a=fractions.get(i)* 360.0d; 
			Arc2D arc= new Arc2D.Double(x, y, wh-2, wh-2, angle, a, Arc2D.PIE);
			g.setColor(Color.black);
//			g.draw(arc);
			g.setColor(colors.get(i));
			g.fill(arc);
			
			angle+=a;
		}
				
		if(Selected)
		{
			g.setColor(Color.red);
		}else
		{
			g.setColor(Color.black);
		}
		g.setStroke(new BasicStroke(0.5f));
		g.drawOval(x, y, wh-2, wh-2);
	}
	

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{		
		double sum=0;
		for(int i=0; i!= value.getNumberOfExperiments(); ++i)
		{
			sum+=value.getValue(i);
		}
		List<Double> fractions=new ArrayList<Double>();
		for(int i=0; i!= value.getNumberOfExperiments(); ++i)
		{
			fractions.add(value.getValue(i)/sum);			
		}		
		drawPie(g, bounds, selected, fractions, colorProvider.getColors(value));		
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbes(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, java.lang.Iterable)
	 */
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> value) 
	{
		// check for single probe
		Iterator<Probe> iter= value.iterator();
		Probe pr=iter.next();// get 1st element
		if(!iter.hasNext())
		{
			drawProbe(g, bounds, label, selected, pr);
			return;
		}
		//
		double sum=0;
		for(Probe p: value)
		{
			sum+=colorProvider.getProbeValue(p, colorProvider.getExperiment());
		}
		List<Double> fractions=new ArrayList<Double>();
		List<Color> colors=new ArrayList<Color>();
		for(Probe p: value)
		{
			fractions.add(colorProvider.getProbeValue(p, colorProvider.getExperiment())/sum);
			colors.add(colorProvider.getColor(p));
		}
		drawPie(g, bounds, selected, fractions, colors);
	}
	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(60,60);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.PieChart",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Pie Chart",
				"Pie Chart"				
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
