package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.plots.profilelogo.renderer.ArrowProfileLogoRenderer;

public class FoldChangeRenderer extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;
	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,20);
	private NumberFormat format=NumberFormat.getNumberInstance();
	
	private ClassSelectionSetting classPartition=new ClassSelectionSetting("Class Partition", null, new ClassSelectionModel(1,1), 1, 2);
	private ColorGradientSetting fcGradient=new ColorGradientSetting("Fold Change Gradient", null, ColorGradient.createDefaultGradient(-16, +16 ));
	
	private boolean classPartitionSet=false;
	
	public FoldChangeRenderer() 
	{	
 		format.setMaximumFractionDigits(0);
	}
	
	public FoldChangeRenderer(SuperColorProvider coloring)
	{
		colorProvider=coloring;	
		format.setMaximumFractionDigits(0);
	}
	
	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{		
		super.drawDouble(g, bounds, label, selected, value);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{
		super.drawProbe(g, bounds, label, selected, value);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbes(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, java.lang.Iterable)
	 */
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> probes) 
	{
		g.setFont(font);
		g.setColor(Color.white);
		g.fillRect(0, 0, bounds.width, bounds.height);
		if(!classPartitionSet || classPartition.getModel().getNumClasses()==1)
		{
			drawProbesRange(g, bounds, label, selected, probes);
		}else
		{
			drawProbesModel(g, bounds, label, selected, probes);
		}
		RendererTools.drawBox(g, bounds, selected);
	}
	
	private void drawProbesModel(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> probes)
	{
		List<Double> vals = colorProvider.getProbesMeanValue(probes);
		
		int numClass1=0;
		int numClass2=0;
		double class1=0;
		double class2=0;
		
		ClassSelectionModel model=classPartition.getModel();
		if(model.getClassesLabels().isEmpty())
		{
			RendererTools.drawBox(g, bounds, selected);
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
			g.drawString("Please ", 5, 15);
			g.drawString("configure ", 5, 30);
			g.drawString("renderer", 5, 45);
			return;
		}
		String cl1=model.getClassesLabels().get(0);
		String cl2=model.getClassesLabels().get(1);
		for(int i=0; i!=model.getNumObjects(); ++i)
		{
			if(model.getObjectClass(i).equals(model.getNoClassLabel()) )
				continue;
			if(model.getObjectClass(i).equals(cl1))
			{
				numClass1++;
				class1+=vals.get(i);
			}
			if(model.getObjectClass(i).equals(cl2))
			{
				numClass2++;
				class2+=vals.get(i);
			}
		}
		class1=class1/(1.0*numClass1);
		class2=class2/(1.0*numClass2);
		double fc=class2 -class1;
		g.setColor(colorProvider.getColor(class1));
		
		int pos=bounds.height/2+10;
		Path2D arrow= new ArrowProfileLogoRenderer().getStableArrow(new Rectangle2D.Double(bounds.getWidth()/2-bounds.getHeight()/4,bounds.getHeight()/4,bounds.getHeight()/2,bounds.getHeight()/2));
		if(fc < -1 )
		{
			arrow= new ArrowProfileLogoRenderer().getDownArrow(new Rectangle2D.Double(bounds.getWidth()/2-bounds.getHeight()/4,bounds.getHeight()/4,bounds.getHeight()/2,bounds.getHeight()/2));
			pos=24;
		}
		if(fc > 1)
		{
			arrow= new ArrowProfileLogoRenderer().getUpArrow(new Rectangle2D.Double(bounds.getWidth()/2-bounds.getHeight()/4,bounds.getHeight()/4,bounds.getHeight()/2,bounds.getHeight()/2));
			pos=bounds.height-10;
		}
		String numStr=format.format(class1);
		if(numStr.length() < 2)
			g.drawString(numStr, 15, pos);
		else
			g.drawString(numStr, 1, pos);
		
		g.setColor(fcGradient.getColorGradient().mapValueToColor(fc));
		g.fill(arrow);
		
		g.setColor(colorProvider.getColor(class2));
		
		if(fc < -1 )
		{
			pos=bounds.height-10;
		}
		if(fc > 1)
		{
			pos=24;
		}
		numStr=format.format(class2);
		if(numStr.length() < 2)
			g.drawString(numStr, (int) arrow.getBounds().getMaxX()+1, pos);
		else
			g.drawString(numStr, (int) arrow.getBounds().getMaxX(), pos);
		
	}
	
	private void drawProbesRange(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> probes) 
	{
		double min=Double.MAX_VALUE;
		double max=Double.MIN_NORMAL;
		int minIdx=0;
		int maxIdx=0;
		List<Double> vals = colorProvider.getProbesMeanValue(probes);
		for(int i=0; i!=vals.size(); ++i )
		{
			if(vals.get(i) < min)
			{
				min=vals.get(i);
				minIdx=i;
			}
			if(vals.get(i) > max)
			{
				max=vals.get(i);
				maxIdx=i;
			}
		}
		double fc=0;
		if(minIdx < maxIdx)
		{
			fc=max-min;
			g.setColor(colorProvider.getColor(min));
			
			int pos=(int)bounds.getHeight()-10;
			String numStr=format.format(min);			
			if(numStr.length() < 2)
				g.drawString(numStr, 10, pos);
			else
				g.drawString(numStr, 1, pos);
			
//			g.drawString(format.format(min), 1, (int)bounds.getHeight()-10);
			
			g.setColor(fcGradient.getColorGradient().mapValueToColor(fc));
			Path2D arrow= new ArrowProfileLogoRenderer().getUpArrow(new Rectangle2D.Double(bounds.getWidth()/2-bounds.getHeight()/4,bounds.getHeight()/4,bounds.getHeight()/2,bounds.getHeight()/2));
			g.fill(arrow);
			
			g.setColor(colorProvider.getColor(max));
			pos=24;
			numStr=format.format(max);			
			if(numStr.length() < 2)
				g.drawString(numStr, (int) arrow.getBounds().getMaxX(), pos);
			else
				g.drawString(numStr, (int) arrow.getBounds().getMaxX(), pos);
			
//			g.drawString(format.format(max), (int) arrow.getBounds().getMaxX(), 24);
			
		}else
		{
			fc=min-max;
			g.setColor(colorProvider.getColor(max));
//			g.drawString(format.format(max), 1, 24);
			
			int pos=24;
			String numStr=format.format(max);			
			if(numStr.length() < 2)
				g.drawString(numStr, 10, pos);
			else
				g.drawString(numStr, 1, pos);
			
			g.setColor(fcGradient.getColorGradient().mapValueToColor(fc));
			Path2D arrow= new ArrowProfileLogoRenderer().getDownArrow(new Rectangle2D.Double(bounds.getWidth()/2-bounds.getHeight()/4,bounds.getHeight()/4,bounds.getHeight()/2,bounds.getHeight()/2));
			g.fill(arrow);
			
			g.setColor(colorProvider.getColor(min));
			
			 pos= (int)bounds.getHeight()-10;
			 numStr=format.format(min);			
			if(numStr.length() < 2)
				g.drawString(numStr, (int) arrow.getBounds().getMaxX(), pos);
			else
				g.drawString(numStr, (int) arrow.getBounds().getMaxX(), pos);
			
//			g.drawString(format.format(min), (int) arrow.getBounds().getMaxX(), (int)bounds.getHeight()-10);
		}
		
	}
	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(100,60);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.FoldChange",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render the values by displaying the maximum FoldChange",
				"Fold Change Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

	public SuperColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		if(colorProvider==null)
			return; 
		this.colorProvider = colorProvider;
		if(!classPartitionSet)
		{
			classPartition.setModel(new ClassSelectionModel(colorProvider.getViewModel().getDataSet().getMasterTable()));	
			classPartitionSet=true;
		}
		

	}
	
	@Override
	public Setting getSetting() 
	{
		HierarchicalSetting setting=new HierarchicalSetting("Fold Change Renderer Setting");
		setting.addSetting(classPartition);
		setting.addSetting(fcGradient);
		return setting;
//		return null;
	}
	
	@Override
	public String getRendererStatus() 
	{
		return "color: "+colorProvider.getSourceName();
	}
	
	
}
