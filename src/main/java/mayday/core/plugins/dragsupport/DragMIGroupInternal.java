package mayday.core.plugins.dragsupport;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.ProbeListManager;

public class DragMIGroupInternal extends AbstractPlugin implements
		DragSupportPlugin {

	@Override
	public void init() {
		 try {
			FLAVOR = new DataFlavor("mayday/migroup");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not create dataflavor for mayday migroup d&d support.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.MIGroupInternalFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements internal drag / drop support for migroups",
				"MIGroup internal format"
				);
		return pli;
	}
	
	protected static DataFlavor FLAVOR;
	protected MIManager targetManager;
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return FLAVOR;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{MIGroup.class};
	}

	@Override
	public Object getTransferData(Object... input) {
		return input; // no conversion
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		Object to;
		try {
			to = t.getTransferData(FLAVOR);
			if (to instanceof MIGroup[]) {
				MIGroup[] groups = ((MIGroup[])to);
				
				DataSet targetDS = targetManager.getDataSet();
				ProbeListManager targetPLM = targetDS.getProbeListManager();
				MasterTable targetMT = targetDS.getMasterTable();
				
				for (int i=0; i!=groups.length; ++i) {
					MIGroup mg = groups[i];
					if (mg.getMIManager()==targetManager)
						continue;
					// check if group is in correct mimanager, if not, clone it as best we can
					String mtype = mg.getMIOType();
					MIGroup targetGroup = targetManager.newGroup(mtype, mg.getName(), mg.getPath());
					HashMap<MIType, MIType> newMIOs = new HashMap<MIType, MIType>();
					for (Entry<Object, MIType> e : mg.getMIOs()) {
						MIType source = e.getValue();
						MIType target = newMIOs.get(source);
						if (target==null) {
							newMIOs.put(source, target = MIManager.newMIO(mtype));
							target.deSerialize(MIType.SERIAL_TEXT, source.serialize(MIType.SERIAL_TEXT));
						}
						Object tkey = null;

						if (e.getKey() instanceof Probe) {
							tkey = targetMT.getProbe(((Probe)e.getKey()).getName());
						} else if (e.getKey() instanceof ProbeList) {
							tkey = targetPLM.getProbeList(((ProbeList)e.getKey()).getName());
						} else if (e.getKey() instanceof Experiment) {
							tkey = targetMT.getExperiment(((Experiment)e.getKey()).getName());
						} else if (e.getKey() instanceof DataSet) {
							tkey = targetDS;
						}
						if (tkey != null)
							targetGroup.add(tkey, target);
					}
					groups[i]=targetGroup;
				}
				return (T[])groups;
			}
		} catch (UnsupportedFlavorException e) {
			System.err.println("Could not accept dragged element:");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not accept dragged element:");
			e.printStackTrace();
		}
	
		return (T[])new Object[0];
	}
	
	public void setContext(Object contextObject) {
		targetManager = (MIManager)contextObject;
	}


}
