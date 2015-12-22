package mayday.vis3.graph.vis3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.ProbeGraphModel;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.gui.actions.ExportPlotAction;
import mayday.vis3.gui.actions.ExportVisibleAreaAction;
import mayday.vis3.gui.menu.MenuManager;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;
import mayday.vis3.plots.profile.ProfilePlotComponent;



@SuppressWarnings("serial")
public class Vis3Component extends MultiProbeComponent implements PlotContainer, ComponentListener, VisualizerMember
{
	private PlotComponent comp;
	private Visualizer visualizer; 
	private MenuManager menuManager;
	private JMenu plotMenu;
	protected JMenuBar menubar = new JMenuBar();
	protected JLabel title;
	
	public Vis3Component(MultiProbeNode node) 
	{
		this(node,new ProfilePlotComponent());
	}
	
	public Vis3Component(MultiProbeNode node, List<ProbeList> probeLists, PlotComponent plot)
	{
		super(node);
		init(probeLists,plot);
	}
	
	public Vis3Component(MultiProbeNode node, PlotComponent c) 
	{
		super(node);
		ProbeList pl=node.getProbeList();
		List<ProbeList> pls=new ArrayList<ProbeList>();
		pls.add(pl);
		init(pls, c);	
	}
	
	private void init(List<ProbeList> probeLists, PlotComponent plot)
	{
		title=new JLabel(getLabel());
		setLabel(getNode().getName());
		setSize(300,300);
		setLayout(new BorderLayout(5,5));
		comp=plot;
		((Component)comp).setPreferredSize(getSize());
		visualizer= new Visualizer(probeLists.get(0).getDataSet(),probeLists);
		visualizer.updateVisualizerMenus();
		viewModel=new ViewModel(visualizer,probeLists.get(0).getDataSet(),probeLists);
		
		JButton closeButton =new JButton(new CloseAction());
		closeButton.setPreferredSize(new Dimension(15, 15));
		closeButton.setBorder(BorderFactory.createLineBorder(Color.black,1));
		menubar.add(closeButton);
		menubar.add(title);
		menubar.add(Box.createHorizontalGlue());
		add(menubar, BorderLayout.NORTH);
				
	
		add(((Component)comp),BorderLayout.CENTER);	
		
		plotMenu=new JMenu("Plot");
		menuManager=new MenuManager(null,this,plotMenu){
			
			protected JMenu makeFileMenu() {
				JMenu plot = new JMenu(PlotContainer.FILE_MENU);
				plot.setMnemonic('P');
				plot.add(new ExportPlotAction(((Component)comp)));
				plot.add(new ExportVisibleAreaAction(((Component)comp)));
				return plot;
			}

		};	
		
		if(viewModel!=null)
		{			
			((PlotComponent)comp).setup(this);
		}
		visualizer.addPlot(this);
		viewModel.addViewModelListener(new ViewModelListener() 
		{			
			@Override
			public void viewModelChanged(ViewModelEvent vme) 
			{
				updateParent();
			}
		});
		JMenu replaceMenu=new JMenu("Replace");
		replaceMenu.add(new ReplaceBySingleNode());
		replaceMenu.add(new ReplaceByNodePerProbeList());
		replaceMenu.add(new ReplaceByNodePerProbe());
		menubar.add(replaceMenu);
	}
	
	@Override
	public void setLabel(String label) 
	{		
		super.setLabel(label);
		if(title!=null)
			title.setText(label);
	}
	
	@Override
	public void paint(Graphics g1) 
	{
		if(isSelected())
			g1.setColor(Color.red);
		else
			g1.setColor(Color.lightGray);
		g1.fillRect(0, 0, getWidth(), getHeight());
		paintChildren(g1);
		
	}

	public void addMenu(JMenu jm) 
	{		
		menuManager.addMenu(jm);
	}

	public JMenu getMenu(String name, PlotComponent askingObject)
	{
		return menuManager.getMenu(name, askingObject);		
	}
	
	

	public void setPreferredTitle(String preferredTitle,
			PlotComponent askingObject) 
	{
		menuManager.setPreferredTitle(preferredTitle, askingObject);
		setLabel(menuManager.getPreferredTitle());
	}

