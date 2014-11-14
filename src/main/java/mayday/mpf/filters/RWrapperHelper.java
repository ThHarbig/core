package mayday.mpf.filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Preferences;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.probelistmanager.MasterTableProbeList;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.RPlugin;
import mayday.interpreter.rinterpreter.core.ParameterType;
import mayday.interpreter.rinterpreter.core.RSettings;
import mayday.interpreter.rinterpreter.core.RSource;
import mayday.interpreter.rinterpreter.core.RSourceParam;
import mayday.interpreter.rinterpreter.core.RSourcesList;
import mayday.mpf.MaydayDataObject;
import mayday.mpf.ProgressMeter;
import mayday.mpf.importwrapper.ImportWrapper;
import mayday.mpf.options.OptBase;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptFile;
import mayday.mpf.options.OptFiles;
import mayday.mpf.options.OptString;

/* This class contains all R-interpreter-dependent code for RWrapper, so that RWrapper can be initialized without
 * having RInterpreter present (pluma requirement)
 */
/** @author Florian Battke */
public class RWrapperHelper {
	
	RPlugin theRPlugin = null;
	RSourcesList RSources;
	RSource selSource;
	ArrayList<RSourceParam> selParams;
	String[] availableSources;
	RWrapper parent;
	ProgressMeter ProgressMeter;

	public RWrapperHelper() {
		// OK, now let's educate ourselves about the RPlugin
		PluginManager PM = PluginManager.getInstance();
		PluginInfo pli = PM.getPluginFromID("PAS.Rinterpreter");
		if (!pli.hasUnmetDependencies())
			theRPlugin = (RPlugin)pli.getInstance();
		
		// During FilterClassList, this will lead to the filter not appearing in the Filter list.
		if (theRPlugin==null) 
			throw new RuntimeException("R Interpreter plugin not found.\nPlease install the R interpreter plugin to use this module.");
		
		// OK. Now let's get the list of available R scripts 
		// SourceComponent.readSources() is private, so I recreate it in this class
		RSources=readSources();

		if (RSources.size()==0) 
			throw new RuntimeException("Could not find available R functions. Use the R Interpreter plugin to add functions.");

		// Finally, we create the list of usable Scripts as OptDropDown
		availableSources = new String[RSources.size()];
		for (int i=0; i!=RSources.size(); ++i) 
			availableSources[i]=RSources.getSourceAt(i).getDescriptor();
	}
	
	private RSourcesList readSources() {
        Preferences prefs=RDefaults.getPrefs().node(RDefaults.Prefs.SOURCES_NODE);          
        RSourcesList list=new RSourcesList();
        String[] keys;
        keys = prefs.keys();
            
        for(int i=0; i!=keys.length; ++i) {
        	String filename=prefs.get(keys[i],"");
            try {
            	RSource src=new RSource(new File(filename), true);
                list.add(src);
            } catch(FileNotFoundException ex) {
            	 // here I don't respond to the exception because I think taking care of invalid prefs should only be done
            	 // inside the RInterpreter classes
            } catch(Exception ex) {
            	 // we just ignore this source file and don't add it to the list
            }
        }
        return list;
	}
	
