package mayday.core.io.dataset.tabular;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class TabularExport extends AbstractPlugin implements DatasetFileExportPlugin {

	public void init() {}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.io.TabularExport",
				new String[0],
				Constants.MC_DATASET_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Saves a DataSet in tabular format (TSV).",
				"Tabular Export"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION, "csv");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION, "Tabular Text file");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION, "Tabular Export");
		return pli;
	}

	public void exportTo(List<DataSet> datasets, final String fileName) {
		exportTo(datasets, fileName, true);
	}
		
	public AbstractTask exportTo(List<DataSet> datasets, final String fileName, boolean returnTask) {
		
		final DataSet ds = datasets.get(0);
		
		AbstractTask fileExport = new AbstractTask("Tabular Export") {

			@Override
			protected void doWork() throws Exception {
								
				setProgress(-1);

				BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
				
				// Enumerate MIO groups
				MIGroupSelection<MIType> migroups = new MIGroupSelection<MIType>();
				
				for (MIGroup mg : ds.getMIManager().getGroups()) {
					for (Object o : mg.getObjects())
						if (o instanceof Probe) {
							migroups.add(mg);
							break;
						}
				}				
				
				// Write header. Cols are: Experiments, ProbeLists, MIOs
				for (int i = 0; i!=ds.getMasterTable().getNumberOfExperiments(); ++i)
					bw.write((i!=0?"\t":"") + ds.getMasterTable().getExperimentName(i));
				for (ProbeList pl : ds.getProbeListManager().getProbeLists())
					bw.write("\t"+pl.getName());								
				for (MIGroup mg : migroups)
					bw.write("\t"+mg.getName());	
				bw.write("\n");

				if (this.hasBeenCancelled()) return;
				
				setProgress(0);
				
				// write probes with mios
				int nop = ds.getMasterTable().getNumberOfProbes();
				int noe = ds.getMasterTable().getNumberOfExperiments();
				double step = 10000.0/nop;
				int i = 0;				
				
				for (Probe p : ds.getMasterTable().getProbes().values()) {
					bw.write(p.getName());
					for (int j=0; j!=noe; ++j) {
						Double v = p.getValue(j);
						bw.write("\t"+(v==null?"":v));
					}
					for (ProbeList pl : ds.getProbeListManager().getProbeLists())
						bw.write("\t"+((p.getProbeLists().contains(pl))?"x":""));
					for (MIGroup mg : migroups) {
						MIType mit = mg.getMIO(p);
						String micontent ="";
						if (mit!=null) { 
						  micontent = mit.serialize(MIType.SERIAL_TEXT);
						  if (micontent.contains("\t"))
							  micontent = "\""+micontent+"\"";
						}
						bw.write("\t"+micontent);
					}
					bw.write("\n");
					if (this.hasBeenCancelled()) return;
					++i;
					setProgress((int)(step*i));
				}
				
				bw.flush();
				bw.close();
				
			}
			
			

			protected void initialize() {
			}
			
		};
		fileExport.start();
		return fileExport;
	}
	
}
