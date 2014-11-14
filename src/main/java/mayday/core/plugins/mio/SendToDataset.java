/*
 * Created on Jan 24, 2005
 *
 */
package mayday.core.plugins.mio;

import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dataset.DataSetSelectionDialog;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class SendToDataset
extends AbstractMetaInfoPlugin
{

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(MIType.class);
	}


	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		DataSetSelectionDialog dssd = new DataSetSelectionDialog();
		dssd.setDialogDescription("Please select the target DataSet(s)");
		dssd.setModal(true);
		dssd.setVisible(true);		
		for (DataSet targetDS : dssd.getSelection()) {
			for (MIGroup mg : selection) {
				MIManager targetManager = targetDS.getMIManager();
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
						tkey = targetDS.getMasterTable().getProbe(((Probe)e.getKey()).getName());
					} else if (e.getKey() instanceof ProbeList) {
						tkey = targetDS.getProbeListManager().getProbeList(((ProbeList)e.getKey()).getName());
					} else if (e.getKey() instanceof Experiment) {
						tkey = targetDS.getMasterTable().getExperiment(((Experiment)e.getKey()).getName());
					} else if (e.getKey()==miManager.getDataSet()) {
						tkey = targetDS;
					}
					if (tkey != null)
						targetGroup.add(tkey, target);
				}
			}
		}
	}

	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.sendtodataset",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Sends a MIO group to another Dataset",
		"Send to DataSet");
		return pli;
	}

}