	@SuppressWarnings("deprecation")
	public void execute() {
		
		if (theRPlugin==null) throw new RuntimeException("R Interpreter plugin not found.\nPlease install the R interpreter plugin to use this module.");
		
		/* Work Partitioning:
		 * 10% - Option setup
		 * 20% - Building input
		 * 50% - Running R
		 * 20% - Parsing output
		 */ 
		
		this.ProgressMeter.statusChanged(0.0,"Creating R options");

		// feed option values back to RInterpreter instance
		// convert option list to hashtable for fast searching
		TreeMap<String, OptBase> ol = new TreeMap<String, OptBase>();
		for (OptBase ob : parent.Options.getValues()) 
			ol.put(ob.Name, ob);
		
		// option name is the key into the RSourceParameter list
		for (RSourceParam rp : selParams) {
			OptBase thisOption = ol.get(rp.getName());			
			ParameterType pt = rp.getType();			
			if (pt==null) pt = ParameterType.KNOWN_TYPES[ParameterType.DEFAULT];			
			if (rp.getName().equals("DATA")) continue; //not to be edited by user			
			switch(pt.editor()) {
			case ParameterType.COMBOBOX:
				OptDropDown odd = (OptDropDown)thisOption;
				rp.setValue(pt.values()[odd.Value]);
				break;
			case ParameterType.TEXTFIELD:
				OptString os = (OptString)thisOption;
				rp.setValue(os.Value);
				break;
            case ParameterType.FILECHOOSER:
                OptFile of = (OptFile)thisOption;
                rp.setValue(of.Value);
                break;
            case ParameterType.FILELIST:
                OptFiles ofl = (OptFiles)thisOption;
                rp.setValue("\"" + ofl.ValueToString()+ "\"");
                break;			}
		}
		
		this.ProgressMeter.statusChanged(0.1,"Creating input");

		// Create Settings
		RSettings selSettings = RSettings.createInitializedInstance(selSource);
		selSettings.setDeleteInputFiles(RSettings.DEL_YES);  // Tempfiles only needed for debugging R scripts
		selSettings.setDeleteOutputFiles(RSettings.DEL_YES);
		selSettings.silentRunning=true;
		
		// Run instance with given InputData
		StringBuffer returnedWarnings = new StringBuffer();
		MasterTable realMT = parent.InputData[0].getProbeList().getDataSet().getMasterTable();
		List<ProbeList> returnedProbeLists;
		
		// Create a fake mastertable for those plugins that don't care about probelists and simply use the 
		// mastertable as their input probelist
		MasterTable fakeMT = new MasterTable(new DataSet());
		fakeMT.setNumberOfExperiments(realMT.getNumberOfExperiments());
		for (int i=0; i!=realMT.getNumberOfExperiments(); ++i)
			fakeMT.setExperimentName(i,realMT.getExperimentName(i));
		
		// FROM HERE ON: When an exception occurs, I have to destroy the fake mastertable by hand!
		
		// The fake name is used to later find out which probe a returned probe inherits from. We use this to move
		// MIOs "around" the R plugin. MIOs are NOT handed over to the RPlugin (it's not using them at the moment
		// and any changes the the R function performs on a probes MIO complement are discarded. 
		String fakeName = "~~TAG~~" + new Random().nextInt();
		
		LinkedList<ProbeList> convertedInput = new LinkedList<ProbeList>();

		// Copy all input probes into the fake mastertable
		for (MaydayDataObject mdo : parent.InputData) { 
			ProbeList pl = new ProbeList(fakeMT.getDataSet(), true);
			convertedInput.add(pl);
			for (Probe pb : mdo) {
				Probe pbclone = (Probe)pb.clone();
				pbclone.setName(pb.getName()+fakeName);
				if (!fakeMT.getProbes().containsKey(pbclone.getName())) {
					fakeMT.addProbe(pbclone);
				}
				pl.addProbe(pbclone);
			}
		}
		
		// FROM HERE ON: When an exception occurs, I have to destroy all input MDOs and the cloned probes!
		
		this.ProgressMeter.statusChanged(0.3,"Running R job");

		boolean calledFromImporter = (parent.InputData[0].getProbeList().getDataSet().getMasterTable()
									 instanceof ImportWrapper.UncheckedMasterTable);
		
		// Exceptions created by the R plugin are simply passed along to the calling Applicator instance
		// to be displayed in the log. 
		
		try {
			returnedProbeLists = 
				theRPlugin.runInternal( 
						selSettings, convertedInput, fakeMT, returnedWarnings
				);	
			} catch (Exception e) {
				properCleanup(fakeMT, convertedInput);
				throw new RuntimeException("The R plugin failed with an exception:\n"+e.getMessage());
		}
		
		this.ProgressMeter.statusChanged(0.8,"Integrating R output");
		
		// If a warning message is created, it will be logged 
		if (returnedWarnings.length()!=0)
			ProgressMeter.writeLogLine("The R plugin returned warning messages:\n"+returnedWarnings);

		if (returnedProbeLists == null) { //when a new dataset is created, the RPlugin sets returnedList=null, don't ask me why.
			DataSet returnedDataSet = selSettings.getMasterTable().getDataSet();
			if (selSettings.getMasterTable() != realMT) {
				returnedProbeLists = new LinkedList<ProbeList>();
				for (Object pl : returnedDataSet.getProbeListManager().getObjects())
					if (calledFromImporter || !(pl instanceof MasterTableProbeList))  // to-do: explain this for other developers
						returnedProbeLists.add((ProbeList)pl);
			}
		}
		
		// Retrieve outputdata
		if (returnedProbeLists == null) { //create a new list, warnings will come in just a moment
			returnedProbeLists = new LinkedList<ProbeList>();
		}
		
		// We can only handle the output if it has the same dimension as the input dataset		
		if (!calledFromImporter) { // abstractimporter has to work ;)
			if (selSettings.getMasterTable().getNumberOfExperiments()!=parent.InputData[0].getNumberOfExperiments()) {
				properCleanup(fakeMT, convertedInput);
				throw new RuntimeException("The R function \""+selSource.getDescriptor()+"\" returned data whose number of experiments differs from\n" +
					                   	"the number of experiments in the input data set. Please apply this R function manually.");
			}
		}
		else  
		{
			// If this is called from importwrapper, we may set experiment names
			parent.InputData[0].getProbeList().getDataSet().getMasterTable().setNumberOfExperiments(
					selSettings.getMasterTable().getNumberOfExperiments()
					);
			for (int i=0; i!=selSettings.getMasterTable().getNumberOfExperiments(); ++i) {
				parent.InputData[0].getProbeList().getDataSet().getMasterTable().setExperimentName(
						i, 
						selSettings.getMasterTable().getExperimentName(i)
				);
			}

		}
		
		// Do we have the right number of outputs? If not, create some empty lists or discard some outputs
		if (parent.getOutputSize()>returnedProbeLists.size()) {
			this.ProgressMeter.writeLogLine(
					"Warning: R returned only "+returnedProbeLists.size() +
					" probe lists when "+parent.getOutputSize()+" probe lists were expected. " +
					"Substituting empty probe lists for missing output.");
			DataSet ds = returnedProbeLists.size()>0 ? returnedProbeLists.get(0).getDataSet() : parent.InputData[0].getProbeList().getDataSet();
			while (parent.getOutputSize()>returnedProbeLists.size()) {
				returnedProbeLists.add(new ProbeList(ds,true));  // don't care about stickyness
			}
		} else if (parent.getOutputSize()<returnedProbeLists.size()) {
			// discard overhanging output
			parent.ProgressMeter.writeLogLine(
					"Warning: R returned "+returnedProbeLists.size() +
					" probe lists when only "+parent.getOutputSize()+" probe lists were expected. " +
					"Discarding "+ (returnedProbeLists.size()-parent.getOutputSize()) + " probe lists.");			
			while (parent.getOutputSize()<returnedProbeLists.size()) 
				returnedProbeLists.remove(returnedProbeLists.size()-1);
		}
		
		// We have made sure that we have the expected number of returned probelists, we know that we can integrate them into
		// our input dataset. Now comes the hard part.

		HashMap<MIGroup, MIGroup> groupMapping = new HashMap<MIGroup, MIGroup>();

		// for each probelist
		for (int i=0; i!=parent.getOutputSize(); ++i) {
			
			ProbeList src = returnedProbeLists.get(i);
			ProbeList tgtl = new ProbeList(parent.InputData[0].getProbeList().getDataSet(),parent.InputData[0].getProbeList().isSticky());
			tgtl.setName(parent.InputData[0].getName());
			MaydayDataObject tgt = new MaydayDataObject(tgtl);
			
			// for each probe, if it isn't already in the old dataset, create a new probe
			for (Probe pb: src.getAllProbes()) {
				Probe candidate = null;
				// try to find this probe's ancestor
				String pbName = pb.getName();
				if (pbName.endsWith(fakeName)) {
					String pbRealName = pbName.substring(0, pbName.length()-fakeName.length());
					candidate = realMT.getProbe(pbRealName);
				}
				// add the new probe or change the ancestral one
				if (candidate!=null) {
					candidate = tgt.cloneProbe(candidate); // clones all ancestral MIOs, we will add the new MIOs later
					tgt.replaceValues(candidate, pb);
				} else {
					// no ancestor, create new probe, clone mios and migroups
					candidate = new Probe(realMT);
					candidate.setValues(Arrays.copyOf(pb.getValues(),pb.getValues().length));
					candidate.setName(pb.getName());
					// add with a new unique name etc
					tgt.add(candidate);
				}
				// clone mios
				for (MIGroup mg : src.getDataSet().getMIManager().getGroupsForObject(pb)) {
					if (mg.getMIManager()==tgtl.getDataSet().getMIManager())
						continue; // this is already cloned
					MIGroup targetMG = groupMapping.get(mg);					
					if (targetMG==null) {
						targetMG = tgtl.getDataSet().getMIManager().newGroup(mg.getMIOType(), mg.getName(), mg.getPath());
						groupMapping.put(mg,targetMG);
					}
					MIType oldMIO = mg.getMIO(pb);
					targetMG.add(candidate,oldMIO.clone());
				}
			}
			
			// finally, add the new ProbeList to our output
			parent.OutputData[i] = tgt;
		}

		
		this.ProgressMeter.statusChanged(0.95,"Cleaning up");
		
		// If I am able to finish everything without exceptions then I still have to remove the input data from memory
		properCleanup(fakeMT, convertedInput);
		
		this.ProgressMeter.statusChanged(1.0,null);
	}
	
