package mayday.mpf.plumawrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.SurrogatePlugin;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.mpf.Applicator;
import mayday.mpf.FilterBase;
import mayday.mpf.MaydayDataObject;

public abstract class WrappedMPFModule<T> extends AbstractPlugin implements SurrogatePlugin<T>, ProbelistPlugin {

	public void init() {}

	public abstract FilterBase getMPFModule();

	@SuppressWarnings("deprecation")
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		DataSet ds = masterTable.getDataSet();

		// convert input
		Vector<MaydayDataObject> Input = new Vector<MaydayDataObject>();
		for (ProbeList pl : probeLists)
			Input.add(new MaydayDataObject(pl));

		FilterBase theFilter = getMPFModule();

		if (theFilter.InputSize>Input.size())
			throw new RuntimeException("This module requires at least "+theFilter.InputSize+" input probe lists." );

		// OK, prepare the module and fire up Applicator step 2
		Applicator app = new Applicator(ds.getMasterTable(), theFilter, Input);

		JFrame modalParent = Mayday.sharedInstance;

		app.showModal(modalParent);

		ArrayList<ProbeList> results = new ArrayList<ProbeList>();
		for (MaydayDataObject mdo : app.OutputDataSets)
			results.add(mdo.getProbeList());

		return results;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		// never called
		return null;
	}


}