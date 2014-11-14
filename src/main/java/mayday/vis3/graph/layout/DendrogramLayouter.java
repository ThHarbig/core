package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.Alignment;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class DendrogramLayouter extends CanvasLayouterPlugin
{
	private static final String horizontal ="horizontal";
	private static final String vertical ="vertical";
	
	private IntSetting spacer=new IntSetting("Spacer", "width of white space between components", 200);
	private RestrictedStringSetting alignment=new RestrictedStringSetting("Alignment", "The graph can be laid out vertically or horizontally",
			0, new String[]{horizontal,vertical});
//	private Alignment alignment=Alignment.HORIZONTALLY;
	private IntSetting offset=new IntSetting("Offset", "left offset", 100);
	
	public DendrogramLayouter()
	{
		initSetting();
	}
	
	public DendrogramLayouter(Alignment algmt)
	{
		initSetting();
		switch (algmt) {
		case HORIZONTALLY: alignment.setStringValue(horizontal);			
			break;
		case VERTICALLY: alignment.setStringValue(vertical);			
		break;
		}
		
	}
	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(spacer).addSetting(offset);		
	}

	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		Node root=Graphs.findRoot(g);

		if(alignment.getStringValue().equals(horizontal))
			placeHorizontally(g, root, (Node)null, 0, bounds.height, offset.getIntValue(), model);
		else
		{
			place(g, root, (Node)null, 0, bounds.width, offset.getIntValue(), model);
		}
		
		
	}

	private void place(Graph g, Node n, Node parent, int offset, int w, int h, GraphModel model)
	{
//		System.out.println(parent+"-->"+n);
		CanvasComponent cc=model.getComponent(n);
		cc.setLocation(offset+w/2, h);

		int cCount=g.getNeighbors(n).size()-1; // subtract one for partent
		if(cCount==0) 
			cCount++; // remove parent if necessary
		int newWidth=w/cCount;

		int i=0;
		for(Node c:g.getNeighbors(n))
		{
			if(c.equals(parent)) continue;			
			place(g,c,n,offset+i*newWidth,newWidth,h+spacer.getIntValue(),model);
			++i;
		}
	}

	private void placeHorizontally(Graph g, Node n, Node parent, int offset, int height, int width, GraphModel model)
	{
//		System.out.println(parent+"-->"+n);
		CanvasComponent cc=model.getComponent(n);
		cc.setLocation(width, offset+height/2);

		int cCount=g.getNeighbors(n).size()-1; // subtract one for partent
		if(cCount==0) 
			cCount++; // remove parent if necessary
		int newHeight=height/cCount;

		int i=0;
		for(Node c:g.getNeighbors(n))
		{
			if(c.equals(parent)) continue;			
			placeHorizontally(g,c,n,offset+i*newHeight,newHeight,width+spacer.getIntValue(),model);
			++i;
		}
	}

	/**
	 * @return the spacer
	 */
	public int getSpacer() {
		return spacer.getIntValue();
	}

	/**
	 * @param spacer the spacer to set
	 */
	public void setSpacer(int spacer) {
		this.spacer.setIntValue(spacer);
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset.getIntValue();
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset.setIntValue(offset);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Dendrogram",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Dendrogram Layout as descrioed in Karp 1996",
				"Dendrogram"				
		);
		return pli;	
	}
	
	




}
