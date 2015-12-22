/*
 * Created on Jan 24, 2005
 *
 */
package mayday.core.plugins.mio;

import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class MergeGroups
extends AbstractMetaInfoPlugin
{

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(MIType.class);
	}


	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		
		if (selection.size()<2)
			throw new RuntimeException("Please select more than one group for merging.");
		
		String miType = null;
		for (MIGroup mg : selection) {
			if (miType == null)
				miType = mg.getMIOType();
			else if (!miType.equals(mg.getMIOType()))
				throw new RuntimeException("Can only merge MI groups of identical type");
		}
		
		MIGroup tgt = miManager.newGroup(miType, selection.iterator().next().getName()+" (merged)", selection.iterator().next().getPath());
			
		for (MIGroup mg : selection) {
			for (Entry<Object, MIType> e : mg.getMIOs()) {
				tgt.add(e.getKey(), e.getValue());
			}
		}

	}

	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.mergegroups",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Merges several MI groups of the same type",
		"Merge groups");
		return pli;
	}

}
