package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;

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
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ComplementaryProbe extends AbstractDataProcessor<Probe, Probe> 
implements StorageNodeStorable, OptionPanelProvider
{
	
	private MIGroup miGroup;
	private JTextField selectedMG = new JTextField(30);

	@Override
	public Class<?>[] getDataClass() {
		if (miGroup==null)
			return null;	
		return new Class[]{Probe.class};
	}
	
	@Override
	public Probe convert(Probe pb) {
		Probe complement = null;
	    StringMIO sm = ((StringMIO)miGroup.getMIO(pb));	    
		if (sm!=null) {
			String name = sm.getValue();
			complement = getDynamicProbeList().getDataSet().getMasterTable().getProbe(name);
		}
		return complement;
	}

	@Override
	public String toString() {
		return (miGroup==null?"":"Complementary probe ");
	}

	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {		
		selectedMG.setEditable(false);
		JButton selectMG = new JButton(new AbstractAction("Select") {
			public void actionPerformed(ActionEvent e) {
				MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(
						getDynamicProbeList().getDataSet().getMIManager(),
						GenericMIO.class
						);
				mgsd.setModal(true);
				mgsd.setVisible(true);
				MIGroupSelection<MIType> mgs = mgsd.getSelection();
				if (mgs.size()>0)
					miGroup = mgs.get(0);
				selectedMG.setText(miGroup.getName());
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
				"PAS.dynamicPL.source.ComplementaryProbe",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Returns the complementary probe defined by a StringMIO mapping ",
				"Complementary Probe"
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
			miGroup = mgs.get(0);
			selectedMG.setText(miGroup.getName());
			fireChanged();
		}
	}

	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("ComplementaryProbe","");
		String path="";
		String name="";
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