	protected void computeSize() {
		int newWidth = 800;
		int newHeight = 600;

		if (((Component)comp).getPreferredSize()!=null) {
			int minimumNewWidth=500;
			int contentWidth = ((Component)comp).getPreferredSize().width;
			int insetWidth = getInsets().left+getInsets().right;
			newWidth = Math.min(newWidth,
					Math.max(contentWidth+insetWidth, minimumNewWidth));

			int minimumNewHeight=400;
			int contentHeight = ((Component)comp).getPreferredSize().height;
			int insetHeight = getInsets().top+getInsets().bottom;
			newHeight= Math.min(newHeight,
					Math.max(contentHeight+insetHeight, minimumNewHeight));
		}		
		setSize(newWidth, newHeight);
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.components.MultiProbeComponent#setViewModel(mayday.vis3.model.ViewModel)
	 */
	@Override
	public void setViewModel(ViewModel viewModel) 
	{
		super.setViewModel(viewModel);
		if(viewModel!=null)
		{
			((ProfilePlotComponent)comp).setup(this);
			add(((Component)comp));
//			computeSize();
		}
	}
	
	public void setRenderer(ComponentRenderer renderer)
	{
		//do nothing
	}

	public void addViewSetting(Setting s, PlotComponent askingObject) 
	{
		menuManager.addViewSetting(s, askingObject);	
	}
		
	@Override
	public void componentHidden(ComponentEvent e) {}
	
	@Override
	public void componentMoved(ComponentEvent e) {}
	
	@Override
	public void componentResized(ComponentEvent e) 
	{
		((Component)comp).setPreferredSize(getSize());
	}
	
	@Override
	public void componentShown(ComponentEvent e) {}
	
	@Override
	protected JPopupMenu setCustomMenu(JPopupMenu menu) 
	{
		return super.setCustomMenu(menu);
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		// build the menu only once		
		buildMenu();
	}
	
	protected void buildMenu() 
	{
		menubar.add(plotMenu);	
	}
	
	private class CloseAction extends AbstractAction
	{
		public CloseAction() 
		{
			super("X");			
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			GraphCanvas canvas=(GraphCanvas)getParent();
			canvas.getModel().remove(Vis3Component.this);			
		}
	}
	
	private class ReplaceBySingleNode extends AbstractAction
	{
		public ReplaceBySingleNode() 
		{
			super("Single Node");			
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			// get the complete probeList of this component
			ProbeList pl=((MultiProbeNode)getNode()).getProbeList();
			pl.setName(getLabel());
			GraphCanvas canvas=(GraphCanvas)getParent();
			MultiProbeComponent comp=((ProbeGraphModel)canvas.getModel()).addProbeListNode(pl);
			comp.setLocation(getX(), getY());
			canvas.getModel().remove(Vis3Component.this);		
			
		}
	}
	
	private class ReplaceByNodePerProbeList extends AbstractAction
	{
		public ReplaceByNodePerProbeList() 
		{
			super("One Node per ProbeList");			
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			GraphCanvas canvas=(GraphCanvas)getParent();
			int i=0;
			for(ProbeList pl: viewModel.getProbeLists(false))
			{
				MultiProbeComponent comp=((ProbeGraphModel)canvas.getModel()).addProbeListNode(pl);
				comp.setLocation(getX()+(i*10), getY()+(i*10));
				canvas.getModel().remove(Vis3Component.this);
				++i;
			}
			canvas.getModel().remove(Vis3Component.this);	
		}
	}
	private class ReplaceByNodePerProbe extends AbstractAction
	{
		public ReplaceByNodePerProbe() 
		{
			super("One Node per Probe");			
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			GraphCanvas canvas=(GraphCanvas)getParent();
			int i=0;
			ProbeList pl=((MultiProbeNode)getNode()).getProbeList();
			List<CanvasComponent> comps=((ProbeGraphModel)canvas.getModel()).addProbes(pl);
			for(CanvasComponent cc: comps )
			{
				cc.setLocation(getX()+(i*5), getY()+(i*5));
				++i;
			}
			canvas.getModel().remove(Vis3Component.this);
		}		
	}
	
	@Override
	public ViewModel getViewModel() 
	{
		return viewModel;
	}

	@Override
	public void closePlot() {}
	@Override
	public void toFront() {}

	@Override
	public JMenu getVisualizerMenu() 
	{
		return getMenu("Visualizer",null);
	}

	@Override
	public void setTitle(String title) {}
	
	public String getPreferredTitle() {
		return title.getText();
	}

	public String getTitle() {
		return title.getText();
	}
	
	
	
}
