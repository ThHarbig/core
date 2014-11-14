package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
public class ValueContainedInMIO extends AbstractDataProcessor<Object, Boolean> 
implements OptionPanelProvider, StorageNodeStorable
{
	
	private MIGroup miGroup;
	private JTextField selectedMG = new JTextField(30);
	private boolean silent=false;
	private AbstractAction selectAction = new AbstractAction("Select") {
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
	
	@Override
	public Class<?>[] getDataClass() {
		checkMIGroup();
		return (miGroup!=null?new Class[]{Boolean.class}:null);
	}

	@Override
	public String toString() {
		checkMIGroup();
		return (miGroup==null?"unfinished":"value contained in "+miGroup.getName());
	}

	public void composeOptionPanel(JPanel optionPanel) {
		silent=true;
		selectedMG.setEditable(false);
		JButton selectMG = new JButton(selectAction);
		optionPanel.add(selectedMG);
		optionPanel.add(selectMG);
		if (miGroup!=null)
			selectedMG.setText(miGroup.getPath()+"/"+miGroup.getName());
		else
			selectAction.actionPerformed(null);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.ValueMIOContained",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Value contained in MIO Group",
				"Value contained in meta-information group"
		);
		return pli;
	}

	private HashSet<Object> miVals;

	protected void checkMIGroup() {
		if (getDynamicProbeList().getDataSet().getMIManager().getGroupID(miGroup)==-1)
			setMIGroup(null);
	}
	
	@SuppressWarnings("unchecked")
	protected boolean isContained(MIGroup mg, Object value) {
		if (miVals == null) {
			miVals = new HashSet<Object>();
			for (Entry<Object, MIType> e : mg.getMIOs()) {
				MIType mt = e.getValue();
				if (mt instanceof GenericMIO) {
					GenericMIO gm = (GenericMIO)mt;
					Object v = gm.getValue();
					miVals.add(v);
				}
			}
		}
		return miVals.contains(value);
	}
	
	@Override
	protected Boolean convert(Object value) {
		checkMIGroup();
		return (miGroup==null || isContained(miGroup, value));
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
		MIGroupSelection<MIType> mgs = getDynamicProbeList().getDataSet().getMIManager().getGroupsForPath(path, false);
		mgs = mgs.filterByName(name);
		if (mgs.size()>0) {
			miGroup = mgs.get(0);
			selectedMG.setText(miGroup.getName());
			fireChanged();
		}
	}

	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("ValueContainedInMIO","");
		String path="";
		String name="";
		checkMIGroup();
		if (miGroup!=null) { 
			path = getDynamicProbeList().getDataSet().getMIManager().getTreeRoot().getPathFor(miGroup);
			name = miGroup.getName();
		}
		parent.addChild("Path", path);
		parent.addChild("Name", name);
		return parent;
	}

	
}

