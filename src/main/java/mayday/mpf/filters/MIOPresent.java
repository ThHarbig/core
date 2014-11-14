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
import mayday.mpf.options.OptDropDown;

/**
 * This filter removes probes based on whether a specific MIO exists for them. During execution it opens a popup window where
 * the user can select which MIO group to use. In batchmode, the user can decide to use the same MIO group
 * for all jobs.
 * @author Florian Battke
 */
public class MIOPresent extends FilterBase {

/* This class doesn't inherit from MIOAbstractFilter since it is too specific */
	
	private ArrayList<MIGroup> selectedGroups = null;
	private boolean reuseGroup = false;
	private OptDropDown matchmode = new OptDropDown("Keep probes that","Select whether to keep matching or non-matching probes.",
			new String[]{"contain the selected MIO group(s)","don't contain the selected MIO group(s)"},0);
	protected OptDropDown multimatch = new OptDropDown("Combine multiple MIOs using logical ","Select how the filter should work when more than one MIO group\n" +
			"is selected by the user. \n" +
			"AND means that all MIOs have to match the filter, \n" +
			"OR means that at least one of them has to match.",new String[]{"AND","OR"},0);
	private static final int COMBINE_AND = 0;

	
	public MIOPresent() {
		super(1,1);
		
		pli.setName("Meta Information Presence Filter");
		pli.setIdentifier("PAS.mpf.mio.presences");
		pli.replaceCategory("Filtering");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Removes probes based on whether a certain meta information object group is attached to them." +				
				"May require user interaction during execution (selection of a MIO Group).");
		
		Version = 2; // Version 1 didn't have "AND" matching for multiple MIOs, instead defaulted to "OR". This version defaults to "AND".

		Options.add(matchmode);
		Options.add(multimatch);
	}

	public void execute() throws Exception {
		if (selectedGroups==null || !reuseGroup) {
			getMIOGroups();
		}
		
		int rpCounter = 0;
		
		OutputData[0]=InputData[0];
		
		// go over all probes and check all selected StringMIOs against the regex
		
		if (selectedGroups!=null) {
			for (Probe pb : OutputData[0]) {
				
				boolean matching = (multimatch.Value==COMBINE_AND); //COMBINE_OR=>false, COMBINE_AND=>true;
				
				for (MIGroup mg : selectedGroups) {
					MIType dm = mg.getMIO(pb);
					if (multimatch.Value==COMBINE_AND)
						matching &= (dm!=null);
					else
						matching |= (dm!=null);
				}
				
				if (matchmode.Value==1) matching=!matching;
				
				if (!matching) {
					OutputData[0].remove(pb);
					++rpCounter;
				}
			}		
			ProgressMeter.writeLogLine(this.getName()+": " +rpCounter + " probes removed.");
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	private void getMIOGroups() {
		// previously collected only MIO groups in the input data, now we simply take all groups
		MIManager mimanager = InputData[0].getProbeList().getDataSet().getMIManager();		
		
		int groupCount = mimanager.getGroups().size();
		
		//060720: If there is only one matching MIO group, select that one, else show a selection dialog.
		if (groupCount==1) {
			selectedGroups = mimanager.getGroups();
			reuseGroup = false; // perhaps there are several groups for the next job... if not, we do the same thing again ;)
			ProgressMeter.writeLogLine("MIOPresence: Only one MIOGroup found => automatically selected");
		} else 
		if (groupCount>1) {
			MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(mimanager);
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