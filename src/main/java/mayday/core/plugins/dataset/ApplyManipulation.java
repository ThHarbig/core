package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ApplicableFunction;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.manipulators.None;

public class ApplyManipulation extends AbstractPlugin implements DatasetPlugin, ApplicableFunction {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dataset.applyManipulation",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Applies a vis3 manipulation to a dataset to create a derived dataset",
				"Apply Data Manipulation"
		);
		pli.addCategory("Transform");
		return pli;
	}

	@SuppressWarnings("deprecation")
	public List<DataSet> run(final List<DataSet> datasets) {
		
		final PluginTypeSetting<ManipulationMethod> pts = new PluginTypeSetting<ManipulationMethod>(
				"Manipulation Method", 
				"Select how the data should be transformed",
				new None(),
				ManipulationMethod.MC
		);
		
		SettingDialog sd = new SettingDialog(null, "Apply Manipulation", pts);
		sd.showAsInputDialog();
		
		final List<DataSet> retval = new LinkedList<DataSet>();
		
		if (!sd.canceled()) {
			AbstractTask manip = new AbstractTask("Manipulating Data") {
			
				protected void initialize() {}
			
				protected void doWork() throws Exception {
					int count=0, total=0;
					for (DataSet ds : datasets) {
						total+=ds.getMasterTable().getNumberOfProbes();
					}
					
					ManipulationMethod mm = pts.getInstance();
					for (DataSet inds: datasets) {
						DataSet outds = new DataSet(inds.getName()+" - "+mm.toString());
						MasterTable mata = outds.getMasterTable();
						mata.setNumberOfExperiments(inds.getMasterTable().getNumberOfExperiments());
						mata.setExperimentNames(inds.getMasterTable().getExperimentNames());
						for (Probe pb : inds.getMasterTable().getProbes().values()) {
							Probe pb2 = new Probe(mata);
							pb2.setName(pb.getName());
							pb2.setValues(mm.manipulate(pb.getValues()));
							mata.addProbe(pb2);
							setProgress((++count*10000)/total);
						}
						retval.add(outds);
					}
				}
			};
			manip.run();			
		}
		
		

		return retval;
	}


	@Override
	public boolean isApplicable(Object... o) {
		return (o.length>0);
	}
	

}
