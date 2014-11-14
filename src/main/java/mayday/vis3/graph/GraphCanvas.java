package mayday.vis3.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import mayday.core.DelayedUpdateTask;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.DefaultValueMap;
import mayday.vis3.ZoomController;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.graph.actions.ArrangeAction;
import mayday.vis3.graph.actions.CanvasZoomController;
import mayday.vis3.graph.actions.ClearSelectionAction;
import mayday.vis3.graph.actions.ComponentsAction;
import mayday.vis3.graph.actions.EdgeRouterAction;
import mayday.vis3.graph.actions.GotoAction;
import mayday.vis3.graph.actions.HelpAction;
import mayday.vis3.graph.actions.LockComponents;
import mayday.vis3.graph.actions.Move;
import mayday.vis3.graph.actions.ResetComponents;
import mayday.vis3.graph.actions.ResetViewAction;
import mayday.vis3.graph.actions.SelectAllAction;
import mayday.vis3.graph.arrows.Arrow;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.LabelRenderer;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.components.SputnikLabel;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.components.NodeComponent.NodeUpdate;
import mayday.vis3.graph.dialog.ComponentList;
import mayday.vis3.graph.dialog.InfoFrame;
import mayday.vis3.graph.edges.router.ClockwiseEdges;
import mayday.vis3.graph.edges.router.CounterClockwiseEdges;
import mayday.vis3.graph.edges.router.CurvyEdges;
import mayday.vis3.graph.edges.router.EdgePoints;
import mayday.vis3.graph.edges.router.EdgeRouter;
import mayday.vis3.graph.edges.router.HorizontalCurlyEdges;
import mayday.vis3.graph.edges.router.OverhandBezierEdgeRouter;
import mayday.vis3.graph.edges.router.SimpleEdgeRouter;
import mayday.vis3.graph.edges.router.SmartEdgeRouter;
import mayday.vis3.graph.edges.router.SmoothIntronEdgeRouter;
import mayday.vis3.graph.edges.router.StraightIntronEdgeRouter;
import mayday.vis3.graph.edges.router.UnderhandBezierEdgeRouter;
import mayday.vis3.graph.edges.router.VerticalCurlyEdges;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.GridLayouter;
import mayday.vis3.graph.listener.CanvasComponentListener;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.SelectionModel;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.vis3.TimedEllipse;
import mayday.vis3.gui.PlotContainer;

/**
 * @author symons
 *
 */
@SuppressWarnings("serial")
public class GraphCanvas extends BasicPlotPanel implements MouseMotionListener, MouseListener, CanvasComponentListener
{
	protected GraphModel model;
	protected HashMap<Edge,Path2D> edgeShapes;
	protected ComponentRenderer renderer;
	protected Rectangle selectionRect;
	protected boolean dragRect;
	protected JPopupMenu contextMenu;
	protected JPopupMenu edgeMenu;
	protected double zoomFactor;
	protected static final double[] zoomFactors={0.1, 0.5, 0.75, 1, 1.5, 2, 3};
	protected Edge highlightEdge;
	protected CanvasLayouter layouter;
	protected EdgeRouter edgeRouter;
	protected DefaultValueMap<Edge, ArrowSettings> arrowSettings;
	protected SelectionModel selectionModel;
	protected ZoomController zoomController;
	protected CanvasUpdater updater;
	protected Point dragPoint;
	protected TimedEllipse markEllipse;
	protected Point lastClick;
	protected Orientation defaultLabelOrientation=Orientation.BELOW;
	protected ComponentList componentList;
	protected boolean showLabel=true;
	protected Set<CanvasComponent> movingComponents=new HashSet<CanvasComponent>();
	protected Font labelFont=new Font(Font.SANS_SERIF,Font.PLAIN,12);
	protected boolean scaleLabels=true; 
	protected HashMap<Edge, Point2D> edgeSupportPoints=new HashMap<Edge, Point2D>();
	protected HashMap<Edge, EdgePoints> edgePoints=new HashMap<Edge, EdgePoints>();
	
	public GraphCanvas(GraphModel model)
	{
		init(model,new SelectionModel(model));
	}

	public GraphCanvas(GraphModel model,SelectionModel selModel)
	{
		init(model,selModel);
	}