	private void properCleanup(MasterTable mt, LinkedList<ProbeList> convinp) {
		// first remove all input data from memory
		for (int i=0; i!=parent.getInputSize(); ++i) 
			parent.InputData[0].dismiss();
		// remove converted input probelists with fake name probes
		for (ProbeList pl : convinp) 
			pl.clearProbes();
		// now remove the fake mastertable and all its probes
		mt.clear();
	}
	
	public void createOptionList(Vector<OptBase> ROptions, int selectedItemIndex) {
		// get options for the selected R function
	    selSource = RSources.getSourceAt(selectedItemIndex);
		selParams = selSource.getParameters();
		ROptions.clear();
		// create OptBase decendants for the function		
		for (RSourceParam rp : selParams) {
			ParameterType pt = rp.getType();
			
			if (pt==null) pt = ParameterType.KNOWN_TYPES[ParameterType.DEFAULT];
			
			if (rp.getName().equals("DATA")) continue; //not to be edited by user
			
			switch(pt.editor()) {
			case ParameterType.COMBOBOX:
				int defaultIndex=0;
				if (rp.getDefault()!=null) defaultIndex=java.util.Arrays.asList(pt.values()).indexOf(rp.getDefault());
				if (defaultIndex==-1) defaultIndex=0;
				ROptions.add(
						new OptDropDown(
								rp.getName(),
								rp.getDescription(),
								pt.values(),
								defaultIndex
						));
				break;
			case ParameterType.TEXTFIELD:
				ROptions.add(
						new OptString(
								rp.getName(),
								rp.getDescription(),
								rp.getDefault()
						));
				break;
            case ParameterType.FILECHOOSER:
                ROptions.add(
                        new OptFile(
                        		rp.getName(),
                        		rp.getDescription(),
                        		rp.getDescription()
                        ));
                break;
            case ParameterType.FILELIST:
            	ROptions.add(
            			new OptFiles(
            					rp.getName(),
            					rp.getDescription(),
            					rp.getDefault()
            			));
            	break;			
			}
		}
		// add Options to the Option List
		for (OptBase ob : ROptions) ob.setVisible(true);
	}
	
}
