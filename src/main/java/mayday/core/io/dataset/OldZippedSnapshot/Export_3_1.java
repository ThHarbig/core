package mayday.core.io.dataset.OldZippedSnapshot;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import mayday.core.DataSet;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot_v3_1;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class Export_3_1 extends AbstractPlugin implements DatasetFileExportPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.zippedsnapshot.write.3_1",
				new String[0],
				Constants.MC_DATASET_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Writes a zipped Mayday 2.10 snapshot of a Dataset.",
				"Zipped Snapshot Export (Mayday 2.11)"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"maydayZ");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Old Snapshot Format (until Mayday 2.11)");		
		return pli;
	}

	public void init() {}


	public void exportTo(List<DataSet> datasets2, final String file) {
		
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
					Snapshot snap = new Snapshot_v3_1();
					snap.setDataSet(ds);
					
				    shift = (int)((double)count*10000.0*scale);
		            ++count;
					
					snap.setProcessingTask(this);
					snap.write(zout);
					zout.closeEntry();     
				}
				zout.finish();
				zout.close();
			}

			@Override
			protected void initialize() {}
			
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
