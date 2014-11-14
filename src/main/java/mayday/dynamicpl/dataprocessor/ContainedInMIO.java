package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.Probe;
import mayday.core.io.StorageNode;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ContainedInMIO extends AbstractDataProcessor<Probe, Boolean> 
implements OptionPanelProvider, StorageNodeStorable
{
	
	private MIGroup miGroup;
	private JTextField selectedMG = new JTextField(30);;

	@Override
	public Class<?>[] getDataClass() {
		checkMIGroup();
		return (miGroup!=null?new Class[]{Boolean.class}:null);
	}

	@Override
	public String toString() {
		checkMIGroup();
		return (miGroup==null?"unfinished":"contained in "+miGroup.getName());
	}

	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {
		selectedMG.setEditable(false);
		if (miGroup!=null)
			selectedMG.setText(miGroup.getName());
		JButton selectMG = new JButton(new AbstractAction("Select") {
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
				
				fireChanged();
			}
		});
		optionPanel.add(selectedMG);
		optionPanel.add(selectMG);
	}
		
	
	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.MIOContained",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Contained in MIO Group",
				"Contained in meta-information Group"
		);
		return pli;
	}

	@Override
	protected Boolean convert(Probe value) {
		checkMIGroup();
		return (miGroup==null || miGroup.contains(value));
	}
	
	protected void checkMIGroup() {
		if (getDynamicProbeList().getDataSet().getMIManager().getGroupID(miGroup)==-1)
			setMIGroup(null);
	}
	

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Probe.class.isAssignableFrom(inputClass[0]);
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
			setMIGroup(mgs.get(0));
		}
	}
	
	protected void setMIGroup( MIGroup mg ) {
		boolean changed = (miGroup != mg);
		miGroup = mg;
		if (mg == null) {
			selectedMG.setText("-- nothing selected --");
		} else {
			selectedMG.setText(miGroup.getPath()+"/"+miGroup.getName());
		}
		if (changed) 
			fireChanged();
	}

	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("ContainedInMIO","");
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

