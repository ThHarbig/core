package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.dataset.DataSetSelectionDialog;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ContainedInPLotherDS extends AbstractDataProcessor<Probe, Boolean> 
implements StorageNodeStorable, OptionPanelProvider, ProbeListListener, StoreListener
{

	protected ProbeList probeList;
	protected DataSet dataSet;
	protected String waitingForDataSet = null;
	protected String waitingForProbeList = null;
	protected JTextField selectedDS = new JTextField(30);		
	protected JTextField selectedPL = new JTextField(30);		

	@Override
	public Class<?>[] getDataClass() {
		return probeList==null?null:new Class[]{Boolean.class};
	}

	@Override
	public String toString() {
		return (probeList==null||dataSet==null?"unfinished":"contained in "+dataSet.getName()+", "+probeList.getName());
	}

	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		// dataset
		JPanel dataSetPanel = new JPanel();
		selectedDS.setEditable(false);
		if (dataSet!=null)
			selectedDS.setText(dataSet.getName());
		JButton selectDS = new JButton(new AbstractAction("Select") {
			public void actionPerformed(ActionEvent e) {
				DataSetSelectionDialog plsd = new DataSetSelectionDialog();
				plsd.setModal(true);
				plsd.setVisible(true);
				List<DataSet> mgs = plsd.getSelection();
				if (mgs.size()>0) {
					setDataSet(mgs.get(0));
				} else {
					setDataSet(null);
				}
				fireChanged();
			}
		});
		dataSetPanel.add(selectedDS);
		dataSetPanel.add(selectDS);
		optionPanel.add(dataSetPanel);		
		// probelist
		JPanel probeListPanel = new JPanel();
		selectedPL.setEditable(false);
		if (probeList!=null)
			selectedPL.setText(probeList.getName());
		JButton selectPL = new JButton(new AbstractAction("Select") {
			public void actionPerformed(ActionEvent e) {
				if (dataSet==null) 
					return;
				ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(
						dataSet.getProbeListManager()
				);
				plsd.setModal(true);
				plsd.setVisible(true);
				List<ProbeList> mgs = plsd.getSelection();
				if (mgs.size()>0) {
					setProbeList(mgs.get(0));
				} else {
					setProbeList(null);
				}
				fireChanged();
			}
		});
		probeListPanel.add(selectedPL);
		probeListPanel.add(selectPL);
		optionPanel.add(probeListPanel);
	}


	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.alignedDS.dpl.ContainedInPLOtherDataSet",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Contained in Probe List of another DataSet (by name)",
				"Contained in Probe List of another DataSet (by name)"
		);
		return pli;
	}

	@Override
	protected Boolean convert(Probe value) {
		return (probeList==null || probeList.contains(value.getName())); 
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Probe.class.isAssignableFrom(inputClass[0]);
	}

	public StorageNode toStorageNode() {
		StorageNode sn = new StorageNode("Settings","");
		sn.addChild(new StorageNode("DataSet",dataSet!=null?dataSet.getName():"null"));
		sn.addChild(new StorageNode("ProbeList",probeList!=null?probeList.getName():"null"));
		return sn;
	}
	
	protected void findDataSet(String dsName) {
		waitingForDataSet = dsName;
		for (DataSet ds : DataSetManager.singleInstance.getDataSets()) {
			if (ds.getName().equals(dsName)) {
				dataSet=ds;
				waitingForDataSet = null;
				break;
			}
		}
	}

	protected void findProbeList(String plName) {
		waitingForProbeList = plName;
		if (dataSet!=null) {
			ProbeList pl = dataSet.getProbeListManager().getProbeList(waitingForProbeList);
			if (pl!=null) {
				probeList=pl;
				waitingForProbeList = null;
			}					
		}
	}
	
	public void fromStorageNode(StorageNode sn) {
		dataSet = null;
		probeList = null;
		StorageNode snc = sn.getChild("DataSet");
		if (snc!=null) {
			findDataSet(snc.Value);
		}
		snc = sn.getChild("ProbeList");			
		if (snc!=null) {
			findProbeList(snc.Value);
			setProbeList(probeList);
		}
		if (waitingForProbeList!=null) {
			registerCallBacks();
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
		fireChanged();
	}

	public void setDataSet(DataSet ds) {
		dataSet=ds;
		if (ds!=null) {
			selectedDS.setText(ds.getName());
		}else {
			selectedDS.setText("-- nothing selected --");
			setProbeList(null);
		}
		fireChanged();
	}

	
	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.CONTENT_CHANGE)
			fireChanged();
		else if (event.getChange()==ProbeListEvent.PROBELIST_CLOSED) {
			waitingForProbeList = probeList.getName(); // wait for its return
			setProbeList(null);
			unRegisterCallBacks();
			registerCallBacks();
		}
	}

	
	protected void registerCallBacks() {
		if (waitingForDataSet!=null)
			DataSetManager.singleInstance.addStoreListener(this);
		else {
			if (waitingForProbeList!=null)
				dataSet.getProbeListManager().addStoreListener(this);
		}
	}

	protected void unRegisterCallBacks() {
		DataSetManager.singleInstance.removeStoreListener(this);
		if (dataSet!=null)
			dataSet.getProbeListManager().removeStoreListener(this);
	}
	
	public void objectAdded(StoreEvent event) {
		if (event.getObject() instanceof DataSet && waitingForDataSet!=null) {
			DataSet ds = ((DataSet)event.getObject());
			if (ds.getName().equals(waitingForDataSet)) {
				setDataSet(ds);
				waitingForDataSet=null;				
				findProbeList(waitingForProbeList);
				if  (waitingForProbeList==null)
					fireChanged();
			}
		} else if (event.getObject() instanceof ProbeList && waitingForProbeList!=null) {
			ProbeList pl = ((ProbeList)event.getObject());
			if (pl.getName().equals(waitingForProbeList)) {
				setProbeList(pl);
				waitingForProbeList=null;
			}
		}
		unRegisterCallBacks();
		registerCallBacks();
	}

	public void objectRemoved(StoreEvent event) {
		// ProbeList closing is handled automatically
		if (event.getObject() instanceof DataSet) {
			DataSet ds = ((DataSet)event.getObject());
			if (ds==dataSet) {
				waitingForDataSet=ds.getName();
				setDataSet(null);
				unRegisterCallBacks();
				registerCallBacks();
			}
		}
	}
	
	public void dispose() {
		setProbeList(null);
		unRegisterCallBacks();
	}

}



