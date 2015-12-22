package mayday.core.structures.trees.screen;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.painter.INodePainter;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;


/**
 * provides the fitting coordinates for this screensize by transforming
 * them accordingly
 * @see @Layout @MainWindow
 * @author Andreas Friedrich, Florian Battke
 * Copies pointers to all members of layout, primitive variables are linked via accessor functions
 */

public class ScreenLayout extends Layout {

	private Layout layout;
	private int width;
	private int height;
	private Insets insets;
	private int minInset = 5; 

	private SelectionManager smgr;

	private HashMap<Node, Double> incomingAngles = new HashMap<Node, Double>();

	/**
	 * Uses a Layout to create a new ScreenLayout
	 * @param layout the Layout object to inherit from
	 */
	public ScreenLayout(Layout layout) {
		this.layout = layout;
		this.root = layout.getRoot();
		this.scaling = layout.getScaling();
		this.edgemap = layout.getEdgeLayouts();
		this.nodemap = layout.getNodeLayouts();
		this.pointmap = layout.getCoordinates();
		this.layouter = layout.getLayouter();
		this.insets = new Insets(0,0,0,0); // Rand oben, links, unten, rechts
		smgr = new DefaultSelectionManager();
	}

	public Coordinate getCoordinate(Node n) {
		return transformed(pointmap.get(n));
	}

	public Coordinate getUntransformedCoordinate(Node n) {
		return pointmap.get(n);
	}

	public void setMinimalMargin(int margin) {
		minInset = margin;
	}
	
	/**
	 * Finds the closest Edge or Node inside a distance of 6 pixels
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 * @return nearest Edge or Node to the given coordinates if the distance is < 6 ; null otherwise
	 */  
	public ITreePart nearestObject(int x, int y, int maxDistance) {
		Edge edge = nearestEdge(x, y, this.root);
		Node node = nearestNode(x, y, this.root);
		
		double nodeDist = distance(node,x,y);
		double edgeDist = distance(edge,x,y);
		
		ITreePart nearest = nodeDist<=edgeDist ? node : edge; // preferentially select nodes
		
		double minDist = Math.min(nodeDist,edgeDist);
		
		if (minDist<=maxDistance)
			return nearest;
				
		return null;
	}

	protected double distance(Node n, int x, int y) {
		if (n==null)
			return Double.POSITIVE_INFINITY;
		return getLayout(n).getPainter().distance(n, this, x, y);
	}
	
	protected double distance(Edge e, int x, int y) {
		if (e==null)
			return Double.POSITIVE_INFINITY;
		return getLayout(e).getPainter().distance(e, this, x, y);
	}
	
	protected double distance(ITreePart t, int x, int y) {
		return (t instanceof Node) ? distance((Node)t,x,y) : distance((Edge)t,x,y);
	}


	private Edge nearestEdge(int x, int y, Node root) {
		return nearestTreePart(x,y, root.postorderEdgeList());
	}
	
	private Node nearestNode(int x, int y, Node root) {
		return nearestTreePart(x,y, root.postorderNodeList(null));
	}
	
	private <T extends ITreePart> T nearestTreePart(int x, int y, Collection<T> parts) {
		T nearest = null;
		double distance = Double.POSITIVE_INFINITY;
		for (T p : parts) {
			double newDistance = distance(p, x, y);
			if (newDistance<distance) {
				distance = newDistance;
				nearest = p;
			}
		}
		return nearest;
	}


	private Coordinate transformed(Coordinate c) {
		if ( scaling == SCALING_NONE ) {
			// assumes that all layout coordinates are final and not to be rescaled
			return c;
		} else {
			// apply scaling
			double xScale = ((scaling & SCALING_WIDTH) != 0) ? width : 1d;
			double yScale = ((scaling & SCALING_HEIGHT) != 0) ? height : 1d;
			double minScale = Math.min(width,height);
			if (scaling == SCALING_BOTH_ASPECT)
				xScale = yScale = minScale;
			double scaledX = c.getX()*xScale + insets.left;
			double scaledY = c.getY()*yScale + insets.top;
			if (scaling == SCALING_BOTH_ASPECT) { // center in view
				scaledX += (width-minScale)/2d;
				scaledY += (height-minScale)/2d;
			}
			return new Coordinate(scaledX, scaledY);
		}
	}
	
