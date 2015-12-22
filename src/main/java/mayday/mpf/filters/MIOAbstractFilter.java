package mayday.mpf.filters;

import java.util.ArrayList;

import javax.swing.JCheckBox;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptString;

/**
 * This filter removes probes based on MIO content. During execution it opens a popup window where
 * the user can select which MIO group to use. In batchmode, the user can decide to use the same MIO group
 * for all jobs.
 * @author Florian Battke
 */
public abstract class MIOAbstractFilter<T extends MIType> extends FilterBase {

	private ArrayList<MIGroup> selectedGroups = null;
	private boolean reuseGroup = false;
	
	protected OptString MIONames = new OptString("Name(s) of the MIOs to filter on",
			"Supply a comma-separated list of the names of MIOs to filter on. \n" +
			"If the list is empty, or one of the MIO names is not found during execution,\n" +
			"a dialog window for MIO selection will bedisplayed.","");
	protected OptBoolean treatmissing = new OptBoolean("Consider probes without MIO as matching","Select this option to treat probes without the select MIO group as if they were matching the filter",true);
	protected OptDropDown multimatch = new OptDropDown("Combine multiple MIOs using logical ","Select how the filter should work when more than one MIO group\n" +
			"is selected by the user. \n" +
			"AND means that all MIOs have to match the filter, \n" +
			"OR means that at least one of them has to match.",new String[]{"AND","OR"},0);
	protected OptDropDown matchmode = new OptDropDown("Keep probes that","Select whether to keep matching or non-matching probes.",
			new String[]{"match this filter","don't match this filter"},0);
	private static final int COMBINE_AND = 0;
	
	@SuppressWarnings("unchecked")
	private Class myMIOclass;
	
	@SuppressWarnings("unchecked")
	public MIOAbstractFilter(String theName, String theDescription, Class<T> theMIOclass, Class pluginClass) {
		super(1,1);
		
		pli.setName(theName);
		pli.setIdentifier("PAS.mpf.mio."+theName.replace(" ", ""));
		pli.replaceCategory("Filtering");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout(theDescription);
		
		Version = 2; // Version 1 didn't have "AND" matching for multiple MIOs, instead defaulted to "OR". This version defaults to "AND".
		myMIOclass=theMIOclass;
	}
	
	protected void addCommonOptions() {
		// add options for all derived types
		Options.add(MIONames);
		Options.add(treatmissing);
		Options.add(multimatch);
		Options.add(matchmode);
	}

	protected abstract boolean checkMatch(T theMIO); //true if this mio matches
	
	@SuppressWarnings("unchecked")
	public final void execute() throws Exception {
		if (selectedGroups==null || !reuseGroup) {
			getMIOGroups();
		}
		
		int rpCounter = 0;
		
		OutputData[0]=InputData[0];
		
		// go over all probes and check all selected MIOs against the conditions
		
		if (selectedGroups!=null) {
			
			ProgressMeter.initializeStepper(InputData[0].size());
			
			for (Probe pb : OutputData[0]) {
				
				boolean matching = (multimatch.Value==COMBINE_AND); //COMBINE_OR=>false, COMBINE_AND=>true;				
				
				for (MIGroup mg : selectedGroups) {
					T theMIO = (T)mg.getMIO(pb);
					//if (theMIO==null)
						//theMIO = (T)mg.getMIObyPointerMatchingOhManHowThisSucks(pb);
					if (theMIO!=null) {
						if (multimatch.Value==COMBINE_AND)  // all must be true
							matching &= checkMatch(theMIO);
						else
							matching |= checkMatch(theMIO); // at least one must be true 					
					} else {
						if (multimatch.Value==COMBINE_AND)
							matching &= treatmissing.Value;
						else
							matching |= treatmissing.Value; // doesn't make all that much sense, but let users decide
					}
					// speed up
					if (  ((multimatch.Value==COMBINE_AND) && !matching)  // already failed
					   || ((multimatch.Value!=COMBINE_AND) && matching))  //already succeeded
						break;
				}
				
				//invert result if chosen by the user
				if (matchmode.Value==1) matching=!matching;
				
				if (!matching) {
					OutputData[0].remove(pb);
					++rpCounter;
				}
				
				ProgressMeter.stepStepper(1);
				
			}
			
			ProgressMeter.writeLogLine(this.getName() +": " + rpCounter + " probes removed.");
		}		
		
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void getMIOGroups() {
		
		// previously collected only MIO groups in the input data, now we simply take all groups
		MIManager mimanager = InputData[0].getProbeList().getDataSet().getMIManager();		
		
		int groupCount = mimanager.getGroups().size();
		
		selectedGroups = new ArrayList<MIGroup>();
		
		//070724: Use preselected list of MIO names
		if (!MIONames.Value.equals("")) {
			String[] theMIONames = MIONames.Value.split(",");
			boolean allMatched = true;
			for (String MIOName : theMIONames) {
				boolean thisMatched=false;
				MIGroupSelection<MIType> mgs = mimanager.getGroupsForName(MIOName).filterByInterface(myMIOclass);
				if (mgs.size()==1) {
					thisMatched=true;
					selectedGroups.add(mgs.get(0));
					break;
				}
				allMatched &= thisMatched;
				if (!allMatched) break;
			}
			if (allMatched) 
				return;
			ProgressMeter.writeLogLine(this.getName()+": Could not find all specified MIO groups");
		}
		
		//060720: If there is only one matching MIO group, select that one, else show a selection dialog.
		if (groupCount==1) {
			selectedGroups = mimanager.getGroups();
			reuseGroup = false; // perhaps there are several groups for the next job... if not, we do the same thing again ;)
			ProgressMeter.writeLogLine("MIOPresence: Only one MIOGroup found => automatically selected");
		} else 
		if (groupCount>1) {
			MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(mimanager, myMIOclass);
			JCheckBox useForAllJobs = new JCheckBox("<html>Use the selected group for all jobs in batchmode</html>",true);
			mgsd.getAdditionalDialogElementsPanel().add(useForAllJobs);
			mgsd.setDialogDescription("Select one or more MIO Groups for filtering");
			mgsd.setVisible(true);
			MIGroupSelection<MIType> mgs = mgsd.getSelection();
			if (mgs.size()>0) {
				selectedGroups=mgs;
				reuseGroup=useForAllJobs.isSelected();
			} else
				throw new RuntimeException("No MIOs found/selected for filtering.");
		} else if (groupCount==0) 
			throw new RuntimeException("No MIO groups found to allow selection.");
	}
	
	
	


}