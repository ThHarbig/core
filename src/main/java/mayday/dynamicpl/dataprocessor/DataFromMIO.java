package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.Probe;
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

public class DataFromMIO extends AbstractDataProcessor<Probe, Object> 
implements StorageNodeStorable, OptionPanelProvider
{
	
	private MIGroup miGroup;
	private JTextField selectedMG = new JTextField(30);
	private boolean silent=false;
	private AbstractAction selectAction;
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] getDataClass() {
		checkMIGroup();
		if (miGroup==null)
			return null;	
		MIType mt = (MIType)miGroup.getMIOs().iterator().next().getValue();
		if (mt!=null)
			if (mt instanceof GenericMIO)
				return buildClassList(((GenericMIO)mt));
		return null; 
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] buildClassList(GenericMIO mt) {
		LinkedList<Class> res = new LinkedList<Class>();
		Class first = mt.getPayloadClass();
		res.add(first);
		if (Collection.class.isAssignableFrom(first)) {
			// get class of contained elements
			Object contained = ((Collection)mt.getValue()).iterator().next();
			res.add(contained.getClass());
		} else if (Map.class.isAssignableFrom(first)) {
			// get class of key and value
			Object containedKey = ((Map)mt.getValue()).keySet().iterator().next();
			Object containedValue = ((Map)mt.getValue()).values().iterator().next();
			res.add(containedKey.getClass());
			res.add(containedValue.getClass());
		}
		return res.toArray(new Class[0]);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Probe pb) {
		checkMIGroup();
		if (miGroup==null)
			return null;
	    GenericMIO gm = ((GenericMIO)miGroup.getMIO(pb));
		if (gm!=null) 
			return gm.getValue();
	    return null;
	}

	@Override
	public String toString() {
		checkMIGroup();
		return (miGroup==null?"MIO value":miGroup.getName());
	}

	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {		
		silent=true;
		selectedMG.setEditable(false);
		JButton selectMG = new JButton(selectAction = new AbstractAction("Select") {
			public void actionPerformed(ActionEvent e) {
				MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(
						getDynamicProbeList().getDataSet().getMIManager(),
						GenericMIO.class
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
		});
		if (miGroup!=null)
			selectedMG.setText(miGroup.getPath()+"/"+miGroup.getName());
		else
			selectAction.actionPerformed(null);
		optionPanel.add(selectedMG);
		optionPanel.add(selectMG);
		silent=false;
	}
		
	
	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.MIOValue",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract MIO values (from MIOs derived from GenericMIO) for filtering",
				"Meta-information values"
		);
		return pli;
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Probe.class.isAssignableFrom(inputClass[0]);
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

	protected void checkMIGroup() {
		if (getDynamicProbeList().getDataSet().getMIManager().getGroupID(miGroup)==-1)
			setMIGroup(null);
	}
	
	protected void setMIGroup( MIGroup mg ) {
		if (miGroup==mg)
			return;
		miGroup = mg;
		if (mg == null) {
			selectedMG.setText("-- nothing selected --");
		} else {
			selectedMG.setText(miGroup.getPath()+"/"+miGroup.getName());
		}
		if (!silent)
			fireChanged();
	}

	
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("DataFromMIO","");
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

	public JPanel getOptionPanel() {
		JPanel p = new JPanel();
		composeOptionPanel(p);
		return p;
	}

}

