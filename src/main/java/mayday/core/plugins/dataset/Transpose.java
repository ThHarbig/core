package mayday.core.plugins.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.tasks.AbstractTask;

public class Transpose extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DataSetTranspose",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Transposes the expression matrix",
				"DataSet Transpose"
				);
		pli.setMenuName("Transpose Matrix");
		pli.addCategory("Transform");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {

		AbstractTask[] ats = new AbstractTask[datasets.size()];
		final List<DataSet> retval = Collections.synchronizedList(new LinkedList<DataSet>());
		int at_index=0;
		
		for (final DataSet ods : datasets) {		
		
			ats[at_index] = new AbstractTask("Transposing Matrix") {

				@Override
				protected void doWork() throws Exception {
					DataSet ds = new DataSet(ods.getName()+" - transposed");		
					ds.getAnnotation().setInfo("This dataset was created by transposing the dataset \""+ods.getName()+"\"");

					ds.getMasterTable().setNumberOfExperiments(ods.getMasterTable().getNumberOfProbes());
					ArrayList<Probe> pbs = new ArrayList<Probe>();

					setProgress(0, "Creating Matrix");
					int numOfExp = ods.getMasterTable().getNumberOfExperiments();

					for (int i=0; i!=numOfExp; ++i) {
						Probe pb = new Probe(ds.getMasterTable());
						String ExpName = ods.getMasterTable().getExperimentName(i);
						if (ExpName==null)
							ExpName=""+i;
						pb.setName(ExpName);
						pbs.add(pb);
						setProgress((3000*i)/numOfExp);
					}

					setProgress(3000, "Copying Data");
					numOfExp = ds.getMasterTable().getNumberOfExperiments();  // now use the new number of exps.
					int experiment=0;
					for (Object opb : ods.getMasterTable().getProbes().values()) {
						Probe pb = (Probe)opb;
						ds.getMasterTable().setExperimentName(experiment, pb.getName());
						for (int j=0; j!=pbs.size(); ++j) {
							pbs.get(j).addExperiment(pb.getValue(j));
						}
						experiment++;
						setProgress(3000+((6500*experiment)/numOfExp));
					}

					setProgress(9500, "Finishing...");
					for (Probe pb : pbs)
						ds.getMasterTable().addProbe(pb);
					setProgress(10000);

					retval.add(ds);

				}

				protected void initialize() {}

			};
			ats[at_index].start();
			at_index++;
		}
		
		for (AbstractTask at : ats)
			at.waitFor();
			
        return retval;
    }


}
