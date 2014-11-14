package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;


public class DefaultComponentRenderer extends PrimaryComponentRenderer
{
	protected Color foregroundColor;
	protected Color backgroundColor;
	protected Color selectedColor;
	protected Dimension suggestedSize;
	protected Font font;
	
	private static DefaultComponentRenderer sharedInstance;
	
	public DefaultComponentRenderer()
	{
		foregroundColor=Color.black;
		backgroundColor=Color.white;
		selectedColor=Color.red;
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);		
		suggestedSize= new Dimension(80,50);
	}

	@Override
	public void drawString(Graphics2D g, Rectangle bounds, boolean selected, String value) 
	{
		g.setBackground(Color.white);
		g.clearRect(0, 0, bounds.width, bounds.height);
		g.setFont(font);
		int h=g.getFontMetrics().getHeight();
		int w=g.getFontMetrics().stringWidth(value);
		g.drawString(value, (bounds.width - w)/2,bounds.height-((bounds.height-h/2)/2));		
		RendererTools.drawBox(g, bounds, selected);
	}
	
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{
		drawString(g, bounds, selected, value.getDisplayName());		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,	boolean selected, Iterable<Probe> value) 
	{
		// try and cast to Collection:
		if(value instanceof Collection)
		{
			drawString(g, bounds, selected,  (((Collection)value).size()+" Probes"));
			return;
		}
		drawString(g, bounds, selected, "Multiple Probes");		
	}
	
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value) 
	{
		drawString(g, bounds, selected, Arrays.toString(value));		
	}
	
	@Override
	public void drawObject(Graphics2D g, Rectangle bounds, String label,boolean selected, Object value) 
	{
		if(value ==null) return;
		drawString(g, bounds, selected, value.toString());
		
	}
	
	public static DefaultComponentRenderer getDefaultRenderer()
	{
		if(sharedInstance==null) sharedInstance=new DefaultComponentRenderer();
		return sharedInstance;
	}

	/**
	 * @return the foregroundColor
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the selectedColor
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}

	/**
	 * @param selectedColor the selectedColor to set
	 */
	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}
	
	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return suggestedSize;
	}

	public Dimension getSuggestedSize() 
	{
		return suggestedSize;
	}

	/**
	 * @param suggestedSize the suggestedSize to set
	 */
	public void setSuggestedSize(Dimension suggestedSize) {
		this.suggestedSize = suggestedSize;
	}

	@Override
	public void drawNode(Graphics2D g, Rectangle bounds, String label,	boolean selected, Node node) 
	{		
		drawString(g, bounds, selected, node.getName());
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Default",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Default Renderer",
				"Default Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
	
	@Override
	public void setColorProvider(SuperColorProvider coloring) {
		// empty		
	}
	
	
	
	@Override
	public String getRendererStatus() 
	{
		return "";
	}
}
