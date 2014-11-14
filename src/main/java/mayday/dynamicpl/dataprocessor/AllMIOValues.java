package mayday.dynamicpl.dataprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class AllMIOValues extends AbstractDataProcessor<Probe, Collection> {

	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Collection.class, String.class};
	}

	@Override
	public String toString() {
		return "All MIO values";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.AllMIOValues",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract all MIO values for filtering and convert them to Strings",
				"Probe meta information values (all converted to Strings)"
		);
		return pli;
	}

	@Override
	protected Collection convert(Probe value) {
		ArrayList<String> al = new ArrayList<String>();
		MIGroupSelection<MIType> mgs = value.getMasterTable().getDataSet().getMIManager().getGroupsForObject(value);
		for (MIGroup mg : mgs) {
			al.add(((GenericMIO)mg.getMIO(value)).toString());
		}
		return al;
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (Probe.class.isAssignableFrom(inputClass[0]));
	}

}

