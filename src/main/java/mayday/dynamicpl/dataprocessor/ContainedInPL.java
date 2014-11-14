package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ContainedInPL extends AbstractDataProcessor<Probe, Boolean> 
implements StorageNodeStorable, OptionPanelProvider, ProbeListListener
{

	private ProbeList probeList;
	private JTextField selectedPL = new JTextField(30);
	private boolean silent=false;
	private AbstractAction selectAction; 

	@Override
	public Class<?>[] getDataClass() {
		return probeList==null?null:new Class[]{Boolean.class};
	}

	@Override
	public String toString() {
		return (probeList==null?"unfinished":"contained in "+probeList.getName());
	}

	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {
		silent=true;
		selectedPL.setEditable(false);
		JButton selectPL = new JButton(selectAction = new AbstractAction("Select") {
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
		});
		optionPanel.add(selectedPL);
		optionPanel.add(selectPL);
		if (probeList!=null)
			selectedPL.setText(probeList.getName());
		else
			selectAction.actionPerformed(null);
		silent=false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.ProbeListContained",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Contained in Probe List",
				"Contained in Probe List"
		);
		return pli;
	}

	@Override
	protected Boolean convert(Probe value) {
		return (probeList==null || probeList.contains(value)); 
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Probe.class.isAssignableFrom(inputClass[0]);
	}

	public StorageNode toStorageNode() {
		return new StorageNode("ProbeList",probeList!=null?probeList.getName():"null");
	}

	public void fromStorageNode(StorageNode sn) {
		String s = sn.Value;
		probeList = null;
		if (!s.equals("null")) {
			ProbeList pl = getDynamicProbeList().getDataSet().getProbeListManager().getProbeList(s);
			setProbeList(pl);
		}	
	}

	public JPanel getOptionPanel() {
		JPanel op = new JPanel();
		composeOptionPanel(op);
		return op;
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
		if (!silent)
			fireChanged();
	}

	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.CONTENT_CHANGE)
			fireChanged();
		else if (event.getChange()==ProbeListEvent.PROBELIST_CLOSED)
			setProbeList(null);
	}

	public void dispose() {
		setProbeList(null);
	}
	
}



