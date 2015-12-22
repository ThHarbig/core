package mayday.core.io.dataset.ZippedSnapshot;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import mayday.core.DataSet;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class Export extends AbstractPlugin implements DatasetFileExportPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.zippedsnapshot.write",
				new String[0],
				Constants.MC_DATASET_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Writes a compressed Mayday snapshot of one or more Datasets.",
				"Zipped Snapshot Export"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"maydayZ");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"Mayday Zipped Snapshot Format");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Mayday Snapshot");		
		return pli;
	}

	public void init() {}

	public void exportTo(List<DataSet> datasets2, final String file) {
		exportTo(datasets2, file, false);
	}

	public void exportTo(List<DataSet> datasets2, final String file, final boolean hidden) {
		
		final List<DataSet> datasets = new LinkedList<DataSet>(datasets2); // copy to insulate against comodification
		
		AbstractTask at = new AbstractTask("Writing Snapshot") {

			protected double scale=1.0/datasets.size();
			protected int shift=0;
			
			@Override
			protected void doWork() throws Exception {
				if (datasets.size()==0) {
					throw new RuntimeException("No DataSets selected for export.");
				}
				ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
				
				int count = 0;
				
				for (DataSet ds : datasets) {
					zout.putNextEntry(new ZipEntry(ds.getName()+".dataset"));
					Snapshot snap = Snapshot.getNewestVersion();
					snap.setDataSet(ds);
					
				    shift = (int)((double)count*10000.0*scale);
		            ++count;
					
					snap.setProcessingTask(this);
					snap.setStreamProvider(new ZipStreamProvider(null,zout));
					snap.write(zout);
					zout.closeEntry();     
				}
				zout.finish();
				zout.close();
			}

			@Override
			protected void initialize() {
				isHidden = hidden;
			}
			
			public void setProgress(int percentageX100, String progressInfo) {
				int scaledPercentageX100 = (int)(shift+(percentageX100*scale));
				if (progressInfo==null) {
					progressInfo = scaledPercentageX100/100+"."+scaledPercentageX100 % 100+" %";
				}
				super.setProgress(scaledPercentageX100, progressInfo);		
			}
			
			public void setProgress(int percentageX100) {
				setProgress(percentageX100, null);
			}
		
			
		};
		at.start();
	}
	

}
