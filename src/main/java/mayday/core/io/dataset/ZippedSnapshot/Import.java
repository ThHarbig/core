package mayday.core.io.dataset.ZippedSnapshot;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class Import extends AbstractPlugin implements DatasetFileImportPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.zippedsnapshot.read",
				new String[0],
				Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads a zipped Mayday snapshot of a Dataset.",
				"Zipped Snapshot Import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"maydayz");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Mayday Zipped Snapshot Format");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Mayday Snapshot");		
		return pli;
	}

	public void init() {}


	
	public List<DataSet> importFrom(final List<String> files) {
		final LinkedList<DataSet> result = new LinkedList<DataSet>();		
		AbstractTask at = new AbstractTask("Reading Snapshot") {
			
			protected double scale=1;
			protected int shift=0;
			
			@SuppressWarnings("unchecked")
			public void doWork() throws Exception {
				for (String file : files) {
					ZipFile zip;
					try {
						zip=new ZipFile(new File(file));
					} catch (Exception e) {
						writeLog("Could not open snapshot file \""+new File(file).getAbsolutePath()+"\". ");
						return;
					}
		        	scale = 1.0/zip.size();
		        	int count=0;
			        for(Enumeration e=zip.entries(); e.hasMoreElements(); )
			        {
			        	ZipEntry entry =(ZipEntry)e.nextElement();
			        	
			            shift = (int)((double)count*10000.0*scale);
			            ++count;

			            if (!entry.getName().endsWith(".dataset"))
			        			continue;
			        	
			            InputStream input = zip.getInputStream(entry);
			            
			            ReadyBufferedReader br = new ReadyBufferedReader(new InputStreamReader(input));
			            
			            Snapshot snap = Snapshot.getCorrectVersion(br);
			            
			            if (snap==null)
			            	throw new Exception("Snapshot format not supported");
			            
			            
			            snap.setProcessingTask(this);
			            snap.setStreamProvider(new ZipStreamProvider(zip,null));
						snap.read(br);
						result.add(snap.getDataSet());
			        }
				};
			}
			public void initialize() {};
			
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
		at.waitFor();
		
		if (at.hasBeenCancelled())
			result.clear();
		
		return result;
	}
	
	public static void loadFilesAtStartup(List<String> filenames) {
		List<DataSet> results = new Import().importFrom(filenames);		
		if (results!=null) {
			for (DataSet ds : results) {
				if (ds!=null) {
					DataSetManagerView.getInstance().addDataSet(ds);
		    		if (ds.getProbeListManager().getNumberOfObjects()==0)
		    			ds.getProbeListManager().addObjectAtTop(ds.getMasterTable().createGlobalProbeList(true));	
				}
			}
			DataSetManagerView.getInstance().setSelectedDataSets(results);
		}	
	}
	

}