	public String getLabel(Node n) {
		return (layout.getLabel(n));
	}

	public Layout getLayout() {
		return this.layout;
	}

	/**
	 * Changes the size of this ScreenLayout
	 * @param size new Dimension object of the wanted size
	 * @see @Dimension
	 */
	public void setSize(Dimension size) {
		Rectangle2D r2d = getLabelBounds();
		if ((scaling & SCALING_WIDTH) != 0) {
			insets.left = (int)Math.max(minInset,Math.abs(r2d.getX())); 
			insets.right = (int)Math.max(minInset,(r2d.getWidth()));
			this.width = size.width - (insets.left+insets.right);
		}
		if ((scaling & SCALING_HEIGHT) != 0) {
			insets.top = (int)Math.max(minInset, Math.abs(r2d.getY()));
			insets.bottom = (int)Math.max(minInset, r2d.getHeight());
			this.height = size.height - (insets.top+insets.bottom);
		}
	}

	/**
	 * Sets new Insets
	 * @param newInsets new Insets object of the wanted size
	 * @see @Insets
	 */
	protected void setInsets(Insets newInsets) {
		// undo old insets
		width += (insets.left+insets.right);
		height += (insets.top+insets.bottom);
		insets = newInsets;
		setSize(new Dimension(width, height));
	}

	public Set<ITreePart> getSelected() {
		return smgr.getSelection();
	}

	public void setSelected(ITreePart t, boolean sel) {
		smgr.setSelected(t, sel);
	}

	public boolean isSelected(ITreePart t) {
		return smgr.isSelected(t);
	}

	/**
	 * Adds a TreePart to the list of selected TreeParts or deletes it from the list
	 * if it was already selected
	 * @param t an Edge or a Node
	 */
	public void toggleSelect(ITreePart t) {
		smgr.setSelected(t, !smgr.isSelected(t));
	}
	/**
	 * Removes all TreeParts from the map
	 */
	public void clearSelected() {
		smgr.clearSelection();
	}

	/** incoming angles define how labels can be placed. negative angles indicate that the label must be placed right-aligned,
	 * positive angles will use left-aligned labels. 
	 * @param node
	 * @param angle
	 */
	public void setIncomingAngle(Node node, Double angle) {
		incomingAngles.put(node, angle);
	}

	/** the angle of the incoming edge. 0 = from top, 90 = from right, 180 = from below, 270 = from left */
	public double getIncomingAngle(Node node) {
		Double d = incomingAngles.get(node);
		if (d==null)
			return 0.0;
		return d;
	}

	public boolean hasIncomingAngles() {
		return !incomingAngles.isEmpty();
	}

	public void setSelectionManager(SelectionManager smg) {
		smgr = smg;
	}

	public SelectionManager getSelectionManager() {
		return smgr;
	}

	protected Rectangle2D getLabelBounds() {
		double w=0, h=0, x=0, y=0;
		for (Node n : root.getLeaves(null)) {
			NodeLayout nl = getLayout(n);
			INodePainter np = nl.getPainter();
			Rectangle2D rectd = np.getNodeBounds(n, this);
			x = Math.min(x, rectd.getX());
			y = Math.min(y, rectd.getY());
			w = Math.max(w, rectd.getWidth());
			h = Math.max(h, rectd.getHeight());
		}
		Rectangle2D max = new Rectangle2D.Double(x,y,w,h);
		return max;
	}

	public int getScaling() {
		return super.getScaling();
	}

	public void setScaling(int Scaling) {
		super.setScaling(Scaling);
	}

	public Node getNode(Object o) {
		return layout.getNode(o);
	}

	public Object getObject(Node n) {
		return layout.getObject(n);
	}

	public void setObjectMapper(ObjectMapper m) {
		layout.setObjectMapper(m);
	}
	
	public ObjectMapper getObjectMapper() {
		return layout.getObjectMapper();
	}

}
