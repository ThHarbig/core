package mayday.mpf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JPanel;

/** 
 * DesignerPanel is a subclass of JPanel that takes care of painting connections between
 * VisualNode objects and also provides methods for smooth repositioning of VisualNodes
 * @author Florian Battke
 */
public class DesignerPanel extends JPanel {	
    private static final long serialVersionUID = 1L;
    
	/** Creates a new instance of DesignerPanel  
	 */
	public DesignerPanel() {
		super(null); //null layout manager ==> We specify where things should go
		this.setBackground(Color.WHITE);
		setDoubleBuffered(true);
	}
	
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g.create();
		// get absolute coordinates of this panel, needed for coordinate calculations in VisualNode
		Point P = this.getLocationOnScreen(); 
		for (Component cn : this.getComponents())
			if (cn instanceof VisualNode)
				((VisualNode)cn).drawOutgoingConnections(g2d, P);
		g2d.dispose();						
	}
	
    // The following code is for user-friendly moving of nodes

	private moverThread mover;
	
	/**
	 * move VisualNodes to the new positions specified in VisualNode.targetX and VisualNode.targetY
	 */
	public void startMoving() {
		if (mover!=null) mover.halt();
		mover = new moverThread(this);
		mover.start();
	}
	
	private class moverThread extends Thread {
		private DesignerPanel myPanel;
		private Component[] myComponents;
		private int steps=30;
		private int delay=40; // 25 fps => 40ms/frame
		
		public moverThread(DesignerPanel parent) {
			super("Module Mover");
			myPanel=parent;
		}
		
		public synchronized void halt() {
			synchronized(myPanel) {
				myPanel=null;
			}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 * 
		 * Here we move all VisualNodes gracefully to their new positions
		 * All Exceptions and Errors are simply ignored because they are insignificant
		 * The worst that can happen is that a node is not placed at the perfect position
		 */
		public void run()  {
			myComponents = myPanel.getComponents();
			Vector<Point2D.Double> step = new Vector<Point2D.Double>();
			Vector<Point> start = new Vector<Point>();
			try {
				for (Component cn : myComponents ) {
					if (cn instanceof VisualNode) {
						VisualNode vn = (VisualNode)cn;
						int deltaX = vn.targetX-vn.getX();
						int deltaY = vn.targetY-vn.getY();
						double stepX = (double)deltaX / steps;
						double stepY = (double)deltaY / steps;
						Point2D.Double t = new Point2D.Double(stepX, stepY);
						step.addElement(t);
						start.add(new Point(vn.getX(), vn.getY()));
					}
				}				
			} catch (Throwable t) {/*ignore*/}
			for (int i=0; i!=steps; ++i) {
				try {
					if (myPanel==null) return;
					int p=0;
					for (Component cn : myComponents) {
						if (cn instanceof VisualNode) {
							VisualNode vn = (VisualNode)cn;
							double sigmoidalBooster = 1/(1+Math.exp(5-(((double)i)/30*10)))*30;
							//System.out.println(sigmoidalBooster);
							int newX = (int)Math.round(start.get(p).x + sigmoidalBooster*step.get(p).x);
							int newY = (int)Math.round(start.get(p).y + sigmoidalBooster*step.get(p).y);
							vn.setLocation(newX,newY);
							++p;
						}						
					}
				} catch (Throwable t) {/*ignore*/ }
				try {
					myPanel.repaint();
					sleep(delay);
				} catch (Exception e) {
					//ignore
				}
			}
		}		
	}
	

}
