package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.io.StorageNode;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

@SuppressWarnings("serial")
public class ValueContainedInListMIOSubset extends AbstractDataProcessor<Object, Boolean> 
implements OptionPanelProvider, StorageNodeStorable, ProbeListListener
{
	
	private MIGroup miGroup;
	private ProbeList probeList;

	private JTextField selectedMG = new JTextField(30);
	private JTextField selectedPL = new JTextField(20);
	
	private boolean silent=false;
	private AbstractAction selectActionMG = new AbstractAction("Select") {
		public void actionPerformed(ActionEvent e) {
			MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(
					getDynamicProbeList().getDataSet().getMIManager()
					);
			mgsd.setModal(true);
			mgsd.setVisible(true);
			MIGroupSelection<MIType> mgs = mgsd.getSelection();
			if (mgs.size()>0) {
				setMIGroup(mgs.get(0));
			} else {
				setMIGroup(null);
			}				
		
		}
	}; 
	private AbstractAction selectActionPL = new AbstractAction("Select") {
		public void actionPerformed(ActionEvent e) {
			ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(
					getDynamicProbeList().getDataSet().getProbeListManager()
			);
			plsd.setModal(true);
			plsd.setVisible(true);
			List<ProbeList> mgs = plsd.getSelection();
			if (mgs.size()>0) {
				setProbeList(mgs.get(0));
			} else {
				setProbeList(null);
			}
		}
	}; 
	
	

	@Override
	public Class<?>[] getDataClass() {
		checkMIGroup();
		return (miGroup!=null?new Class[]{Boolean.class}:null);
	}

	@Override
	public String toString() {
		checkMIGroup();
		return (miGroup==null?"unfinished":"value contained in subset of "+miGroup.getName());
	}

	public void composeOptionPanel(JPanel optionPanel) {
		silent=true;
		optionPanel.setLayout(new ExcellentBoxLayout(true,5));
		
		JPanel p1 = new JPanel();		
		selectedMG.setEditable(false);
		JButton selectMG = new JButton(selectActionMG);
		p1.add(selectedMG);
		p1.add(selectMG);
		optionPanel.add(p1);

		p1 = new JPanel();
		selectedPL.setEditable(false);
		JButton selectPL = new JButton(selectActionPL);
		p1.add(new JLabel("Subset: "));
		p1.add(selectedPL);
		p1.add(selectPL);
		optionPanel.add(p1);
		
		if (miGroup!=null)
			selectedMG.setText(miGroup.getPath()+"/"+miGroup.getName());
		else
			selectActionMG.actionPerformed(null);
		
		if (probeList!=null)
			selectedPL.setText(probeList.getName());
		else
			selectActionPL.actionPerformed(null);
				
		silent=false;
	}
		
	
	protected void setMIGroup( MIGroup mg ) {
		miGroup = mg;
		if (mg == null) {
			selectedMG.setText("-- nothing selected --");
		} else {
			selectedMG.setText(miGroup.getPath()+"/"+miGroup.getName());
		}
		miVals=null;
		if (!silent)
			fireChanged();
	}
	
	public void setProbeList(ProbeList pl) {
		if (probeList!=null)
			probeList.removeProbeListListener(this);		
		probeList=pl;
		if (pl!=null) {
			selectedPL.setText(pl.getName());
			probeList.addProbeListListener(this);
		}else
			selectedPL.setText("-- nothing selected --");
		miVals=null;
		if (!silent)
			fireChanged();
	}
	
	public void dispose() {
		setProbeList(null);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.ValueListMIOPartContained",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke, Alexander Herbig",
				"battke@informatik.uni-tuebingen.de",
				"Value contained in ListMIO Group subset",
				"Value contained in ListMIO Group subset"
		);
		return pli;
	}

	private HashSet<Object> miVals;

	protected void checkMIGroup() {
		if (getDynamicProbeList().getDataSet().getMIManager().getGroupID(miGroup)==-1)
			setMIGroup(null);
	}
	
	@SuppressWarnings("unchecked")
	protected boolean isContained(MIGroup mg, ProbeList pl, Object value) {
		if (miVals == null) {
			miVals = new HashSet<Object>();
			for (Probe pb : pl.toCollection()) {
				MIType mt = mg.getMIO(pb);
				if (mt instanceof GenericMIO) {
					GenericMIO gm = (GenericMIO)mt;
					Object v = gm.getValue();
					miVals.add(v);
					//BEGIN herbig code
					if(v instanceof List)
						miVals.addAll((List)v);
					//END herbig code
				}
			}
		}
		return miVals.contains(value);
	}
	
	@Override
	protected Boolean convert(Object value) {
		checkMIGroup();
		return (miGroup==null || probeList==null || isContained(miGroup, probeList, value));
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Object.class.isAssignableFrom(inputClass[0]);
	}

	public JPanel getOptionPanel() {
		JPanel p = new JPanel();
		composeOptionPanel(p);
		return p;
	}

	public void fromStorageNode(StorageNode storageNode) {
		String path = storageNode.getChild("Path").Value;
		String name = storageNode.getChild("Name").Value;
		String plname = storageNode.getChild("PL").Value;
		MIGroupSelection<MIType> mgs = getDynamicProbeList().getDataSet().getMIManager().getGroupsForPath(path, false);
		mgs = mgs.filterByName(name);
		if (mgs.size()>0) {
			miGroup = mgs.get(0);
			selectedMG.setText(miGroup.getName());
			setProbeList(getDynamicProbeList().getDataSet().getProbeListManager().getProbeList(plname));
		}
	}

	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("ValueContainedInMIO","");
		String path="";
		String name="";
		String plname="";
		checkMIGroup();
		if (miGroup!=null) { 
			path = getDynamicProbeList().getDataSet().getMIManager().getTreeRoot().getPathFor(miGroup);
			name = miGroup.getName();
		}
		if (probeList!=null) {
			plname = probeList.getName();
		}
		parent.addChild("Path", path);
		parent.addChild("Name", name);
		parent.addChild("PL", plname);
		return parent;
	}
	
	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.CONTENT_CHANGE)
			fireChanged();
		else if (event.getChange()==ProbeListEvent.PROBELIST_CLOSED)
			setProbeList(null);
	}
	
}

