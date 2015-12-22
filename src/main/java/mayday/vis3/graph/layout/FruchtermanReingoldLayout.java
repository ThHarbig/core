package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.model.GraphModel;

public class FruchtermanReingoldLayout extends CanvasLayouterPlugin
{
	private IntSetting maxIterations=new IntSetting("Max Iterations", null, 500);
	
	private double forceConstant;
	private double temp;
	private Graph graph;
	private static final double EPSILON = 0.000001D;
	private static final double ALPHA = 0.1;

	private boolean randomInit=true;
	
	private Rectangle2D bounds;
	private HashMap<Node,Parameters> params;
	
	public FruchtermanReingoldLayout()
	{
		initSetting();
	}
	
	public FruchtermanReingoldLayout(int numIterations)
	{
		this.maxIterations.setIntValue(numIterations);
		initSetting();
	}
	
	
	protected void initSetting()
	{
		setting.addSetting(maxIterations);
	}

	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Rectangle workingBounds=new Rectangle(bounds.width-50, bounds.height-50);
		graph=model.getGraph();
		if(!randomInit)
		{
			params=new HashMap<Node, Parameters>();
			Iterator<Node> nodeIter = graph.getNodesIterator();
			while(nodeIter.hasNext())
			{
				Node n = nodeIter.next();
				Parameters np = getParameters(n);
				np.loc[0] = model.getComponent(n).getX();
				np.loc[1] = model.getComponent(n).getY();
				np.bounds[0]=model.getComponent(n).getWidth();
				np.bounds[1]=model.getComponent(n).getHeight();				
			}
			
		}
		
		layoutGraph(model.getGraph(), workingBounds);
    	for(Node n: graph.getNodes())
    	{
    		Parameters np = getParameters(n);
    		model.getComponent(n).setLocation((int)np.loc[0],(int)np.loc[1]);    			
    	}
	}
	
	/**
	 * Wrapper class holding parameters used for each node in this layout.
	 */
	public static class Parameters implements Cloneable 
	{
		double[] loc = new double[2];
		double[] bounds=new double[2];
		double[] disp = new double[2];
	}
	
	public String toString()
	{
		return "Fruchterman-Reingold-Layout";
	}
	
	private  void layoutGraph(Graph g,Rectangle2D bounds)
	{
		this.bounds=bounds;
		graph=g;
		
		initialize();
		for(int i=0; i!= maxIterations.getIntValue(); ++i)
		{
//			System.out.println("FR iteration "+i);
			// Calculate repulsion
			for(Node n:graph.getNodes())
			{
				calcRepulsion(n);
			}

			// Calculate attraction
			for (Edge e:graph.getEdges()) 
			{
				calcAttraction(e);
			}

			for(Node n:graph.getNodes()) 
			{                
				calcPositions(n);
			}
			cool(i);
		}
//		finish();
	}

	private void initialize() 
	{
		temp = bounds.getWidth() / 10;
		forceConstant = 0.75 * Math.sqrt(bounds.getHeight()*bounds.getWidth()/graph.nodeCount());
		if(params==null)
			params=new HashMap<Node, Parameters>();
		// initialize node positions
		if(randomInit)
		{
			Iterator<Node> nodeIter = graph.getNodesIterator();
			Random rand = new Random(42); // get a deterministic layout result
			double scaleW = ALPHA*bounds.getWidth()/2;
			double scaleH = ALPHA*bounds.getHeight()/2;
			while(nodeIter.hasNext())
			{
				Node n = nodeIter.next();
				Parameters np = getParameters(n);
				np.loc[0] = bounds.getCenterX() + rand.nextDouble()*scaleW;
				np.loc[1] = bounds.getCenterY() + rand.nextDouble()*scaleH;
			}
		}
	}
	
	private void calcRepulsion(Node node) 
	{
		Parameters np = getParameters(node);
		np.disp[0] = 0.0; np.disp[1] = 0.0;

		for (Node node2:graph.getNodes()) 
		{            
			Parameters n2p = getParameters(node2);
			if (node != node2) 
			{
			
				double xDelta = np.loc[0] - n2p.loc[0];
				double yDelta = np.loc[1] - n2p.loc[1];
				double deltaLength = Math.max(EPSILON, Math.sqrt(xDelta*xDelta + yDelta*yDelta));
				double force = (forceConstant*forceConstant) / deltaLength;
				if (Double.isNaN(force)) 
				{
					System.err.println("Mathematical error...");
				}
				np.disp[0] += (xDelta/deltaLength)*force;
				np.disp[1] += (yDelta/deltaLength)*force;
			
//				System.out.println("C:"+ xDelta + "\t" + yDelta + "\t" + deltaLength + "\t" +force);
//				
//				
////				double deltaLength;
//				 xDelta= minDist(np.loc[0], np.bounds[0],n2p.loc[0],n2p.bounds[0]);
//				 yDelta= minDist(np.loc[1], np.bounds[1],n2p.loc[1],n2p.bounds[1]);
////
//				if(isOverlap(np.loc[0], np.bounds[0], n2p.loc[0],n2p.bounds[0]) &&
//				   isOverlap(np.loc[1], np.bounds[1], n2p.loc[1],n2p.bounds[1]) )
//				{
//					deltaLength=EPSILON;
//					System.out.println("==" + Arrays.toString(np.loc)+ Arrays.toString(np.bounds)+ Arrays.toString(n2p.loc)+ Arrays.toString(n2p.bounds));
//				}else
//				{
//					
//					deltaLength = Math.max(EPSILON, Math.sqrt(xDelta*xDelta + yDelta*yDelta));
//				}
//
//				force = (forceConstant*forceConstant) / deltaLength;
//				if (Double.isNaN(force)) 
//				{
//					System.err.println("Mathematical error...");
//				}
//				System.out.println("F:"+ xDelta + "\t" + yDelta + "\t" + deltaLength + "\t" +force);
//				np.disp[0] += (xDelta/deltaLength)*force;
//				np.disp[1] += (yDelta/deltaLength)*force;
			}
		}
	}
	
