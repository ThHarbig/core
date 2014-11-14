package mayday.mpf.importwrapper;

import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.mpf.Applicator;
import mayday.mpf.ExceptionHandler;
import mayday.mpf.FilterBase;
import mayday.mpf.FilterClassList;
import mayday.mpf.FilterOptions;
import mayday.mpf.MaydayDataObject;
import mayday.mpf.options.OptBase;
import mayday.mpf.options.OptExternalized;

/** 
 * @author Florian Battke
 */
public class ImportWrapper {
	
	/* define these */
	protected FilterBase theFilter;
	protected String ModuleName;
	protected String ImporterName;	
	private OptBase filenameoption;
	Exception filterException;
	
	public ImportWrapper(String ModuleName, String ImporterName)
    {
		this.ImporterName = ImporterName;
		this.ModuleName = ModuleName;
	}

	public void prepare() {
		try {
			theFilter = FilterClassList.getInstance().newInstance(ModuleName);
		} catch (Exception e) {
			theFilter = null;
			filterException = e;
		}
	}
	
	public void setFileName(String filename) {
		if (filenameoption!=null)
			filenameoption.ValueFromString(filename);
	}
	
	public void setFileNameOptionIndex(int idx) {
		if (theFilter!=null) {
			FilterOptions opts = theFilter.Options;
			OptBase o = opts.getValues().get(idx);
			while (o instanceof OptExternalized)
				o = ((OptExternalized)o).getOption();
			filenameoption =o;
		}
	}
	
	public class UncheckedMasterTable extends MasterTable {
		public UncheckedMasterTable(DataSet ds) {
			super(ds);
		}
		
		public boolean dirtyhack=true;
		
		public void addProbe(Probe pb) {
			if (dirtyhack) 
				this.setNumberOfExperiments(pb.getNumberOfExperiments());
			super.addProbe(pb);
		}
	}
	
	@SuppressWarnings("deprecation")
	public LinkedList<DataSet> run() {
		
		LinkedList<DataSet> result = new LinkedList<DataSet>();

		if (filterException!=null) {
			ExceptionHandler.handle(new Exception("Can't start import filter: \n"+filterException.getMessage()), (JFrame)null);
			return result;
		}

		// Create a new DataSet with empty mastertable and empty global ProbeList
		DataSet ds = new DataSet("MPF Imported DataSet");
		
		// For now we don't know how many experiments we have so just ignore them
		MasterTable mt = new UncheckedMasterTable(ds);
		
		if (theFilter==null) {
			ExceptionHandler.handle(new Exception(
					"This import plugin can't be initialized. Check the module \n\"" +
					this.ModuleName+ "\" in the Mayday Processing Framework.\n"+filterException.getMessage()), (JFrame)null);
			return result;
		}
		
		// OK, prepare the module and fire up Applicator step 3
		Vector<MaydayDataObject> Input = new Vector<MaydayDataObject>();
		Input.add(new MaydayDataObject(ds));
		Applicator app = new Applicator(mt, theFilter, Input);
		
		JFrame modalParent = Mayday.sharedInstance;
		
		app.showModal(modalParent);
		
		// Collect output and build a nice dataset this time
		
//		List<ProbeList> returnedLists = new ArrayList<ProbeList>();

//		for (MaydayDataObject mdo : app.OutputDataSets) {
//			returnedLists.add(mdo.getProbeList());			
//		}
		
		if (mt.getNumberOfProbes()==0)  // nothing returned
			return result; 
		
		// Make us look almost normal
		if (mt instanceof UncheckedMasterTable) 
			((UncheckedMasterTable)mt).dirtyhack=false;		
		
		// Get a good name for this dataset
		String s = ImporterName;

        ds.setName(s);
        
        for (int i=0; i!=mt.getNumberOfExperiments(); ++i) {
        	if (mt.getExperimentName(i)==null)
        		mt.setExperimentName(i, ""+i);
        }
        
		for (MaydayDataObject mdo : app.OutputDataSets) {
			ProbeList pl = mdo.getProbeList();;
			ds.getProbeListManager().addObjectAtBottom(pl);
			// also: add all probes to their probelists, this has to be done here because
			// the probe lists are not sticky in mpf
			pl.setSticky(true);
		}

        result.add(ds);
        //DataSetManager.singleInstance.addObject(ds);
        //ds.getProbeListManager().addObject(mt.getGlobalProbeList());
        
		
        return result;
	}
	
}
