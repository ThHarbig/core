package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.ProbeListListener;
import mayday.core.StoreListener;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ContainedInPLotherDSbyDisplayName extends ContainedInPLotherDS
implements StorageNodeStorable, OptionPanelProvider, ProbeListListener, StoreListener
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.alignedDS.dpl.ContainedInPLOtherDataSetbyDisplayName",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Contained in Probe List of another DataSet (by display name)",
				"Contained in Probe List of another DataSet (by display name)"
		);
		return pli;
	}

	@Override
	protected Boolean convert(Probe value) {
		return (probeList==null || probeList.contains(value.getDisplayName())); 
	}
	
}