	protected void init(GraphModel model, SelectionModel selModel)
	{
		setFocusable(true);
		updater=new CanvasUpdater("Update Canvas");
		layouter=new GridLayouter();
		edgeRouter=new SimpleEdgeRouter();
		setSize(2000, 2000);
		edgeShapes=new HashMap<Edge, Path2D>();
		arrowSettings=new DefaultValueMap<Edge, ArrowSettings>(new HashMap<Edge, ArrowSettings>(),new ArrowSettings());
		renderer=DefaultComponentRenderer.getDefaultRenderer();
		
		selectionModel=selModel;
		
		setModel(model);
		setLayout(null);
		setDoubleBuffered(true);		
		addMouseMotionListener(this);
		addMouseListener(this);
		dragRect=false;
		zoomFactor=1.0;
		
		setContextMenu();	
		setEdgeMenu();
		zoomController=new CanvasZoomController();

		getInputMap().put(KeyStroke.getKeyStroke("F1"),"help");
		getInputMap().put(KeyStroke.getKeyStroke("F3"),"search");
		getInputMap().put(KeyStroke.getKeyStroke("F5"),"resetView");
		getInputMap().put(KeyStroke.getKeyStroke("F6"),"fitWidth");
		getInputMap().put(KeyStroke.getKeyStroke("F7"),"fitFrame");
		getInputMap().put(KeyStroke.getKeyStroke("F8"),"resetComponents");
		getInputMap().put(KeyStroke.getKeyStroke("F9"),"hideLabel");
		getInputMap().put(KeyStroke.getKeyStroke("0"),"resetZoom");
		getInputMap().put(KeyStroke.getKeyStroke("HOME"),"gotoHome");
		getInputMap().put(KeyStroke.getKeyStroke("END"),"gotoEnd");
		getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"),"clearSelection");
		getInputMap().put(KeyStroke.getKeyStroke("DELETE"),"hideSelected");
		getInputMap().put(KeyStroke.getKeyStroke("F2"),"lockUnlock");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,KeyEvent.CTRL_MASK),"moveup");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,KeyEvent.CTRL_MASK),"movedown");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.CTRL_MASK),"moveleft");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.CTRL_MASK),"moveright");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK),"moveup10");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK),"movedown10");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK),"moveleft10");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK),"moveright10");
		
		getInputMap().put(KeyStroke.getKeyStroke('h'),"arrangeHCenter");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H,KeyEvent.CTRL_MASK),"arrangeHLeft");	
		getInputMap().put(KeyStroke.getKeyStroke('j'),"arrangeVCenter");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_J,KeyEvent.CTRL_MASK),"arrangeVTop");	
		
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_MASK), "selectAll");
		
		getActionMap().put("help",new HelpAction());
		getActionMap().put("search",new ComponentsAction(this));
		getActionMap().put("resetView",new ResetViewAction(this));
		getActionMap().put("fitWidth",new FitWidthAction());
		getActionMap().put("fitFrame",new FitFrameAction());
		getActionMap().put("resetZoom",new ZoomAction(1.0));
		getActionMap().put("gotoHome",new GotoAction(this, GotoAction.HOME));
		getActionMap().put("gotoEnd",new GotoAction(this, GotoAction.END));
		getActionMap().put("clearSelection",new ClearSelectionAction(this));
		getActionMap().put("hideSelected",new HideSelectedAction());
		getActionMap().put("lockUnlock",new LockComponents(this));
		getActionMap().put("resetComponents",new ResetComponents(this));
		getActionMap().put("selectAll",new SelectAllAction(this));
		getActionMap().put("hideLabel",new ToggleLabelAction());
		
		getActionMap().put("moveup",new Move(1, Move.UP, this));
		getActionMap().put("movedown",new Move(1, Move.DOWN, this));
		getActionMap().put("moveleft",new Move(1, Move.LEFT, this));
		getActionMap().put("moveright",new Move(1, Move.RIGHT, this));
		getActionMap().put("moveup10",new Move(10, Move.UP, this));
		getActionMap().put("movedown10",new Move(10, Move.DOWN, this));
		getActionMap().put("moveleft10",new Move(10, Move.LEFT, this));
		getActionMap().put("moveright10",new Move(10, Move.RIGHT, this));
		
		getActionMap().put("arrangeHCenter",new ArrangeAction(SwingConstants.CENTER, SwingConstants.HORIZONTAL, this));
		getActionMap().put("arrangeHLeft",new ArrangeAction(SwingConstants.LEADING, SwingConstants.HORIZONTAL, this));
		getActionMap().put("arrangeVCenter",new ArrangeAction(SwingConstants.CENTER, SwingConstants.VERTICAL, this));
		getActionMap().put("arrangeVTop",new ArrangeAction(SwingConstants.LEADING, SwingConstants.VERTICAL, this));
	}
	
	protected void setContextMenu()
	{
		contextMenu=new JPopupMenu();
		contextMenu.add(new SelectAllAction(this));
		contextMenu.add(new ClearSelectionAction(this));
		contextMenu.add(new ShowOnlySelectedAction());
		contextMenu.add(new HideSelectedAction());
		JMenu zoomMenu=new JMenu("Zoom");
		zoomMenu.add(new FitWidthAction());
		zoomMenu.add(new FitFrameAction());
		zoomMenu.addSeparator();
		for(double f:zoomFactors)
		{
			zoomMenu.add(new ZoomAction(f));
		}
		contextMenu.add(zoomMenu);
		contextMenu.add(new ComponentsAction(this));

		JMenu routerMenu=new JMenu("Edges");
		routerMenu.add(new EdgeRouterAction(new SimpleEdgeRouter(), "Simple Edges", this ));
		routerMenu.add(new EdgeRouterAction(new CurvyEdges(), "Bezier Edges Type 1", this ));
		routerMenu.add(new EdgeRouterAction(new OverhandBezierEdgeRouter(), "Bezier Edges Type 2", this ));
		routerMenu.add(new EdgeRouterAction(new UnderhandBezierEdgeRouter(), "Bezier Edges Type 3", this ));
		routerMenu.add(new EdgeRouterAction(new ClockwiseEdges(), "Clockwise", this ));
		routerMenu.add(new EdgeRouterAction(new CounterClockwiseEdges(), "Counter clockwise", this ));
		routerMenu.add(new EdgeRouterAction(new SmartEdgeRouter(), "Smart Edges", this ));
		routerMenu.add(new EdgeRouterAction(new VerticalCurlyEdges(), "Curly 1", this ));
		routerMenu.add(new EdgeRouterAction(new HorizontalCurlyEdges(), "Curly 2", this ));
		routerMenu.add(new EdgeRouterAction(new StraightIntronEdgeRouter(), "Introns", this ));
		routerMenu.add(new EdgeRouterAction(new SmoothIntronEdgeRouter(), "Smooth Introns", this ));
		
		contextMenu.add(routerMenu);

		setCustomMenu();
	}

	protected void setEdgeMenu()
	{
		edgeMenu=new JPopupMenu();
		edgeMenu.add(new RemoveEdgeAction());
		//		edgeMenu.add();
	}

	protected void setCustomMenu()
	{
		//do nothing, use when subclassing.
	}

	/**
	 * @return the model
	 */
	public GraphModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(GraphModel model) 
	{
		if(model==null) return;
		this.model = model;	
		removeAll();
		for(CanvasComponent c:this.model.getComponents())
		{
			add(c);	
			c.setRenderer(renderer);
			c.setSize(renderer.getSuggestedSize(null,null));				
			c.addCanvasComponentListener(selectionModel);
		}
		layouter.layout(this, new Rectangle(0,0,1000,1000), getModel());
		updateSize();
		edgeShapes=new HashMap<Edge, Path2D>(model.getEdges().size());
		edgePoints.clear();
		edgeSupportPoints.clear();
		setSelectionModel(selectionModel);
	}

	public void setSelectionModel(SelectionModel selectionModel)
	{
		if(this.selectionModel!=null)
		{
			for(CanvasComponent c:this.model.getComponents())
			{
				c.removeCanvasComponentListener(this.selectionModel);
			}
		}
		this.selectionModel=selectionModel;
		setContextMenu();		
		for(CanvasComponent c:this.model.getComponents())
		{
			c.addCanvasComponentListener(this.selectionModel);
		}		
	}

	protected Shape getShape(Edge e)
	{
		Shape s=edgeShapes.get(e);
		if(s==null)
		{
			edgeShapes.put(e,edgeRouter.routeEdge(e,this, model));	
			s=edgeShapes.get(e);
		}
		return s;
	}

	public void updateSize()
	{
		int maxX=getWidth();
		int maxY=getHeight();
		for(CanvasComponent c:this.model.getComponents())
		{
			if(c.getBounds().getMaxX() > maxX) maxX=c.getBounds().x+c.getBounds().width;
			if(c.getBounds().getMaxY() > maxY) maxY=c.getBounds().y+c.getBounds().height;
		}
		maxX+=50;
		maxY+=50;
		setPreferredSize(new Dimension(maxX,maxY));
		setSize(new Dimension(maxX,maxY));
		updatePlot();
	}

	public void mouseDragged(MouseEvent event) 
	{
		if(dragRect)
		{		
			Rectangle old=selectionRect;
			selectionRect=new Rectangle(dragPoint.x,dragPoint.y,1,1);
			selectionRect.add(event.getPoint());	
			
			Rectangle reprect=new Rectangle(
					(int)Math.min(old.x,selectionRect.x)-10,
					(int)Math.min(old.y,selectionRect.y)-10,
					(int)Math.max(old.getMaxX(),selectionRect.getMaxX())-(int)Math.min(old.x,selectionRect.x)+100,
					(int)Math.max(old.getMaxY(),selectionRect.getMaxY())-(int)Math.min(old.y,selectionRect.y)+100);
			
			repaint(reprect);
				event.consume();
		}else
		{
			selectionRect=new Rectangle(event.getX(),event.getY(),1,1);
			dragPoint=event.getPoint();
			dragRect=true;		
			repaint();
		}		
	}

	public void mouseMoved(MouseEvent event) 
	{	
		event.consume();	
		Rectangle eventRect=new Rectangle(event.getX()-1, event.getY()-1, 3, 3);
		boolean pre= highlightEdge==null;
		for(Edge e: getModel().getEdges())
		{				
			if(edgeShapes.get(e)==null) continue;
			if( ((Path2D)getShape(e)).intersects(eventRect))
			{		
				if(!pre && highlightEdge!=e)
				{
					Rectangle r=getShape(highlightEdge).getBounds();
					r.add(model.getComponent(highlightEdge.getSource()).getBounds());
					r.add(model.getComponent(highlightEdge.getTarget()).getBounds());
					highlightEdge=null;
					updatePlot(r.x-2,r.y-2,r.width+2, r.height+2);
				}
				highlightEdge=e;
				Rectangle r=getShape(e).getBounds();
				r.add(model.getComponent(highlightEdge.getSource()).getBounds());
				r.add(model.getComponent(highlightEdge.getTarget()).getBounds());
				updatePlot(r.x-5,r.y-5,r.width+5, r.height+5);
				return;
			}
		}
		if(!pre && highlightEdge==null)
		{
			updatePlot();
		}		
				
	}

	public void mouseClicked(MouseEvent event) 
	{
		requestFocusInWindow();
		if(event.getButton()==MouseEvent.BUTTON1)
		{		
			selectionModel.clearSelection();
			event.consume();
		}
		if(highlightEdge!=null)
		{
			highlightEdge=null;
			updatePlot();
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent event) 
	{
		if(event.isPopupTrigger())
		{
			Rectangle eventRect=new Rectangle(event.getX()-1, event.getY()-1, 3, 3);
			for(Edge e: getModel().getEdges())
			{	
				if(edgeShapes.get(e)==null) continue;
				if( ((Path2D)getShape(e)).intersects(eventRect))
				{		
					highlightEdge=e;
					updatePlot();
					edgeMenu.show(event.getComponent(),event.getX(), event.getY());
					event.consume();
					return;
				}
			}
			lastClick=event.getPoint();
			//			if(contextMenu==null) 
			setContextMenu();
			contextMenu.show(event.getComponent(),event.getX(), event.getY());
			event.consume();		
		}		
	}

	public void mouseReleased(MouseEvent e) 
	{
		if(dragRect)
		{
			dragRect=false;
			doRectSelection();
		}		
	}

	private void doRectSelection()
	{
		selectionModel.clearSelection();
		for(int i=0; i!= model.getComponents().size(); ++i)
		{
			if(selectionRect.contains(model.getComponents().get(i).getBounds()))
			{
				selectionModel.select(model.getComponents().get(i));
			}
		}
		updatePlot();
	}

	//	public void updatePlot() 
	//	{
	//		revalidate();
	//		repaint();
	//	}

	@Override
	public void setup(PlotContainer plotContainer) 
	{
		zoomController.setTarget(this);
		zoomController.setAllowXOnlyZooming(true);
		zoomController.setAllowYOnlyZooming(true);
		zoomController.setActive(true);
	}

	public class ZoomAction extends AbstractAction
	{
		private double factor;
		public ZoomAction(double f)
		{
			super(""+f);
			factor=f;
		}

		public void actionPerformed(ActionEvent e) 
		{
			zoom(factor);
		}		
	}

	public class FitWidthAction extends AbstractAction
	{
		public FitWidthAction()
		{
			super("Fit width");
		}

		public void actionPerformed(ActionEvent e) 
		{
			fitWidth();
		}		
	}

	protected void fitWidth()
	{
		//determine screen width 
		int w=getParent().getWidth()-15;
		if(w <= 0) w=800;
		//determine maxX of all components
		double maxX=0;
		for(int i=0; i!= getComponentCount(); ++i)
		{
			if(!getComponent(i).isVisible()) continue;
			if(getComponent(i).getBounds().getMaxX() > maxX) maxX=getComponent(i).getBounds().getMaxX();
		}
		// calculate zoom factor
		double factor= w / maxX;
		if(factor > 0.95 && factor < 1.0) return;
		zoom(factor*zoomFactor);
	}

	public class FitFrameAction extends AbstractAction
	{
		public FitFrameAction()
		{
			super("Fit to Frame");
		}

		public void actionPerformed(ActionEvent e) 
		{
			fitFrame();
		}		
	}

	protected void fitFrame()
	{
		//determine screen width 
		int w=getParent().getWidth()-15;
		int h=getParent().getHeight()-15;
		if(w <= 0) w=800;
		if(h <= 0) h=800;
		//determine maxX of all components
		double maxX=0;
		double maxY=0;
		for(int i=0; i!= getComponentCount(); ++i)
		{
			if(!getComponent(i).isVisible()) continue;
			if(getComponent(i).getBounds().getMaxX() > maxX) maxX=getComponent(i).getBounds().getMaxX();
			if(getComponent(i).getBounds().getMaxY() > maxY) maxY=getComponent(i).getBounds().getMaxY();
		}
		// calculate zoom factor
		double factorX= w / maxX;
		double factorY= h / maxY;			
		zoom(zoomFactor*Math.min(factorX, factorY));
	}

	public void zoom(double factor)
	{
		if(factor < 0.1)
			factor = 0.1;
		if(factor > 10)
			factor= 10.0;
		double zfq=zoomFactor/factor;
		zoomFactor=factor;
		//setSize((int)(getPreferredSize().width*zoomFactor), (int)(getPreferredSize().height*zoomFactor));
		setSize((int)(getWidth()/zfq), (int)(getHeight()/zfq));
		setPreferredSize(getSize());
		for(int i=0; i!= getComponentCount(); ++i)
		{
			if(getComponent(i) instanceof CanvasComponent)
			{
				CanvasComponent comp=(CanvasComponent)getComponent(i);
				comp.zoomed(zoomFactor);
				comp.setLocation((int)(comp.getLocation().getX()/zfq), (int)(comp.getLocation().getY()/zfq));		
			}
			if(getComponent(i) instanceof SputnikLabel)
			{
				SputnikLabel comp=((SputnikLabel)getComponent(i));
				comp.zoomed(zoomFactor);
//				comp.setLocation((int)(comp.getLocation().getX()/zfq), (int)(comp.getLocation().getY()/zfq));		
			}
			
		}
		updateSize();
		updatePlot();
		if(getMousePosition()!=null)
			centerSilent(new Rectangle(getMousePosition()), false);
	}

	public void center(Rectangle r, boolean withInsets)
	{
		Rectangle visible = this.getVisibleRect();

		visible.x = r.x - (visible.width - r.width) / 2;
		visible.y = r.y - (visible.height - r.height) / 2;

		Rectangle bounds = this.getBounds();
		Insets i = withInsets ? new Insets(0, 0, 0, 0) : this.getInsets();
		bounds.x = i.left;
		bounds.y = i.top;
		bounds.width -= i.left + i.right;
		bounds.height -= i.top + i.bottom;

		if (visible.x < bounds.x) visible.x = bounds.x;

		if (visible.x + visible.width > bounds.x + bounds.width)
			visible.x = bounds.x + bounds.width - visible.width;

		if (visible.y < bounds.y) visible.y = bounds.y;

		if (visible.y + visible.height > bounds.y + bounds.height)
			visible.y = bounds.y + bounds.height - visible.height;

		this.scrollRectToVisible(visible);
		markEllipse=new TimedEllipse(r);
		updatePlotNow();
	}
	
	public void centerSilent(Rectangle r, boolean withInsets)
	{
		Rectangle visible = this.getVisibleRect();
		visible.x = r.x - (visible.width - r.width) / 2;
		visible.y = r.y - (visible.height - r.height) / 2;

		Rectangle bounds = this.getBounds();
		Insets i = withInsets ? new Insets(0, 0, 0, 0) : this.getInsets();
		bounds.x = i.left;
		bounds.y = i.top;
		bounds.width -= i.left + i.right;
		bounds.height -= i.top + i.bottom;

		if (visible.x < bounds.x) visible.x = bounds.x;

		if (visible.x + visible.width > bounds.x + bounds.width)
			visible.x = bounds.x + bounds.width - visible.width;

		if (visible.y < bounds.y) visible.y = bounds.y;

		if (visible.y + visible.height > bounds.y + bounds.height)
			visible.y = bounds.y + bounds.height - visible.height;

		
		this.scrollRectToVisible(visible);
		updatePlotNow();
	}

	/**
	 * @return the layouter
	 */
	public CanvasLayouter getLayouter() {
		return layouter;
	}

	/**
	 * @param layouter the layouter to set
	 */
	public void setLayouter(CanvasLayouter layouter) 
	{
		this.layouter = layouter;
		if(getModel()!=null && model.componentCount()!=0)
		{
			updateLayout();
		}
	}

	/**
	 * @return the zoomFactor
	 */
	public double getZoomFactor() 
	{
		return zoomFactor;
	}

	/**
	 * @param zoomFactor the zoomFactor to set
	 */
	public void setZoomFactor(double zoomFactor) 
	{
		this.zoomFactor = zoomFactor;
		zoom(this.zoomFactor);
	}

	public void setZoomFactorIncrease(double increase) 
	{		
		zoom(zoomFactor*increase);
	}

	/**
	 * @return the renderer
	 */
	public ComponentRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(ComponentRenderer renderer) 
	{
		this.renderer = renderer;
		if(model==null) return;
		for(CanvasComponent c:this.model.getComponents())
		{
			c.setRenderer(renderer);
			if(c instanceof NodeComponent)
				c.setSize(renderer.getSuggestedSize(((NodeComponent) c).getNode(),null));
			else
				c.setSize(renderer.getSuggestedSize(null,null));
		}
		updateLayout();		
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRendererSilent(ComponentRenderer renderer) 
	{
		this.renderer = renderer;
		if(model==null) return;
		for(CanvasComponent c:this.model.getComponents())
		{
			c.setRenderer(renderer);
			c.setSize(renderer.getSuggestedSize(null,null));
		}
		revalidateEdges();
		updatePlot();

	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(Map<String,ComponentRenderer> rendererMap) 
	{
		if(model==null) return;
		for(CanvasComponent c:this.model.getComponents())
		{
			ComponentRenderer render=renderer;
			if(c instanceof NodeComponent)
			{
				if(rendererMap.get(((NodeComponent) c).getNode().getRole())!=null)
					render=rendererMap.get(((NodeComponent) c).getNode().getRole());
			}
			c.setRenderer(render);
		}
		updateLayout();		
	}

	/**
	 * resizes all components to the optimal size set by their respective renderer. 
	 */
	public void resizeComponents()
	{
		for(CanvasComponent c:this.model.getComponents())
		{
			c.setSize(c.getRenderer().getSuggestedSize(null,null));
		}
	}

	/**
	 * Resize all components to the given size. 
	 * @param d
	 */
	public void resizeComponents(Dimension d)
	{
		for(CanvasComponent c:this.model.getComponents())
		{
			c.setSize(d);
		}
	}

	public void updateLayout(Rectangle bounds)
	{
		layouter.layout(this, bounds, getModel());
		updateSize();
		revalidateEdges();
	}
	
	public void updateLayout()
	{
		updateLayout(getBounds());
	}

	public void paint(Graphics g)
	{
		paintPlot((Graphics2D)g);		
	}

	public void paintPlot(Graphics2D g)
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		((Graphics2D)getGraphics()).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		((Graphics2D)getGraphics()).setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		((Graphics2D)getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.black);
		paintEdges(g, model.getEdges());
		paintChildren(g);
		paintLabels(g);
//		g.setStroke(new BasicStroke(0.0f));
		if(dragRect)
		{   	
			g.setStroke(new BasicStroke(1.0f));
			g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
			Color c=new Color(128,128,200,80);
			g.setColor(c);
			g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
		}
		g.setStroke(new BasicStroke(0.0f));
		if(markEllipse!=null)
		{
			if(!markEllipse.stillAlive())
			{
				markEllipse=null;
			}else
			{
				g.setStroke(new BasicStroke(2));
				g.setColor(Color.red);
				g.draw(markEllipse);
			}
		}
		g.transform(AffineTransform.getScaleInstance(zoomFactor, zoomFactor));   
	}

	protected void paintEdges(Graphics2D g, Collection<? extends Edge> edges)
	{
		for(Edge e: edges)
		{
			//			if(! getVisibleRect().contains((model.getComponent(e.getSource()).getLocation())) &&  
			//			   ! getVisibleRect().contains((model.getComponent(e.getTarget()).getLocation())) )
			//					continue;

			if(model.getComponent(e.getSource()).isVisible() && model.getComponent(e.getTarget()).isVisible())
			{				
				if(edgeShapes.get(e)==null)
				{
					edgeShapes.put(e,edgeRouter.routeEdge(e,this, model));					
				}
				EdgePoints points=edgeRouter.getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
				Point2D point=edgeRouter.getSupportPoint(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
				// draw name

				Path2D es=edgeShapes.get(e);
				// extract second last point in here: 
				PathIterator pi=es.getPathIterator(null);
				while(!pi.isDone())
				{
					double[] cur=new double[6];
					int t=pi.currentSegment(cur);
					if(t!=PathIterator.SEG_CLOSE && t!=PathIterator.SEG_MOVETO)
					{						
						if(t==PathIterator.SEG_CUBICTO)
							point.setLocation(cur[2], cur[3]);
						if(t==PathIterator.SEG_QUADTO)
							point.setLocation(cur[0], cur[1]);
					}
					pi.next();
				}	

				Stroke backupStroke=g.getStroke();

				float s=(float)(e.getWeight()==0?1:e.getWeight());
				Stroke stroke=new BasicStroke(s);
				g.setStroke(stroke);
				if(e == highlightEdge)
				{

					g.setColor(Color.ORANGE);
					g.draw(edgeShapes.get(e));
					if(arrowSettings.get(e).isRenderTarget())
					{
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}
					if(arrowSettings.get(e).isFillTarget())
					{
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}					
					g.setColor(Color.black);

				}else
				{
					g.setColor(Color.black);
					Path2D p=edgeShapes.get(e);
					g.setColor(arrowSettings.get(e).getEdgeColor());
					g.draw(p);
					// draw Arrowheads:
					if(arrowSettings.get(e).isRenderTarget())
					{
						g.setColor(Color.white);
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.setColor(arrowSettings.get(e).getEdgeColor());
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}
					if(arrowSettings.get(e).isFillTarget())
					{
						g.setColor(arrowSettings.get(e).getFillColor());
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}
				}
				g.setStroke(backupStroke);	
				// draw name
				//				if(e.getName()!=null && !e.getName().isEmpty())
				//				{
				//					g.drawString(e.getName(), (int)edgeShapes.get(e).getBounds().getCenterX(), (int)edgeShapes.get(e).getBounds().getCenterY());
				//				}


			}
		}
	}

	/**
	 * @return the highlightEdge
	 */
	public Edge getHighlightEdge() {
		return highlightEdge;
	}

	/**
	 * @param highlightEdge the highlightEdge to set
	 */
	protected void setHighlightEdge(Edge highlightEdge) {
		this.highlightEdge = highlightEdge;
	}

	public void updatePlot(int x, int y, int w, int h) 
	{
//		revalidate);
		repaint(x,y,w,h);
	}
	
	
	/**
	 * Repaints the part of the plot occupied with the component cc and its neigbors. 
	 * @param cc
	 */
	public void updatePlot(Rectangle r, CanvasComponent cc)
	{
		if(model.getNode(cc)!=null)
		{
			for(Node n:model.getGraph().getNeighbors(model.getNode(cc)))
			{
				r.add(model.getComponent(n).getBounds());
			}
		}
		repaint(r);
	}

	public void updatePlot()
	{
		updatePlotNow();
//		updater.trigger();
	}

	public void updatePlotNow()
	{
		revalidate();
		repaint();		
	}



	/**
	 * Force the canvas to repaint all edges adjacent to the node represented by the component
	 * @param comp
	 */
	public void revalidateEdge(CanvasComponent comp)
	{
		if(comp instanceof NodeComponent && isShowing())
		{
			if(getModel().getGraph().edgeCount()==0) return;
			for(Edge e:getModel().getGraph().getAllEdges(((NodeComponent)comp).getNode()))
			{
//				edgeShapes.put(e, null);
				edgeShapes.remove(e);
				edgeSupportPoints.remove(e);
				edgePoints.remove(e);
			}
		}
	}

	public void revalidateEdges()
	{
		edgeShapes.clear();
		edgeSupportPoints.clear();
		edgePoints.clear();
	}	

	private class ShowOnlySelectedAction extends AbstractAction
	{
		public ShowOnlySelectedAction()
		{
			super("Show only selected components");
		}

		public void actionPerformed(ActionEvent e) 
		{
			selectionModel.showOnlySelected();
			updatePlot();
		}
	}

	private class HideSelectedAction extends AbstractAction
	{
		public HideSelectedAction()
		{
			super("Hide selected components");
			putValue(TOOL_TIP_TEXT_KEY, "Remove the selected components from the view.");
		}

		public void actionPerformed(ActionEvent e) 
		{
			selectionModel.hideSelected();
			updatePlot();
		}		
	}
	
	private class ToggleLabelAction extends AbstractAction
	{
		public ToggleLabelAction()
		{
			super("Show/Hide Labels");
			putValue(TOOL_TIP_TEXT_KEY, "Show or hide the node labels");
		}

		public void actionPerformed(ActionEvent e) 
		{
			showLabel= !showLabel;
			message("Node labels "+(showLabel?"on":"off"));
			updatePlot();
		}		
	}

	private class RemoveEdgeAction extends AbstractAction
	{
		public RemoveEdgeAction()
		{
			super("Remove edge");
		}

		public void actionPerformed(ActionEvent e) 
		{
			model.removeEdge(highlightEdge);
			revalidateEdges();
			((NodeComponent)model.getComponent(highlightEdge.getSource())).nodeUpdated(NodeUpdate.EDGES);
			((NodeComponent)model.getComponent(highlightEdge.getTarget())).nodeUpdated(NodeUpdate.EDGES);
			updatePlot();
		}
	}

	/**
	 * @return the edgeRouter
	 */
	public EdgeRouter getEdgeRouter() {
		return edgeRouter;
	}

	/**
	 * @param edgeRouter the edgeRouter to set
	 */
	public void setEdgeRouter(EdgeRouter edgeRouter) {
		this.edgeRouter = edgeRouter;
		revalidateEdges();
	}

	/**
	 * @return the selectionModel
	 */
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void remove(CanvasComponent comp)
	{
		comp.removeNotify();
		super.remove(comp);
	}

	/**
	 * @return the arrowSettings
	 */
	public DefaultValueMap<Edge, ArrowSettings> getArrowSettings() {
		return arrowSettings;
	}

	/**
	 * @param arrowSettings the arrowSettings to set
	 */
	public void setArrowSettings(DefaultValueMap<Edge, ArrowSettings> arrowSettings) {
		this.arrowSettings = arrowSettings;
	}

	public class CanvasUpdater extends DelayedUpdateTask
	{
		public CanvasUpdater(String name) 
		{
			super(name,50);			
		}

		@Override
		protected boolean needsUpdating() 
		{
			return true;
		}

		@Override
		protected void performUpdate() 
		{
			revalidate();
			repaint();					
		}

	}

	public void componentMoved(CanvasComponent sender, int dx, int dy) 
	{
		if(movingComponents.isEmpty())
			repaint();
		movingComponents.add(sender);
		for(CanvasComponent cc: getSelectionModel().getSelectedComponents())
		{
			if(cc==sender)
				continue;
			cc.setLocation(cc.getX()+dx, cc.getY()+dy);
			Rectangle b=cc.getBounds();
			b.height+=30;
			revalidateEdge(cc);
			updatePlot(b, cc);
			movingComponents.add(cc);
		}	
	}

	public void componentMoveFinished()
	{
		for(CanvasComponent cc: getSelectionModel().getSelectedComponents())
		{
			if(cc==null)
				continue;
			if(cc.getBounds().getMaxX() > getWidth() || cc.getBounds().getMaxY() > getHeight())
			{
				// this component is no longer completely visible
				int maxX=Math.max((int)cc.getBounds().getMaxX()+25, getWidth());
				int maxY=Math.max((int)cc.getBounds().getMaxY()+25, getHeight());
				setPreferredSize(new Dimension(maxX,maxY));
				setSize(maxX,maxY);
			}
			revalidateEdge(cc);
		}
		movingComponents.clear();
	}	
	public void componentSelectionChanged(CanvasComponent component){} // do nothing

	@Override
	public Dimension getPreferredSize() 
	{
		int maxX=0;
		int maxY=0;

		for(Component c:getComponents())
		{
			maxX=Math.max(maxX,c.getLocation().x+c.getWidth());
			maxY=Math.max(maxY,c.getLocation().y+c.getHeight());				
		}
		maxX+=20;
		maxY+=20;
		return new Dimension(maxX, maxY);

	}

	@Override
	public Dimension getSize() 
	{
		return getPreferredSize();
	}
	
	public void paintLabels(Graphics2D g)
	{
		paintLabels(g, new LabelRenderer());
	}
	
	public void paintLabels(Graphics2D g, LabelRenderer renderer)
	{
		if(zoomFactor < 0.5)
			return;
		double zoomFactor =this.zoomFactor;
		if(!scaleLabels)
			zoomFactor=1.0;
		
//		showLabel=true;
		if(!showLabel)
			return;
//		LabelRenderer renderer=new LabelRenderer();
		renderer.setFont(labelFont);
		int i = getComponentCount() - 1;
        if (i < 0) 
        {
            return;
        }
        
        Rectangle tmpRect = new Rectangle(); 
        Orientation ori=null;
        for (; i >= 0 ; i--) {
            Component comp = getComponent(i); 
            if(!(comp instanceof CanvasComponent))
            	continue;
            if (comp.isVisible())
            {
	            Rectangle cr;
	            cr = comp.getBounds(tmpRect);
	            	            
	            boolean hitClip = g.hitClip(cr.x, cr.y, cr.width,cr.height+30);
	            if(hitClip)
	            {	
	            	CanvasComponent cc=((CanvasComponent)comp);  
	            	if(!cc.hasLabel())
	            		continue;
	            	JLabel rr=null;
	            	ori=cc.getLabelOrientation()==null?defaultLabelOrientation:cc.getLabelOrientation();
	            	if(!cc.isMouseOver())
	            	{
	            		rr=renderer.getLabelComponent(this, cc, cc.isSelected(),ori);
	            	}else
	            	{
	            		rr=renderer.getLabelComponent(this, cc, cc.isSelected(),Orientation.BELOW);
	            	}
	            	rr.getBounds(cr);
	            	      
	            	Graphics cg = g.create(cc.getX()-(int)(((cr.width*zoomFactor)-cc.getWidth() )/2.0), cr.y, (int)(cr.width*zoomFactor),(int)(cr.height*zoomFactor));
	            	((Graphics2D)cg).scale(zoomFactor, zoomFactor);
                    cg.setColor(comp.getForeground());
                    cg.setFont(comp.getFont());	       
                   	rr.paint(cg);          	
	            }
            }
        }
	}

	public Point getLastClick() {
		return lastClick;
	}

	public void message(String message) {
		new InfoFrame(getOutermostJWindow(), message, 5000).setVisible(true);		
	}
	
	public void message(ImageIcon icon, String message) {
		new InfoFrame(getOutermostJWindow(), message, icon, 5000).setVisible(true);		
	}
	
	public void message(ImageIcon icon) {
		new InfoFrame(getOutermostJWindow(), icon, 5000).setVisible(true);		
	}

	public ComponentList getComponentList() {
		return componentList;
	}

	public void setComponentList(ComponentList componentList) {
		this.componentList = componentList;
	}
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		if(componentList!=null)
		{
			componentList.removeNotify();
		}
		
	}
	
	public JPopupMenu getEdgeMenu(Edge e) 
	{
		setHighlightEdge(e);
		return edgeMenu;
	}
}
