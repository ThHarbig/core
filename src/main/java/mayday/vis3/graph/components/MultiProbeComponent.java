package mayday.vis3.graph.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.vis3.graph.renderer.primary.NoteRenderer;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class MultiProbeComponent extends NodeComponent
{
	protected ViewModel viewModel;
	private  List<Probe> displayProbes;
	private ProbeDisplayMode displayMode;
	
	
	public MultiProbeComponent(Node node) 
	{
		super(node);
		setSize(100, 50);
		displayProbes=new ArrayList<Probe>();
		displayMode=ProbeDisplayMode.ALL;
	}
	
	public MultiProbeComponent(MultiProbeNode node) 
	{
		super(node);
		setSize(100, 50);
		displayProbes=new ArrayList<Probe>(node.getProbes());
		displayMode=ProbeDisplayMode.ALL;
	}
	
	public List<Probe> getProbes()
	{
		return ((MultiProbeNode)getNode()).getProbes();	
	}
	
	public Probe getFirstProbe()
	{
		return ((MultiProbeNode)getNode()).getProbes().get(0);
	
	}
	
	public void paint(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		if(getNode().getRole().equals(Nodes.Roles.NOTE_ROLE))
		{
			new NoteRenderer().draw(g, getNode(),  new Rectangle(getSize()), displayProbes, labelComponent==null?getLabel():"", isSelected());
			return;
		}		
		renderer.draw(g, getNode(),  new Rectangle(getSize()), displayProbes, labelComponent==null?getLabel():"", isSelected());
	}
	
	protected JPopupMenu setCustomMenu(JPopupMenu menu)
	{
		menu=super.setCustomMenu(menu);
		
		menu.add(new ExtractProbeListAction());
		JMenu display=new JMenu("Display");
		ButtonGroup displayGroup=new ButtonGroup();

		JRadioButtonMenuItem allProbes= new JRadioButtonMenuItem(new ProbeDisplayModeAction(ProbeDisplayMode.ALL));
		displayGroup.add(allProbes);
		display.add(allProbes);

		JRadioButtonMenuItem meanProbe= new JRadioButtonMenuItem(new ProbeDisplayModeAction(ProbeDisplayMode.MEAN));
		displayGroup.add(meanProbe);
		display.add(meanProbe);
		
		JRadioButtonMenuItem medianProbe= new JRadioButtonMenuItem(new ProbeDisplayModeAction(ProbeDisplayMode.MEDIAN));
		displayGroup.add(medianProbe);
		display.add(medianProbe);
		
		JRadioButtonMenuItem qProbes= new JRadioButtonMenuItem(new ProbeDisplayModeAction(ProbeDisplayMode.QUARTILES));
		displayGroup.add(qProbes);
		display.add(qProbes);
		
		switch(displayMode){
			case ALL: allProbes.setSelected(true); break;
			case MEAN: meanProbe.setSelected(true); break;
			case MEDIAN: medianProbe.setSelected(true); break;
			case QUARTILES: qProbes.setSelected(true); break;
		}
		
		menu.add(display);
		if(((MultiProbeNode)getNode()).getProbes().size()!=0 )
		{			
			menu.add(new ProbeMenu(((MultiProbeNode)getNode()).getProbes(),getProbes().get(0).getMasterTable()).getMenu());
		}
		return menu;
	}
	
	/**
	 * @return the displayProbes
	 */
	protected List<Probe> getDisplayProbes() {
		return displayProbes;
	}

	/**
	 * @return the viewModel
	 */
	public ViewModel getViewModel() {
		return viewModel;
	}

	/**
	 * @param viewModel the viewModel to set
	 */
	public void setViewModel(ViewModel viewModel) {
		this.viewModel = viewModel;
	}
	
	private class ProbeDisplayModeAction  extends AbstractAction
	{
		private ProbeDisplayMode mode;
				
		private ProbeDisplayModeAction(ProbeDisplayMode mode) {
			super(mode.getName());
			this.mode = mode;
			
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			setDisplayMode(mode);
		}
		
	}

	private class ExtractProbeListAction extends AbstractAction
	{
		public ExtractProbeListAction()
		{
			super("Create ProbeList from this Node");			
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			if(getProbes().isEmpty()) // need at least 1 probe to export! 
				return;
			
			ProbeList plist=new ProbeList(getProbes().get(0).getMasterTable().getDataSet(),true);
			plist.setName(getLabel());
			for(Probe p:((MultiProbeNode)getNode()).getProbes())
			{
				plist.addProbe(p);
			}
			getProbes().get(0).getMasterTable().getDataSet().getProbeListManager().addObject(plist);
		}		
	}
	
	public void clearProbes()
	{
		((MultiProbeNode)getNode()).getProbes().clear();
	}
	
	public void setDisplayMode(ProbeDisplayMode mode) 
	{
		this.displayMode=mode;
		updateDisplayMode();
	}
	
	public void updateDisplayMode()
	{
		displayProbes.clear();
		switch(displayMode)
		{
		case ALL: displayProbes=new ArrayList<Probe>(getProbes());
			break;
		case MEAN: 
				displayProbes.add (((MultiProbeNode)getNode()).getProbeList().getStatistics().getMean());
			break;
		case QUARTILES:
			ProbeList.Statistics s=((MultiProbeNode)getNode()).getProbeList().getStatistics();
			displayProbes.add(s.getQ1());
			displayProbes.add(s.getQ3());
			displayProbes.add(s.getMedian());
			break;
		case MEDIAN:	
			displayProbes.add (((MultiProbeNode)getNode()).getProbeList().getStatistics().getMedian());
		}
		updateParentLocal();	
	}

	public void addProbe(Probe p)
	{
		MultiProbeNode mpn=(MultiProbeNode)getNode();
		if(!mpn.getProbes().contains(p))
		{
			mpn.addProbe(p);
		}
		updateDisplayMode();
	}
	
	public void setProbes(Collection<Probe> probes)
	{
		MultiProbeNode mpn=(MultiProbeNode)getNode();
		mpn.setProbes(new ArrayList<Probe>(probes));
		updateDisplayMode();
	}
	
	public enum ProbeDisplayMode
	{
		ALL("All Probes"),
		MEAN("Mean"),
		MEDIAN("Median"),
		QUARTILES("Quartiles");
		
		private String name;

		private ProbeDisplayMode(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}		
	}
	
}
