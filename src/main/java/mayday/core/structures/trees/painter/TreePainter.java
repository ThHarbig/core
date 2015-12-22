package mayday.core.structures.trees.painter;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;

import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;

/**
 * paints the tree
 * @author Andreas Friedrich
 *
 */
public class TreePainter  {
	
	private ScreenLayout layoutForPainting = null;
	
	public ITreePart nearestObject(int x, int y) {
		if(layoutForPainting!=null) 
			return layoutForPainting.nearestObject(x,y,3);
		return null;
	}
	
	
	protected void initializeAngles(Dimension size) {
		layoutForPainting.setSize(size);
		if (size.width==0 || size.height==0)
			return; //fix race condition
		Graphics2D g2d = (Graphics2D)new BufferedImage(size.width,size.height,  BufferedImage.TYPE_INT_ARGB).getGraphics();		
		Set<ITreePart> nodeset = layoutForPainting.getNodeSet();
		Iterator<ITreePart> nodeit = nodeset.iterator();			
        while(nodeit.hasNext()) {
        	Node node = (Node)nodeit.next();	        	
        	for (Edge e : node.getEdges()) {
        		if (e.getNode(0)==node) { // only incoming
        			IEdgePainter epainter = layoutForPainting.getLayout(e).getPainter();
        			epainter.paint(e, g2d, layoutForPainting, layoutForPainting.isSelected(e));
        		}
        	}
        }
	}
	
	/**
	 * uses a Layout to paint each Node and each Edge
	 * 
	 */
	public void paint(Graphics2D g, Dimension size) {
		if(layoutForPainting != null) {

			// special case: first painting has no incoming angles for nodes
			if (!layoutForPainting.hasIncomingAngles()) {
				initializeAngles(size);
			}			
			
			layoutForPainting.setSize(size);
			
	        // Jetzt zeichnen wir die Kanten (hinter die Knoten)
			Set<ITreePart> nodeset = layoutForPainting.getNodeSet();
			Iterator<ITreePart> nodeit = nodeset.iterator();			
	        while(nodeit.hasNext()) {
	        	Node node = (Node)nodeit.next();	        	
	        	for (Edge e : node.getEdges()) {
	        		if (e.getNode(0)==node) { // only incoming
	        			IEdgePainter epainter = layoutForPainting.getLayout(e).getPainter();
	        			epainter.paint(e, g, layoutForPainting, layoutForPainting.isSelected(e));
	        		}
	        	}
	        }
	        
	        // Jetzt kommen die Knoten (und verdecken dadurch die Kanten)
	        for (ITreePart itp : layoutForPainting.getNodeSet()) {
	        	Node node = (Node)itp;
	        	INodePainter npainter = layoutForPainting.getLayout(node).getPainter();
	        	npainter.paint(node, g, layoutForPainting, layoutForPainting.isSelected(node));	        
	        }
        }
	}
	/**
	 * Changes the Layout to paint a different tree or the same tree with a different Layout
	 * @param layout
	 * @see @ScreenLayout @Layout
	 */
	public void setLayoutForPainting(Layout layout) {
		ScreenLayout slayout = new ScreenLayout(layout);
		layoutForPainting = slayout;
	}
	
	public ScreenLayout getScreenLayout() {
		return layoutForPainting;
	}
}