//	private double minDist(double xa, double wa, double xb, double wb)
//	{
//		// check for overlap:
////		if(xb >= xa && xb < (xa + wa) )
////			return 0;
////		if(xa >= xb && xa < (xb + wb) )
////			return 0;
//		// calculate distances:
//		if(xa <= xb)
//			return xb- (xa + wa);
//		else
//			return -1*(xa - (xb + wb)); 
//		
//	}
//	
//	private boolean isOverlap(double xa, double wa, double xb, double wb)
//	{
//		// check for overlap:
//		if(xb >= xa && xb < (xa + wa) )
//			return true;
//		if(xa >= xb && xa < (xb + wb) )
//			return true;
//		return false;
//	}

	private void calcAttraction(Edge e) 
	{
		Node n1 = e.getSource();
		Parameters n1p = getParameters(n1);
		Node n2 = e.getTarget();
		Parameters n2p = getParameters(n2);

		double xDelta = n1p.loc[0] - n2p.loc[0];
		double yDelta = n1p.loc[1] - n2p.loc[1];

		double deltaLength = Math.max(EPSILON, Math.sqrt(xDelta*xDelta + yDelta*yDelta));
		double force = (deltaLength*deltaLength) / forceConstant;

		if (Double.isNaN(force)) 
		{
			System.err.println("Mathematical error...");
		}

		double xDisp = (xDelta/deltaLength) * force;
		double yDisp = (yDelta/deltaLength) * force;

		n1p.disp[0] -= xDisp; n1p.disp[1] -= yDisp;
		n2p.disp[0] += xDisp; n2p.disp[1] += yDisp;
	}
	
	public void calcPositions(Node node) {
		Parameters np = getParameters(node);

		double deltaLength = Math.max(EPSILON, Math.sqrt(np.disp[0]*np.disp[0] + np.disp[1]*np.disp[1]));

		double xDisp = np.disp[0]/deltaLength * Math.min(deltaLength, temp);

		if (Double.isNaN(xDisp)) 
		{
			System.err.println("Mathematical error... (calcPositions:xDisp)");
		}

		double yDisp = np.disp[1]/deltaLength * Math.min(deltaLength, temp);

		np.loc[0] += xDisp;
		np.loc[1] += yDisp;

		// don't let nodes leave the display
		double borderWidth = bounds.getWidth() / 50.0;
		double x = np.loc[0];
		if (x < bounds.getMinX() + borderWidth) {
			x = bounds.getMinX() + borderWidth + Math.random() * borderWidth * 2.0;
		} else if (x > (bounds.getMaxX() - borderWidth)) {
			x = bounds.getMaxX() - borderWidth - Math.random() * borderWidth * 2.0;
		}

		double y = np.loc[1];
		if (y < bounds.getMinY() + borderWidth) {
			y = bounds.getMinY() + borderWidth + Math.random() * borderWidth * 2.0;
		} else if (y > (bounds.getMaxY() - borderWidth)) {
			y = bounds.getMaxY() - borderWidth - Math.random() * borderWidth * 2.0;
		}

		np.loc[0] = x;
		np.loc[1] = y;
	}
	



	/**
	 * Update temperature for current iteration.
	 * @param curIter
	 */
	private void cool(int curIter) 
	{
		temp *= (1.0 - curIter / (double) maxIterations.getIntValue());
	}


	/**
	 * Handle parameters. 
	 * @param n
	 * @return
	 */
	private Parameters getParameters(Node n) 
	{
		Parameters rp = params.get(n);
		if ( rp == null ) 
		{
			rp = new Parameters();
			params.put(n, rp);
		}
		return rp;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.FR",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Fruchterman-Reingold force-based layout algorithm",
				"Fruchterman-Reingold"				
		);
		return pli;	
	}
	
	public void setRandomInit(boolean randomInit) {
		this.randomInit = randomInit;
	}
	
	public boolean isRandomInit() {
		return randomInit;
	}

}
