package mayday.vis3.graph.components;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.actions.SelectAction;
import mayday.vis3.graph.actions.ZoomAction;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.dialog.ComponentZoomFrame;
import mayday.vis3.graph.listener.CanvasComponentListener;
import mayday.vis3.graph.listener.CanvasZoomListener;
import mayday.vis3.graph.menus.RendererMenu;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererAcceptor;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;


@SuppressWarnings("serial")
public abstract class CanvasComponent extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, 
	Comparable<CanvasComponent>, CanvasZoomListener, RendererAcceptor
{
	protected ComponentRenderer renderer;
	private String label;

	private boolean selected;
	private boolean mouseOver;
	protected JPopupMenu menu;
	protected boolean locked;
	protected boolean drag;
	protected Point dragPoint;
	protected Rectangle origin;
	private double outerZoomFactor;
	private double innerZoomFactor;
	protected Dimension baseSize;
	private Set<CanvasComponentListener> componentListener;
	private JCheckBoxMenuItem selectionMenuItem;
	private Object payload;
	protected RendererMenu rendererMenu;
	protected ComponentZoomFrame zoom;
	
	protected SputnikLabel labelComponent;
	
	public static final int PORT_TOP_LEFT=3*11;
	public static final int PORT_TOP_CENTER=5*11;
	public static final int PORT_TOP_RIGHT=7*11;
	
	public static final int PORT_CENTER_LEFT=3*13;
	public static final int PORT_CENTER_CENTER=5*13;
	public static final int PORT_CENTER_RIGHT=7*13;
	
	public static final int PORT_BOTTOM_LEFT=3*17;
	public static final int PORT_BOTTOM_CENTER=5*17;
	public static final int PORT_BOTTOM_RIGHT=7*17;
	
	public CanvasComponent()
	{
//		setDoubleBuffered(true);
		selected=false;
		menu=null;
		innerZoomFactor=1.0;
		outerZoomFactor=1.0;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
//		setBounds(0, 0, 30, 30);
		baseSize=new Dimension(30,30);
		componentListener=new HashSet<CanvasComponentListener>();
		renderer=DefaultComponentRenderer.getDefaultRenderer();
		selectionMenuItem=new JCheckBoxMenuItem(new ToggleSelectionAction());
		setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
	}

	protected final void setMenu()
	{
		menu=new JPopupMenu();
		menu.add(getLabelMenuItem());		
		menu.add(selectionMenuItem);
		
		JMenu sizeMenu=new JMenu("Size");
		sizeMenu.add(new ResetSizeAction());
		sizeMenu.add(new ResizeAction(5.0, "Maximize"));
		sizeMenu.add(new ResizeAction(0.5, "Minimize"));
		menu.add(new JCheckBoxMenuItem(new HideAction()));
		
		menu.add(sizeMenu);
		if(getRendererMenu()!=null) menu.add(getRendererMenu());
		menu=setCustomMenu(menu);
	}

	protected JMenuItem getLabelMenuItem()
	{
		return new JMenuItem(new ZoomAction(this,zoom));
	}
	
	protected JPopupMenu setCustomMenu(JPopupMenu menu)
	{
		return menu;
	}
	
	public void resetRendererMenu()
	{
		rendererMenu=null;
	}


	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1)
	{
//		super.paint(g1);
		Graphics2D g=(Graphics2D)g1;
		renderer.draw(g, null, new Rectangle(getSize()), getPayload(), labelComponent==null?label:"", selected);
	}

	// Mouse Listener Methods

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#getRenderer()
	 */
	public ComponentRenderer getRenderer() {
		return renderer;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#setRenderer(mayday.canvas.renderer.ComponentRenderer)
	 */
	public void setRenderer(ComponentRenderer renderer) {
		this.renderer = renderer;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#isSelected()
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * set the selection state. do not notify. 
	 */
	public void setSelected(boolean selected) 
	{
		this.selected = selected;
		selectionMenuItem.setSelected(this.selected);
		repaint();
		updateParentSurroundings();
	}


	/**
	 * toggle selection of this component and notify the selection listeners.
	 */
	public void toggleSelection() 
	{
		selected = !selected;
		for(CanvasComponentListener l:componentListener)
		{
			l.componentSelectionChanged(this);
		}
		selectionMenuItem.setSelected(selected);
		repaint();
		updateParentSurroundings();
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#getMenu()
	 */
	public JPopupMenu getMenu() {
		if(menu==null) setMenu();
		return menu;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#setSize(java.awt.Dimension)
	 */
	public void setSize(Dimension d)
	{
		super.setSize(d);
		baseSize=d;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#setSize(int, int)
	 */
	public void setSize(int w, int h)
	{
		super.setSize(w,h);
		baseSize=new Dimension(w,h);
	}

	public void adjustSize(int w, int h)
	{
		if(w < 5 || h < 5 ) return;
		if(w > 5*baseSize.width || h > 5*baseSize.height) return;
		super.setSize(w,h);
	}
	
	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) 
	{
		int selectNeighborsMask= InputEvent.SHIFT_DOWN_MASK;
		int selectAllMask= InputEvent.CTRL_DOWN_MASK  + InputEvent.SHIFT_DOWN_MASK ;


		if(event.getClickCount() >=1 && event.getButton()==MouseEvent.BUTTON1
				&& (event.getModifiersEx() & selectAllMask) == selectAllMask)	
		{

			SelectAction.selectAllReachable(((GraphCanvas)getParent()).getModel(), this);
			event.consume();
			return;
		}


		if(event.getClickCount() >=1 && event.getButton()==MouseEvent.BUTTON1
				&& (event.getModifiersEx() & selectNeighborsMask) == selectNeighborsMask)	
		{
			SelectAction.selectNeighbors(((GraphCanvas)getParent()).getModel(), this);
			event.consume();
			return;
		}
		
		if(event.getClickCount() >=1 && event.getButton()==MouseEvent.BUTTON1)
		{
			toggleSelection();
			event.consume();
			return;
		}
	}	

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0){
		
		if(labelComponent!=null)
		{
			labelComponent.hide(true);
			updateParentLocal();

		}else
		{
			mouseOver=true;
			updateParentLocal();
		}
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.ComponentInterface#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0){
		
		if(labelComponent!=null)
		{
			labelComponent.hide(false);
			updateParentLocal();
		}else
		{
			mouseOver=false;
			updateParentLocal();
		}
	}

	public void mousePressed(MouseEvent event)
	{
		//check for popup menu
		if(event.isPopupTrigger())
		{
//			if(menu==null)
				setMenu();
			menu.show(event.getComponent(),event.getX(), event.getY());
			event.consume();		
		}	
		if(event.getButton()==MouseEvent.BUTTON2)
		{
			zoom=new ComponentZoomFrame(this,getRenderer());
			zoom.setLocation(event.getXOnScreen(),event.getYOnScreen());
			zoom.setVisible(true);
		}		
	}

	public void mouseReleased(MouseEvent event) 
	{		
		if(drag)
		{
			drag=false;
			int x= getX()+event.getX()-(int)dragPoint.getX();
			int y= getY()+event.getY()-(int)dragPoint.getY();
			setLocation(x,y);	
			//,getX(),getY(),getX()+event.getX(),getY()+event.getY());
			// update canvas size
			if(getBounds().getMaxX() > getParent().getWidth() || getBounds().getMaxY() > getParent().getHeight())
			{
//				((GraphCanvas)getParent()).updateSize();
				// this component is no longer completely visible
				int maxX=Math.max((int)getBounds().getMaxX()+25, getParent().getWidth());
				int maxY=Math.max((int)getBounds().getMaxY()+25, getParent().getHeight());
				getParent().setPreferredSize(new Dimension(maxX,maxY));
				getParent().setSize(maxX,maxY);
			}
			for(CanvasComponentListener l:componentListener)
				l.componentMoveFinished();
			updateParent();		
			dragPoint=null; // removes the jumping. 
		}	
	
		if(zoom!=null)
		{
			zoom.dispose();
		}
	}

	public void mouseDragged(MouseEvent event) 
	{		
		if(event.getModifiers()==16 &&!locked)
		{
			if(drag)
			{
				int x= getX()+event.getX()-(int)dragPoint.getX();
				int y= getY()+event.getY()-(int)dragPoint.getY();
				setLocation(x, y);	
				for(CanvasComponentListener l:componentListener)
					l.componentMoved(this, event.getX()-(int)dragPoint.getX(), event.getY()-(int)dragPoint.getY());

				if(!((JComponent)getParent()).getVisibleRect().contains(getBounds()))
				{
					((GraphCanvas)getParent()).scrollRectToVisible(getBounds());
				}	
				origin.add(getBounds());
				
				((GraphCanvas)getParent()).revalidateEdge(this);
				((GraphCanvas)getParent()).updatePlot(origin, this);
			}else
			{
				drag=true;
				origin=getBounds();
				dragPoint=event.getPoint();
				if(!isSelected())
				{
					toggleSelection();
				}				
			}
		}
	}


	public void mouseMoved(MouseEvent e) {		}


	public void mouseWheelMoved(MouseWheelEvent event) 
	{
		if (locked) return;
		final int ctrlmask=MouseEvent.CTRL_DOWN_MASK;
		final int shiftmask=MouseEvent.SHIFT_DOWN_MASK;
		if((event.getModifiersEx() | ctrlmask) == event.getModifiersEx())
		{
			adjustSize(getWidth()+(int)Math.signum(event.getUnitsToScroll())*-2, getHeight());
			event.consume();
			updateParentLocal();
			return;
		}
		if((event.getModifiersEx() | shiftmask ) == event.getModifiersEx())
		{
			adjustSize(getWidth(),getHeight()+(int)Math.signum(event.getUnitsToScroll())*-2);
			event.consume();
			updateParentLocal();			
			return;
		}
		innerZoomFactor= innerZoomFactor+-1*Math.signum(event.getUnitsToScroll())*0.2;
		resize();
		event.consume();
	}
	

	
	private void resize()
	{
		if(innerZoomFactor < 0.5) innerZoomFactor=0.5;
		if(innerZoomFactor > 5) innerZoomFactor=5;
		scale((int)(baseSize.width*innerZoomFactor*outerZoomFactor),(int)(baseSize.height*innerZoomFactor*outerZoomFactor));
		revalidate();
		repaint();
		updateParentSurroundings();
	}
	
	public void minimize()
	{
		scale(5, 5);
	}
	
	public void resetSize()
	{
		innerZoomFactor=1.0;
		setSize(renderer.getSuggestedSize(null,null));
		repaint();
	}

	protected void scale(int w, int h)
	{
		super.setSize(w,h);
	}


	public int compareTo(CanvasComponent o) 
	{
		if(label==null && o.getLabel()!=null) return -1;
		if(label!=null && o.getLabel()==null) return 1;
		if(label==null && o.getLabel()==null) return 0;
		return getLabel().compareTo(o.getLabel());
	}


	public void addCanvasComponentListener(CanvasComponentListener listener)
	{
		componentListener.add(listener);
	}
	
	public void removeCanvasComponentListener(CanvasComponentListener listener) 
	{
		componentListener.remove(listener);		
	}


	public void removeAllComponentListeners()
	{
		componentListener.clear();
	}

	private class ToggleSelectionAction extends AbstractAction
	{
		public ToggleSelectionAction()
		{
			super("Select");
		}

		public void actionPerformed(ActionEvent event) 
		{
			toggleSelection();
		}		
	}

	private class ResizeAction extends AbstractAction
	{		
		double newZoomFactor;
		
		public ResizeAction(double f, String name)
		{
			super(name);
			newZoomFactor=f;
		}

		public void actionPerformed(ActionEvent event) 
		{			
			innerZoomFactor=newZoomFactor;
			resize();
		}		
	}
	
	private class ResetSizeAction extends AbstractAction
	{
		public ResetSizeAction() 
		{
			super("Reset");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			resetSize();			
		}
	}
	
	private class HideAction extends AbstractAction
	{		
		public HideAction()
		{
			super("Hide");			
		}

		public void actionPerformed(ActionEvent event) 
		{			
			setVisible(!isVisible());
			updateParentSurroundings();
		}		
	}


	@Override
	public void setVisible(boolean flag) 
	{
		super.setVisible(flag);
		repaint();
		updateParentSurroundings();
	}


	public void zoomed(double zoomFactor) 
	{
		outerZoomFactor=zoomFactor;
		resize();		
	}


	public void shrink() 
	{
		scale(1,1);
		repaint();
	}
	

	public void restore() 
	{
		resize();		
	}

	protected void updateParentLocal()
	{
		if(getParent() instanceof GraphCanvas)
		{
			((GraphCanvas)getParent()).revalidateEdge(this);
			((GraphCanvas)getParent()).updatePlot(0,0,getX()+getWidth(),getY()+getHeight());
			
		}else
		{
			getParent().repaint(0,0,getX()+getWidth(),getY()+getHeight());
		}
	}
	
	protected void updateParentSurroundings()
	{
		if(getParent() instanceof GraphCanvas)
		{
			((GraphCanvas)getParent()).revalidateEdge(this);
			((GraphCanvas)getParent()).updatePlot(getX()-50,getY()-50,getX()+getWidth()+50,getY()+getHeight()+50);
		}else
		{
			if(getParent()!=null)
				getParent().repaint(0,0,getX()+getWidth(),getY()+getHeight());
		}
	}
	
	protected void updateParent()
	{
		if(getParent() instanceof GraphCanvas)
		{
			((GraphCanvas)getParent()).updatePlot();		
			((GraphCanvas)getParent()).revalidateEdge(this);
		}else
		{
			if(getParent()!=null)
				getParent().repaint();
		}
	}


	/**
	 * @return
	 */
	public Object getPayload() {
		return payload;
	}


	/**
	 * @param payload
	 */
	public void setPayload(Object payload) {
		this.payload = payload;
	}

	/**
	 * @param rendererMenu the rendererMenu to set
	 */
	public void setRendererMenu(RendererMenu rendererMenu) {
		this.rendererMenu = rendererMenu;
	}

	/**
	 * @return  the rendererMenu 
	 */
	public RendererMenu getRendererMenu() {
		return this.rendererMenu;
	}
	
	/**
	 * @return the labelComponent
	 */
	public SputnikLabel getLabelComponent() {
		return labelComponent;
	}

	/**
	 * @param labelComponent the labelComponent to set
	 */
	public void setLabelComponent(SputnikLabel labelComponent) {
		this.labelComponent = labelComponent;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}
	
	public Point getPort(int port)
	{
		Rectangle b=getBounds();
		int x=0;
		if(port % 3 ==0)
			x=b.x;
		
		if(port % 5 ==0)
			x=b.x+b.width/2;
		
		if(port % 7 ==0)
			x=b.x+b.width;
		
		
		int y=0;
		if(port % 11 ==0)
			y=b.y;
		
		if(port % 13 ==0)
			y=b.y+b.height/2;
		
		if(port % 17 ==0)
			y=b.y+b.height;
		
		return new Point(x,y);
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public void toggleLocked() {
		this.locked = !this.locked;
	}
	
	public Orientation getLabelOrientation()
	{
		return null;
	}
	
	public boolean hasLabel()
	{
		return true;
	}
	
	@Override
	public String toString() 
	{
		return getLabel()+"@"+getBounds();
	}
	
	
}
